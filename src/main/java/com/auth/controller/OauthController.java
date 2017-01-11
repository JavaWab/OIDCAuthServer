package com.auth.controller;

import com.auth.controller.vo.TokenVO;
import com.auth.oauth2.clientdetails.MongoClientDetailsService;
import com.auth.openid.connect.service.impl.DefaultOAuth2ProviderTokenService;
import com.auth.openid.connect.service.impl.DefaultOIDCAuthorizationCodeServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;

/**
 * OauthController
 *
 * @author Anbang Wang
 * @date 2017/1/5
 */
@Controller
@RequestMapping(value = "/oidc")
public class OauthController implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(OauthController.class);

    private RedirectResolver redirectResolver = new DefaultRedirectResolver();
    private UserApprovalHandler userApprovalHandler = new DefaultUserApprovalHandler();
    private OAuth2RequestValidator oauth2RequestValidator = new DefaultOAuth2RequestValidator();
    private String userApprovalPage = "forward:/oidc/confirm_access";
    private String errorPage = "forward:/oauth/error";
    private Object implicitLock = new Object();

    @Autowired
    private MongoClientDetailsService clientDetailsService;
    @Autowired
    private DefaultOIDCAuthorizationCodeServices defaultOIDCAuthorizationCodeServices;
    @Autowired
    private DefaultOAuth2ProviderTokenService defaultOAuth2ProviderTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;
    private OAuth2RequestFactory oAuth2RequestFactory;

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }

        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }

        model.setViewName("login");
        return model;
    }

    /* -------------------------------------- ^^User Endpoint Login^^ --------------------------------------*/
    @RequestMapping(value = "confirm_access")
    public ModelAndView confirmAccess(@RequestParam Map<String, Object> model) {
        ModelAndView result = new ModelAndView();
        if (model.isEmpty() || !model.containsKey("response_type") || !model.containsKey("client_id") || !model.containsKey("redirect_uri")){
            result.setViewName("login");
        } else {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(model.get("client_id").toString());
            model.put("scopes", clientDetails.getScope());
            result.addAllObjects(model);
            result.setViewName("confirm");
        }
        return result;
    }

    /**
     * Token EndPoint
     *
     * @param tokenVO
     * @return
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public OAuth2AccessToken getAccessToken(@ModelAttribute TokenVO tokenVO) {
        String client_id = tokenVO.getClient_id();
        Map<String, String> parameters = tokenVO.toMap();
        ClientDetails authenticatedClient = this.clientDetailsService.loadClientByClientId(client_id);

        if (authenticatedClient == null) {
            throw new InvalidClientException("Client not found: " + client_id);
        } else {
            String secret = authenticatedClient.getClientSecret();
            if (!secret.equals(tokenVO.getClient_secret())) {
                throw new InvalidClientException("invalid confidential client: " + client_id);
            }
        }
        TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(parameters, authenticatedClient);
        new DefaultOAuth2RequestValidator().validateScope(tokenRequest, authenticatedClient);
        String grantType = tokenRequest.getGrantType();
        if (!StringUtils.hasText(grantType)) {
            throw new InvalidRequestException("Missing grant type");
        } else if (grantType.equals("implicit")) {
            throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
        } else {
            if (this.isAuthCodeRequest(parameters) && !tokenRequest.getScope().isEmpty()) {
                LOG.debug("Clearing scope of incoming token request");
                tokenRequest.setScope(Collections.<String>emptySet());
            }

            if (this.isRefreshTokenRequest(parameters)) {
                tokenRequest.setScope(OAuth2Utils.parseParameterList((String) parameters.get("scope")));
            }

            OAuth2AccessToken token = this.getTokenGranter(grantType).grant(grantType, tokenRequest);
            if (token == null) {
                throw new UnsupportedGrantTypeException("Unsupported grant type: " + grantType);
            } else {
                return token;
            }
        }
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
    }

    protected TokenGranter getTokenGranter(String grantType) {
        if ("authorization_code".equals(grantType)) {
            return new AuthorizationCodeTokenGranter(this.defaultOAuth2ProviderTokenService, this.defaultOIDCAuthorizationCodeServices, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("password".equals(grantType)) {
            return new ResourceOwnerPasswordTokenGranter(this.authenticationManager, this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("refresh_token".equals(grantType)) {
            return new RefreshTokenGranter(this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("client_credentials".equals(grantType)) {
            return new ClientCredentialsTokenGranter(this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("implicit".equals(grantType)) {
            return new ImplicitTokenGranter(this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else {
            throw new UnsupportedGrantTypeException("Unsupport grant_type: " + grantType);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(this.clientDetailsService != null, "ClientDetailsService must be provided");
        this.oAuth2RequestFactory = new DefaultOAuth2RequestFactory(this.clientDetailsService);
    }
/* -------------------------------------- ^^Token Endpoint Request^^ --------------------------------------*/

        /**
     * Authorize Endpoint
     *
     * @param model
     * @param parameters
     * @param sessionStatus
     * @param principal
     * @return
     */
    @RequestMapping({"/authorize"})
    public ModelAndView authorize(Map<String, Object> model, @RequestParam Map<String, String> parameters, SessionStatus sessionStatus, Principal principal) {
        AuthorizationRequest authorizationRequest = this.oAuth2RequestFactory.createAuthorizationRequest(parameters);
        Set responseTypes = authorizationRequest.getResponseTypes();
        if (!responseTypes.contains("token") && !responseTypes.contains("code")) {
            throw new UnsupportedResponseTypeException("Unsupported response types: " + responseTypes);
        } else if (authorizationRequest.getClientId() == null) {
            throw new InvalidClientException("A client id must be provided");
        } else {
            try {
                if (principal instanceof Authentication && ((Authentication) principal).isAuthenticated()) {
                    ClientDetails e = this.clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
                    String redirectUriParameter = (String) authorizationRequest.getRequestParameters().get("redirect_uri");
                    String resolvedRedirect = this.redirectResolver.resolveRedirect(redirectUriParameter, e);
                    if (!StringUtils.hasText(resolvedRedirect)) {
                        throw new RedirectMismatchException("A redirectUri must be either supplied or preconfigured in the ClientDetails");
                    } else {
                        authorizationRequest.setRedirectUri(resolvedRedirect);
                        this.oauth2RequestValidator.validateScope(authorizationRequest, e);
                        authorizationRequest = this.userApprovalHandler.checkForPreApproval(authorizationRequest, (Authentication) principal);
                        boolean approved = this.userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
                        authorizationRequest.setApproved(approved);
                        if (authorizationRequest.isApproved()) {
                            if (responseTypes.contains("token")) {
                                return this.getImplicitGrantResponse(authorizationRequest);
                            }

                            if (responseTypes.contains("code")) {
                                return new ModelAndView(this.getAuthorizationCodeResponse(authorizationRequest, (Authentication) principal));
                            }
                        }

                        model.put("authorizationRequest", authorizationRequest);
                        return this.getUserApprovalPageResponse(model, authorizationRequest, (Authentication) principal);
                    }
                } else {
                    throw new InsufficientAuthenticationException("User must be authenticated with Spring Security before authorization can be completed.");
                }
            } catch (RuntimeException var11) {
                sessionStatus.setComplete();
                throw var11;
            }
        }
    }

    @RequestMapping(
        value = {"/authorize"},
        method = {RequestMethod.POST},
        params = {"user_oauth_approval"}
    )
    public View approveOrDeny(@RequestParam Map<String, String> approvalParameters, Map<String, ?> model, SessionStatus sessionStatus, Principal principal) {
        if (!(principal instanceof Authentication)) {
            sessionStatus.setComplete();
            throw new InsufficientAuthenticationException("User must be authenticated with Spring Security before authorizing an access token.");
        } else {
            AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
            if (authorizationRequest == null) {
                sessionStatus.setComplete();
                throw new InvalidRequestException("Cannot approve uninitialized authorization request.");
            } else {
                View var8;
                try {
                    Set responseTypes = authorizationRequest.getResponseTypes();
                    authorizationRequest.setApprovalParameters(approvalParameters);
                    authorizationRequest = this.userApprovalHandler.updateAfterApproval(authorizationRequest, (Authentication) principal);
                    boolean approved = this.userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
                    authorizationRequest.setApproved(approved);
                    if (authorizationRequest.getRedirectUri() == null) {
                        sessionStatus.setComplete();
                        throw new InvalidRequestException("Cannot approve request when no redirect URI is provided.");
                    }

                    if (!authorizationRequest.isApproved()) {
                        RedirectView var12 = new RedirectView(this.getUnsuccessfulRedirect(authorizationRequest, new UserDeniedAuthorizationException("User denied access"), responseTypes.contains("token")), false, true, false);
                        return var12;
                    }

                    if (responseTypes.contains("token")) {
                        var8 = this.getImplicitGrantResponse(authorizationRequest).getView();
                        return var8;
                    }

                    var8 = this.getAuthorizationCodeResponse(authorizationRequest, (Authentication) principal);
                } finally {
                    sessionStatus.setComplete();
                }

                return var8;
            }
        }
    }

    private ModelAndView getImplicitGrantResponse(AuthorizationRequest authorizationRequest) {
        try {
            TokenRequest e = this.oAuth2RequestFactory.createTokenRequest(authorizationRequest, "implicit");
            OAuth2Request storedOAuth2Request = this.oAuth2RequestFactory.createOAuth2Request(authorizationRequest);
            OAuth2AccessToken accessToken = this.getAccessTokenForImplicitGrant(e, storedOAuth2Request);
            if (accessToken == null) {
                throw new UnsupportedResponseTypeException("Unsupported response type: token");
            } else {
                return new ModelAndView(new RedirectView(this.appendAccessToken(authorizationRequest, accessToken), false, true, false));
            }
        } catch (OAuth2Exception var5) {
            return new ModelAndView(new RedirectView(this.getUnsuccessfulRedirect(authorizationRequest, var5, true), false, true, false));
        }
    }

    private OAuth2AccessToken getAccessTokenForImplicitGrant(TokenRequest tokenRequest, OAuth2Request storedOAuth2Request) {
        OAuth2AccessToken accessToken = null;
        Object var4 = this.implicitLock;
        synchronized (this.implicitLock) {
            accessToken = this.getTokenGranter("implicit").grant("implicit", new ImplicitTokenRequest(tokenRequest, storedOAuth2Request));
            return accessToken;
        }
    }

    private ModelAndView getUserApprovalPageResponse(Map<String, Object> model, AuthorizationRequest authorizationRequest, Authentication principal) {
        LOG.debug("Loading user approval page: " + this.userApprovalPage);
        model.putAll(this.userApprovalHandler.getUserApprovalRequest(authorizationRequest, principal));
        return new ModelAndView(this.userApprovalPage, model);
    }

    private View getAuthorizationCodeResponse(AuthorizationRequest authorizationRequest, Authentication authUser) {
        try {
            return new RedirectView(this.getSuccessfulRedirect(authorizationRequest, this.generateCode(authorizationRequest, authUser)), false, true, false);
        } catch (OAuth2Exception var4) {
            return new RedirectView(this.getUnsuccessfulRedirect(authorizationRequest, var4, false), false, true, false);
        }
    }

    private String generateCode(AuthorizationRequest authorizationRequest, Authentication authentication) throws AuthenticationException {
        try {
            OAuth2Request e = this.oAuth2RequestFactory.createOAuth2Request(authorizationRequest);
            OAuth2Authentication combinedAuth = new OAuth2Authentication(e, authentication);
            String code = this.defaultOIDCAuthorizationCodeServices.createAuthorizationCode(combinedAuth);
            return code;
        } catch (OAuth2Exception var6) {
            if (authorizationRequest.getState() != null) {
                var6.addAdditionalInformation("state", authorizationRequest.getState());
            }

            throw var6;
        }
    }

    private String appendAccessToken(AuthorizationRequest authorizationRequest, OAuth2AccessToken accessToken) {
        LinkedHashMap vars = new LinkedHashMap();
        HashMap keys = new HashMap();
        if (accessToken == null) {
            throw new InvalidRequestException("An implicit grant could not be made");
        } else {
            vars.put("access_token", accessToken.getValue());
            vars.put("token_type", accessToken.getTokenType());
            String state = authorizationRequest.getState();
            if (state != null) {
                vars.put("state", state);
            }

            Date expiration = accessToken.getExpiration();
            if (expiration != null) {
                long originalScope = (expiration.getTime() - System.currentTimeMillis()) / 1000L;
                vars.put("expires_in", Long.valueOf(originalScope));
            }

            String originalScope1 = (String) authorizationRequest.getRequestParameters().get("scope");
            if (originalScope1 == null || !OAuth2Utils.parseParameterList(originalScope1).equals(accessToken.getScope())) {
                vars.put("scope", OAuth2Utils.formatParameterList(accessToken.getScope()));
            }

            Map additionalInformation = accessToken.getAdditionalInformation();
            Iterator var9 = additionalInformation.keySet().iterator();

            while (var9.hasNext()) {
                String key = (String) var9.next();
                Object value = additionalInformation.get(key);
                if (value != null) {
                    keys.put("extra_" + key, key);
                    vars.put("extra_" + key, value);
                }
            }

            return this.append(authorizationRequest.getRedirectUri(), vars, keys, true);
        }
    }

    private String getUnsuccessfulRedirect(AuthorizationRequest authorizationRequest, OAuth2Exception failure, boolean fragment) {
        if (authorizationRequest != null && authorizationRequest.getRedirectUri() != null) {
            LinkedHashMap query = new LinkedHashMap();
            query.put("error", failure.getOAuth2ErrorCode());
            query.put("error_description", failure.getMessage());
            if (authorizationRequest.getState() != null) {
                query.put("state", authorizationRequest.getState());
            }

            if (failure.getAdditionalInformation() != null) {
                Iterator var5 = failure.getAdditionalInformation().entrySet().iterator();

                while (var5.hasNext()) {
                    Map.Entry additionalInfo = (Map.Entry) var5.next();
                    query.put(additionalInfo.getKey(), additionalInfo.getValue());
                }
            }

            return this.append(authorizationRequest.getRedirectUri(), query, fragment);
        } else {
            throw new UnapprovedClientAuthenticationException("Authorization failure, and no redirect URI.", failure);
        }
    }

    private String getSuccessfulRedirect(AuthorizationRequest authorizationRequest, String authorizationCode) {
        if (authorizationCode == null) {
            throw new IllegalStateException("No authorization code found in the current request scope.");
        } else {
            LinkedHashMap query = new LinkedHashMap();
            query.put("code", authorizationCode);
            String state = authorizationRequest.getState();
            if (state != null) {
                query.put("state", state);
            }

            return this.append(authorizationRequest.getRedirectUri(), query, false);
        }
    }

    private String append(String base, Map<String, ?> query, boolean fragment) {
        return this.append(base, query, (Map) null, fragment);
    }

    private String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {
        UriComponentsBuilder template = UriComponentsBuilder.newInstance();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);

        URI redirectUri;
        try {
            redirectUri = builder.build(true).toUri();
        } catch (Exception var12) {
            redirectUri = builder.build().toUri();
            builder = UriComponentsBuilder.fromUri(redirectUri);
        }

        template.scheme(redirectUri.getScheme()).port(redirectUri.getPort()).host(redirectUri.getHost()).userInfo(redirectUri.getUserInfo()).path(redirectUri.getPath());
        String key;
        String name;
        if (fragment) {
            StringBuilder encoded = new StringBuilder();
            if (redirectUri.getFragment() != null) {
                key = redirectUri.getFragment();
                encoded.append(key);
            }

            String name1;
            for (Iterator key1 = query.keySet().iterator(); key1.hasNext(); encoded.append(name1 + "={" + name + "}")) {
                name = (String) key1.next();
                if (encoded.length() > 0) {
                    encoded.append("&");
                }

                name1 = name;
                if (keys != null && keys.containsKey(name)) {
                    name1 = (String) keys.get(name);
                }
            }

            if (encoded.length() > 0) {
                template.fragment(encoded.toString());
            }

            UriComponents key2 = template.build().expand(query).encode();
            builder.fragment(key2.getFragment());
        } else {
            for (Iterator encoded1 = query.keySet().iterator(); encoded1.hasNext(); template.queryParam(name, new Object[]{"{" + key + "}"})) {
                key = (String) encoded1.next();
                name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = (String) keys.get(key);
                }
            }

            template.fragment(redirectUri.getFragment());
            UriComponents encoded2 = template.build().expand(query).encode();
            builder.query(encoded2.getQuery());
        }

        return builder.build().toUriString();
    }
/* -------------------------------------- ^^Authorize Endpoint Request^^ --------------------------------------*/
}
