package com.example.oauth2.config;

import com.example.oauth2.clientdetails.MongoClientDetailsService;
import com.example.oauth2.clientdetails.WXBaseClientDetails;
import com.example.openid.connect.service.impl.DefaultOAuth2ProviderTokenService;
import com.example.oauth2.userdetails.UserPressDetailsService;
import com.example.openid.connect.service.impl.DefaultOIDCAuthorizationCodeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * OAuth2Config
 * 客户端模式获取AccessToken URL：
 *      1、curl -X POST -H "Authorization: Basic YWNtZTphY21lc2VjcmV0" -d grant_type=client_credentials http://localhost:1111/uaa/oauth/token
 *      2、curl acme:acmesecret@localhost:1111/uaa/oauth/token -d grant_type=client_credentials
 *      3、curl -u acme:acmesecret http://localhost:1111/uaa/oauth/token -d grant_type=client_credentials
 * 用户名密码模式获取AccessToken URL: curl acme:acmesecret@localhost:1111/uaa/oauth/token -d grant_type=password -d username=user -d password=password -d scope=openid
 * 授权码模式获取AccessToken URL：curl acme:acmesecret@localhost:1111/uaa/oauth/token -d grant_type=authorization_code -d client_id=acme -d redirect_uri=http://example.com -d code=vGFx4k
 *
 * @author Anbang Wang
 * @date 2016/12/13
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MongoClientDetailsService mongodbClientDetailsService;
    @Autowired
    @Qualifier("redisTokenStore")
    private RedisTokenStore redisTokenStore;
    @Autowired
    private UserPressDetailsService userPressDetailsService;
//    @Autowired
//    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private DefaultOAuth2ProviderTokenService defaultOAuth2ProviderTokenService;
    @Autowired
    private DefaultOIDCAuthorizationCodeServices defaultOIDCAuthorizationCodeServices;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        if (mongodbClientDetailsService.loadClientByClientId("acme") == null){
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            WXBaseClientDetails clientDetails = new WXBaseClientDetails();
            clientDetails.setClientId("acme");
            clientDetails.setClientSecret("acmesecret");
            clientDetails.setAuthorizedGrantTypes(Arrays.asList("authorization_code", "client_credentials", "refresh_token", "password"));
            clientDetails.setScope(Arrays.asList("openid"));
            clientDetails.setAuthorities(authorities);
            mongodbClientDetailsService.addClientDetails(clientDetails);
        }

        clients.withClientDetails(mongodbClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        endpoints.tokenServices(defaultOAuth2ProviderTokenService);
        endpoints.authenticationManager(authenticationManager);
        endpoints.tokenStore(redisTokenStore);
        endpoints.userDetailsService(userPressDetailsService);
        endpoints.setClientDetailsService(mongodbClientDetailsService);
        endpoints.authorizationCodeServices(defaultOIDCAuthorizationCodeServices);
    }

}
