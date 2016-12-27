package com.example.oauth2.config;

import org.jose4j.json.JsonUtil;
import org.jose4j.jwk.RsaJsonWebKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * ResourceServerConfiguration
 *
 * @author Anbang Wang
 * @date 2016/12/16
 */
//@Configuration
//@EnableResourceServer
//public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
//    @Autowired
//    @Qualifier("jwtTokenStore")
//    private JwtTokenStore jwtTokenStore;
//
//    @Override
//    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//        resources.resourceId("acme");//这个资源ID就是认证授权服务器中的client_id，千万不可弄错
//        resources.tokenStore(jwtTokenStore);
//
//    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.formLogin().permitAll();
//        http.authorizeRequests()
//                .antMatchers("/api/test", "/api/userinfo", "/api/users/add")
//                .authenticated()
//            .antMatchers("/oauth/authorize").permitAll();
//    }
//}
