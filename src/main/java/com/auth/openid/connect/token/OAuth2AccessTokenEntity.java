package com.auth.openid.connect.token;

import com.auth.oauth2.clientdetails.WXBaseClientDetails;
import com.auth.openid.connect.token.model.ApprovedSite;
import com.auth.openid.connect.token.model.AuthenticationHolderEntity;
import com.auth.openid.connect.token.model.Permission;
import com.nimbusds.jwt.JWT;
import org.springframework.data.annotation.Transient;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2AccessTokenEntity
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class OAuth2AccessTokenEntity implements OAuth2AccessToken,Serializable {

    private static final long serialVersionUID = -525202699563723926L;
    private String id;

    private WXBaseClientDetails client;

    private AuthenticationHolderEntity authenticationHolder; // the authentication that made this access

    private transient JWT jwtValue; // JWT-encoded access token value

    private OAuth2AccessTokenEntity idToken; // JWT-encoded OpenID Connect IdToken

    private Date expiration;

    private String tokenType = OAuth2AccessToken.BEARER_TYPE;

    private OAuth2RefreshTokenEntity refreshToken;

    private Set<String> scope;

    private Set<Permission> permissions;

    private ApprovedSite approvedSite;

    @Override
    public Map<String, Object> getAdditionalInformation() {
        Map<String, Object> map = new HashMap<>(); //super.getAdditionalInformation();
        if (getIdToken() != null) {
            map.put("id_token", getIdTokenString());
        }
        return map;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public OAuth2RefreshToken getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public boolean isExpired() {
        return getExpiration() == null ? false : System.currentTimeMillis() > getExpiration().getTime();
    }

    @Override
    public Date getExpiration() {
        return expiration;
    }

    @Override
    public int getExpiresIn() {
        if (getExpiration() == null) {
            return -1; // no expiration time
        } else {
            int secondsRemaining = (int) ((getExpiration().getTime() - System.currentTimeMillis()) / 1000);
            if (isExpired()) {
                return 0; // has an expiration time and expired
            } else { // has an expiration time and not expired
                return secondsRemaining;
            }
        }
    }

    @Override
    public String getValue() {
        return jwtValue.serialize();
    }

    public OAuth2AccessTokenEntity getIdToken() {
        return idToken;
    }

    public String getIdTokenString() {
        if (idToken != null) {
            return idToken.getValue(); // get the JWT string value of the id token entity
        } else {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WXBaseClientDetails getClient() {
        return client;
    }

    public void setClient(WXBaseClientDetails client) {
        this.client = client;
    }

    public AuthenticationHolderEntity getAuthenticationHolder() {
        return authenticationHolder;
    }

    public void setAuthenticationHolder(AuthenticationHolderEntity authenticationHolder) {
        this.authenticationHolder = authenticationHolder;
    }

    public JWT getJwt() {
        return jwtValue;
    }

    public void setJwt(JWT jwt) {
        this.jwtValue = jwt;
    }

    public void setIdToken(OAuth2AccessTokenEntity idToken) {
        this.idToken = idToken;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setRefreshToken(OAuth2RefreshTokenEntity refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public ApprovedSite getApprovedSite() {
        return approvedSite;
    }

    public void setApprovedSite(ApprovedSite approvedSite) {
        this.approvedSite = approvedSite;
    }
}
