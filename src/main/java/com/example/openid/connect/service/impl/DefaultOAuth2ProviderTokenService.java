package com.example.openid.connect.service.impl;

import com.example.oauth2.clientdetails.MongoClientDetailsService;
import com.example.oauth2.userdetails.UserPressDetailsService;
import com.example.oauth2.userdetails.model.UserInfo;
import com.example.openid.connect.service.OAuth2TokenEntityService;
import com.example.openid.connect.token.OAuth2AccessTokenEntity;
import com.example.openid.connect.token.OAuth2RefreshTokenEntity;
import com.example.openid.connect.token.keypair.OIDCKeyPairGenerator;
import com.example.openid.connect.token.model.AuthenticationHolderEntity;
import com.example.openid.connect.token.model.PKCEAlgorithm;
import com.example.utils.IdTokenHashUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * DefaultOAuth2ProviderTokenService
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class DefaultOAuth2ProviderTokenService implements OAuth2TokenEntityService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultOAuth2ProviderTokenService.class);
    @Autowired
    private MongoClientDetailsService clientDetailsService;
    @Value("${westar.token.validity-seconds}")
    private Integer tokenValiditySeconds;
    @Value("${westar.refresh-token.validity-seconds}")
    private Integer refreshTokenValiditySeconds;
    @Autowired
    private OIDCKeyPairGenerator oidcKeyPairGenerator;
    @Autowired
    private UserPressDetailsService userPressDetailsService;

    @Override
    public OAuth2AccessTokenEntity createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        if (authentication != null && authentication.getOAuth2Request() != null) {
            // look up our client
            OAuth2Request request = authentication.getOAuth2Request();

            ClientDetails client = clientDetailsService.loadClientByClientId(request.getClientId());

            if (client == null) {
                throw new InvalidClientException("Client not found: " + request.getClientId());
            }

            // handle the PKCE code challenge if present
            if (request.getExtensions().containsKey("code_challenge")) {
                String challenge = (String) request.getExtensions().get("code_challenge");
                PKCEAlgorithm alg = PKCEAlgorithm.parse((String) request.getExtensions().get("code_challenge_method"));

                String verifier = request.getRequestParameters().get("code_verifier");

                if (alg.equals(PKCEAlgorithm.plain)) {
                    // do a direct string comparison
                    if (!challenge.equals(verifier)) {
                        throw new InvalidRequestException("Code challenge and verifier do not match");
                    }
                } else if (alg.equals(PKCEAlgorithm.S256)) {
                    // hash the verifier
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        String hash = Base64URL.encode(digest.digest(verifier.getBytes(StandardCharsets.US_ASCII))).toString();
                        if (!challenge.equals(hash)) {
                            throw new InvalidRequestException("Code challenge and verifier do not match");
                        }
                    } catch (NoSuchAlgorithmException e) {
                        logger.error("Unknown algorithm for PKCE digest", e);
                    }
                }

            }


            OAuth2AccessTokenEntity token = new OAuth2AccessTokenEntity();//accessTokenFactory.createNewAccessToken();

            // attach the client
            token.setClient(client);
            token.setScope(request.getScope());

            Date expiration = new Date(System.currentTimeMillis() + (tokenValiditySeconds * 1000L));
            if (client.getAccessTokenValiditySeconds() != null && client.getAccessTokenValiditySeconds() > 0) {
                expiration = new Date(System.currentTimeMillis() + (client.getAccessTokenValiditySeconds() * 1000L));
            }
            token.setExpiration(expiration);

            // attach the authorization so that we can look it up later
            AuthenticationHolderEntity authHolder = new AuthenticationHolderEntity();
            authHolder.setAuthentication(authentication);
            token.setAuthenticationHolder(authHolder);

            if (token.getRefreshToken() == null) {
                OAuth2RefreshTokenEntity savedRefreshToken = createRefreshToken(client, authHolder);

                token.setRefreshToken(savedRefreshToken);
            }

            OAuth2AccessTokenEntity enhancedToken = (OAuth2AccessTokenEntity) enhance(token, authentication);

            //可存储各种token

            return enhancedToken;
        }

        throw new AuthenticationCredentialsNotFoundException("No authentication credentials found");
    }

    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        OAuth2AccessTokenEntity token = (OAuth2AccessTokenEntity) accessToken;
        OAuth2Request originalAuthRequest = authentication.getOAuth2Request();

        String clientId = originalAuthRequest.getClientId();
        ClientDetails client = clientDetailsService.loadClientByClientId(clientId);

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .claim("azp", clientId)
                .issuer("http://wwww.wang.com")
                .issueTime(new Date())
                .expirationTime(token.getExpiration())
                .subject(authentication.getName())
                .jwtID(UUID.randomUUID().toString()); // set a random NONCE in the middle of it

        String audience = (String) authentication.getOAuth2Request().getExtensions().get("aud");
        if (!Strings.isNullOrEmpty(audience)) {
            builder.audience(Lists.newArrayList(audience));
        }

        JWTClaimsSet claims = builder.build();

        JWSAlgorithm signingAlg = JWSAlgorithm.parse("RS256");
        JWSHeader header = new JWSHeader(signingAlg, new JOSEObjectType("JWT"),null, null, null, null, null, null, null, null, null, null, null);
        SignedJWT signed = new SignedJWT(header, claims);
        KeyPair kp = oidcKeyPairGenerator.getDefaultKeyPair();
        JWSSigner signer = new RSASSASigner((RSAPrivateKey) (kp.getPrivate()));

        try {
            signed.sign(signer);
        } catch (JOSEException e) {

            logger.error("Failed to sign JWT, error was: ", e);
        }

        token.setJwt(signed);

        /**
         * Authorization request scope MUST include "openid" in OIDC, but access token request
         * may or may not include the scope parameter. As long as the AuthorizationRequest
         * has the proper scope, we can consider this a valid OpenID Connect request. Otherwise,
         * we consider it to be a vanilla OAuth2 request.
         *
         * Also, there must be a user authentication involved in the request for it to be considered
         * OIDC and not OAuth, so we check for that as well.
         */
        if (originalAuthRequest.getScope().contains("openid") && !authentication.isClientOnly()) {
            String username = authentication.getName();
//      一般在此会从数据库中查询出用户信息
            UserInfo userInfo = userPressDetailsService.getUserByUsername(username);
            if (userInfo != null) {

                OAuth2AccessTokenEntity idTokenEntity = createIdToken(client, originalAuthRequest, claims.getIssueTime(), userInfo, token);

                // attach the id token to the parent access token
                token.setIdToken(idTokenEntity);
            } else {
                // can't create an id token if we can't find the user
                logger.warn("Request for ID token when no user is present.");
            }

        }

        return token;
    }

    private OAuth2AccessTokenEntity createIdToken(ClientDetails client, OAuth2Request request, Date issueTime, UserInfo userInfo, OAuth2AccessTokenEntity accessToken) {
        JWSAlgorithm signingAlg = JWSAlgorithm.parse("RS256");

        OAuth2AccessTokenEntity idTokenEntity = new OAuth2AccessTokenEntity();
        JWTClaimsSet.Builder idClaims = new JWTClaimsSet.Builder();

        // if the auth time claim was explicitly requested OR if the client always wants the auth time, put it in
        if (request.getExtensions().containsKey("max_age") || (request.getExtensions().containsKey("idtoken"))) {

            if (request.getExtensions().get("AUTH_TIMESTAMP") != null) {

                Long authTimestamp = Long.parseLong((String) request.getExtensions().get("AUTH_TIMESTAMP"));
                if (authTimestamp != null) {
                    idClaims.claim("auth_time", authTimestamp / 1000L);
                }
            } else {
                // we couldn't find the timestamp!
                logger.warn("Unable to find authentication timestamp! There is likely something wrong with the configuration.");
            }
        }

        idClaims.issueTime(issueTime);
        Date expiration = new Date(System.currentTimeMillis() + (tokenValiditySeconds * 1000L));
        if (client.getAccessTokenValiditySeconds() != null && client.getAccessTokenValiditySeconds() > 0) {
            expiration = new Date(System.currentTimeMillis() + (client.getAccessTokenValiditySeconds() * 1000L));
        }
        idClaims.expirationTime(expiration);
        idTokenEntity.setExpiration(expiration);
        idClaims.issuer("http://wwww.wang.com");
        idClaims.subject(userInfo.getSub());
        idClaims.audience(Lists.newArrayList(client.getClientId()));
        idClaims.claim("user_name", userInfo.getSub());
        idClaims.jwtID(UUID.randomUUID().toString()); // set a random NONCE in the middle of it

        String nonce = (String) request.getExtensions().get("nonce");
        if (!Strings.isNullOrEmpty(nonce)) {
            idClaims.claim("nonce", nonce);
        }

        Set<String> responseTypes = request.getResponseTypes();

        if (responseTypes.contains("token")) {
            // calculate the token hash
            Base64URL at_hash = IdTokenHashUtils.getAccessTokenHash(signingAlg, accessToken);
            idClaims.claim("at_hash", at_hash);
        }
        String type = request.getGrantType();

        JWT idToken;

        if (signingAlg.equals(Algorithm.NONE)) {
            // unsigned ID token
            idToken = new PlainJWT(idClaims.build());

        } else {

            // signed ID token
            KeyPair kp = oidcKeyPairGenerator.getDefaultKeyPair();
            JWSSigner signer = new RSASSASigner((RSAPrivateKey) (kp.getPrivate()));

            JWSHeader header = new JWSHeader(signingAlg, new JOSEObjectType("JWT"),null, null, null, null, null, null, null, null, null, null, null);
            SignedJWT signedJWT = new SignedJWT(header, idClaims.build());
            try {
                signedJWT.sign(signer);
            } catch (JOSEException e) {
                e.printStackTrace();
            }
            idToken = signedJWT;
        }


        idTokenEntity.setJwt(idToken);

        idTokenEntity.setAuthenticationHolder(accessToken.getAuthenticationHolder());

        // create a scope set with just the special "id-token" scope
        Set<String> idScopes = Sets.newHashSet("id-token");
        idTokenEntity.setScope(idScopes);

        idTokenEntity.setClient(accessToken.getClient());

        return idTokenEntity;

    }

    private OAuth2RefreshTokenEntity createRefreshToken(ClientDetails client, AuthenticationHolderEntity authHolder) {
        OAuth2RefreshTokenEntity refreshToken = new OAuth2RefreshTokenEntity(); //refreshTokenFactory.createNewRefreshToken();
        JWTClaimsSet.Builder refreshClaims = new JWTClaimsSet.Builder();

        // make it expire if necessary
        Date expiration = new Date(System.currentTimeMillis() + (refreshTokenValiditySeconds * 1000L));
        if (client.getRefreshTokenValiditySeconds() != null) {
            expiration = new Date(System.currentTimeMillis() + (client.getRefreshTokenValiditySeconds() * 1000L));
        }
        refreshToken.setExpiration(expiration);
        refreshClaims.expirationTime(expiration);
        // set a random identifier
        refreshClaims.jwtID(UUID.randomUUID().toString());

        // TODO: add issuer fields, signature to JWT
        PlainJWT refreshJwt = new PlainJWT(refreshClaims.build());
        refreshToken.setJwt(refreshJwt);

        //Add the authentication
        refreshToken.setAuthenticationHolder(authHolder);
        refreshToken.setClient(client);

        return refreshToken;
    }

    protected int getRefreshTokenValiditySeconds(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getRefreshTokenValiditySeconds();
            if (validity != null) {
                return validity.intValue();
            }
        }

        return 2591000;
    }

    protected boolean isSupportRefreshToken(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            return client.getAuthorizedGrantTypes().contains("refresh_token");
        } else {
            return false;
        }
    }

    @Override
    public OAuth2AccessTokenEntity refreshAccessToken(String refreshTokenValue, TokenRequest authRequest) throws AuthenticationException {
//        OAuth2RefreshTokenEntity refreshToken = clearExpiredRefreshToken(tokenRepository.getRefreshTokenByValue(refreshTokenValue));
//
//        if (refreshToken == null) {
//            throw new InvalidTokenException("Invalid refresh token: " + refreshTokenValue);
//        }
//
//        ClientDetailsEntity client = refreshToken.getClient();
//
//        AuthenticationHolderEntity authHolder = refreshToken.getAuthenticationHolder();
//
//        // make sure that the client requesting the token is the one who owns the refresh token
//        ClientDetailsEntity requestingClient = clientDetailsService.loadClientByClientId(authRequest.getClientId());
//        if (!client.getClientId().equals(requestingClient.getClientId())) {
//            tokenRepository.removeRefreshToken(refreshToken);
//            throw new InvalidClientException("Client does not own the presented refresh token");
//        }
//
//        //Make sure this client allows access token refreshing
//        if (!client.isAllowRefresh()) {
//            throw new InvalidClientException("Client does not allow refreshing access token!");
//        }
//
//        // clear out any access tokens
//        if (client.isClearAccessTokensOnRefresh()) {
//            tokenRepository.clearAccessTokensForRefreshToken(refreshToken);
//        }
//
//        if (refreshToken.isExpired()) {
//            tokenRepository.removeRefreshToken(refreshToken);
//            throw new InvalidTokenException("Expired refresh token: " + refreshTokenValue);
//        }
//
//        OAuth2AccessTokenEntity token = new OAuth2AccessTokenEntity();
//
//        // get the stored scopes from the authentication holder's authorization request; these are the scopes associated with the refresh token
//        Set<String> refreshScopesRequested = new HashSet<>(refreshToken.getAuthenticationHolder().getAuthentication().getOAuth2Request().getScope());
//        Set<SystemScope> refreshScopes = scopeService.fromStrings(refreshScopesRequested);
//        // remove any of the special system scopes
//        refreshScopes = scopeService.removeReservedScopes(refreshScopes);
//
//        Set<String> scopeRequested = authRequest.getScope() == null ? new HashSet<String>() : new HashSet<>(authRequest.getScope());
//        Set<SystemScope> scope = scopeService.fromStrings(scopeRequested);
//
//        // remove any of the special system scopes
//        scope = scopeService.removeReservedScopes(scope);
//
//        if (scope != null && !scope.isEmpty()) {
//            // ensure a proper subset of scopes
//            if (refreshScopes != null && refreshScopes.containsAll(scope)) {
//                // set the scope of the new access token if requested
//                token.setScope(scopeService.toStrings(scope));
//            } else {
//                String errorMsg = "Up-scoping is not allowed.";
//                logger.error(errorMsg);
//                throw new InvalidScopeException(errorMsg);
//            }
//        } else {
//            // otherwise inherit the scope of the refresh token (if it's there -- this can return a null scope set)
//            token.setScope(scopeService.toStrings(refreshScopes));
//        }
//
//        token.setClient(client);
//
//        if (client.getAccessTokenValiditySeconds() != null) {
//            Date expiration = new Date(System.currentTimeMillis() + (client.getAccessTokenValiditySeconds() * 1000L));
//            token.setExpiration(expiration);
//        }
//
//        if (client.isReuseRefreshToken()) {
//            // if the client re-uses refresh tokens, do that
//            token.setRefreshToken(refreshToken);
//        } else {
//            // otherwise, make a new refresh token
//            OAuth2RefreshTokenEntity newRefresh = createRefreshToken(client, authHolder);
//            token.setRefreshToken(newRefresh);
//
//            // clean up the old refresh token
//            tokenRepository.removeRefreshToken(refreshToken);
//        }
//
//        token.setAuthenticationHolder(authHolder);
//
//        tokenEnhancer.enhance(token, authHolder.getAuthentication());
//
//        tokenRepository.saveAccessToken(token);
//
//        return token;
        return null;
    }

    @Override
    public OAuth2AccessTokenEntity getAccessToken(OAuth2Authentication oAuth2Authentication) {
        throw new UnsupportedOperationException("Unable to look up access token from authentication object.");
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
//        OAuth2AccessTokenEntity accessToken = clearExpiredAccessToken(tokenRepository.getAccessTokenByValue(accessTokenValue));
//
//        if (accessToken == null) {
//            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
//        } else {
//            return accessToken.getAuthenticationHolder().getAuthentication();
//        }
        return null;
    }

    @Override
    public OAuth2AccessTokenEntity readAccessToken(String accessTokenValue) {
//        OAuth2AccessTokenEntity accessToken = clearExpiredAccessToken(tokenRepository.getAccessTokenByValue(accessTokenValue));
//        if (accessToken == null) {
//            throw new InvalidTokenException("Access token for value " + accessTokenValue + " was not found");
//        } else {
//            return accessToken;
//        }
        return null;
    }
}
