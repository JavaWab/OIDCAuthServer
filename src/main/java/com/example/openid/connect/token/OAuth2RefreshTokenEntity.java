package com.example.openid.connect.token;

import com.example.openid.connect.token.model.AuthenticationHolderEntity;
import com.nimbusds.jwt.JWT;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Date;

/**
 * OAuth2RefreshTokenEntity
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class OAuth2RefreshTokenEntity implements OAuth2RefreshToken {
    private Long id;

    private AuthenticationHolderEntity authenticationHolder;

    private ClientDetails client;

    //JWT-encoded representation of this access token entity
    private JWT jwt;

    // our refresh tokens might expire
    private Date expiration;

    @Override
    public String getValue() {
        return jwt.serialize();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthenticationHolderEntity getAuthenticationHolder() {
        return authenticationHolder;
    }

    public void setAuthenticationHolder(AuthenticationHolderEntity authenticationHolder) {
        this.authenticationHolder = authenticationHolder;
    }

    public ClientDetails getClient() {
        return client;
    }

    public void setClient(ClientDetails client) {
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
