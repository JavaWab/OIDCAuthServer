package com.auth.openid.connect.token;

import com.auth.oauth2.clientdetails.WXBaseClientDetails;
import com.auth.openid.connect.token.model.AuthenticationHolderEntity;
import com.nimbusds.jwt.JWT;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.io.Serializable;
import java.util.Date;

/**
 * OAuth2RefreshTokenEntity
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class OAuth2RefreshTokenEntity implements OAuth2RefreshToken, Serializable {
    private static final long serialVersionUID = 833619042218875228L;
    private String id;

    private AuthenticationHolderEntity authenticationHolder;

    private WXBaseClientDetails client;

    //JWT-encoded representation of this access token entity
    private transient JWT jwt;

    // our refresh tokens might expire
    private Date expiration;

    public boolean isExpired() {
        return getExpiration() == null ? false : System.currentTimeMillis() > getExpiration().getTime();
    }

    @Override
    public String getValue() {
        return jwt.serialize();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuthenticationHolderEntity getAuthenticationHolder() {
        return authenticationHolder;
    }

    public void setAuthenticationHolder(AuthenticationHolderEntity authenticationHolder) {
        this.authenticationHolder = authenticationHolder;
    }

    public WXBaseClientDetails getClient() {
        return client;
    }

    public void setClient(WXBaseClientDetails client) {
        this.client = client;
    }

    public JWT getJwt() {
        return jwt;
    }

    public void setJwt(JWT jwt) {
        this.jwt = jwt;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
