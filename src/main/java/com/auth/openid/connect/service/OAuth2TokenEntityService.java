package com.auth.openid.connect.service;

import com.auth.openid.connect.token.OAuth2AccessTokenEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * OAuth2TokenEntityService
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public interface OAuth2TokenEntityService extends AuthorizationServerTokenServices, ResourceServerTokenServices {
    @Override
    public OAuth2AccessTokenEntity createAccessToken(OAuth2Authentication oAuth2Authentication) throws AuthenticationException;

    @Override
    public OAuth2AccessTokenEntity refreshAccessToken(String accessTokenValue, TokenRequest tokenRequest) throws AuthenticationException;

    @Override
    public OAuth2AccessTokenEntity getAccessToken(OAuth2Authentication oAuth2Authentication);

    @Override
    public OAuth2AccessTokenEntity readAccessToken(String accessTokenValue);
}
