package com.auth;

import com.auth.oauth2.clientdetails.MongoClientDetailsService;
import com.auth.oauth2.service.MongodbTokenStore;
import com.auth.oauth2.userdetails.UserPressDetailsService;
import com.auth.openid.connect.filter.OIDCTokenFilter;
import com.auth.openid.connect.service.impl.DefaultOAuth2ProviderTokenService;
import com.auth.openid.connect.service.impl.DefaultOIDCAuthorizationCodeServices;
import com.auth.openid.connect.tokenconverter.OIDCDefaultAccessTokenConverter;
import com.auth.openid.connect.tokenconverter.OIDCUserAuthenticationConverter;
import com.auth.utils.BlowfishEncryptor;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwk.RsaJsonWebKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
public class OidctestApplication {
    @Value("${westar.flowfish.key}")
    private String blowfishKey;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    public static void main(String[] args) {
        SpringApplication.run(OidctestApplication.class, args);
    }

    @Bean(name = "blowfishEncryptor")
    public BlowfishEncryptor getBlowfishEncryptor() {
        return new BlowfishEncryptor(blowfishKey);
    }

    @Bean(name = "userPressDetailsService")
    public UserPressDetailsService getUserPressDetailsService() {
        return new UserPressDetailsService();
    }

    @Bean(name = "mongoClientDetailsService")
    public MongoClientDetailsService getMongoClientDetailsService() {
        return new MongoClientDetailsService();
    }

    @Bean(name = "redisTokenStore")
    public RedisTokenStore getRedisTokenStore() {
        return new RedisTokenStore(jedisConnectionFactory);
    }

    @Bean(name = "mongodbTokenStore")
    public MongodbTokenStore getMongodbTokenStore(){
        return new MongodbTokenStore();
    }

    @Bean(name = "defaultOAuth2ProviderTokenService")
    public DefaultOAuth2ProviderTokenService getDefaultOAuth2ProviderTokenService() {
        return new DefaultOAuth2ProviderTokenService();
    }

    @Bean(name = "defaultOIDCAuthorizationCodeServices")
    public DefaultOIDCAuthorizationCodeServices getDefaultOIDCAuthorizationCodeServices() {
        return new DefaultOIDCAuthorizationCodeServices();
    }

    /**
     * 生成处理jwt的工具
     *
     * @return
     */
//    @Bean(name = "jwtTokenStore")
    public JwtTokenStore getJwtTokenStore() {
        //添加token解析增强工具
        JwtTokenStore store = new JwtTokenStore(tokenEnhancer());
        return store;
    }

    //    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
        final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //创建用户身份授权处理器并为其制定处理用户信息的service
        OIDCUserAuthenticationConverter oidcUserAuthenticationConverter = new OIDCUserAuthenticationConverter();
        oidcUserAuthenticationConverter.setUserDetailsService(getUserPressDetailsService());
        //创建OIDCToken处理工具并指定用户身份处理器
        OIDCDefaultAccessTokenConverter oidcDefaultAccessTokenConverter = new OIDCDefaultAccessTokenConverter();
        oidcDefaultAccessTokenConverter.setUserTokenConverter(oidcUserAuthenticationConverter);
        //将创建好的OIDCToken处理工具指定给jwt token处理工具
        jwtAccessTokenConverter.setAccessTokenConverter(oidcDefaultAccessTokenConverter);

        try {
            Map<String, Object> privateKeyMap = JsonUtil.parseJson("{\"kty\":\"RSA\",\"kid\":\"6587743035498186987\",\"alg\":\"RS256\",\"n\":\"uytG0T1iG74KkQKjpyH_iPE9kL-yrOH6q_02DMPxKw9wQR55CUL9GEkkFRAHSDuDlb3eok5rcrk9UbU6QGfyx6NAVbnEOMw2D5jVrOrsB4JndQQkpXJbAKmtV7zwSqNdzdmRZ2AgWebPvU-TMa3JQhtTD2a4UKEgfxxaeFf-ELVhVPCeglTrkehai1n6ESrlFNwYJh4XwkAgQqMPYBt0Kwe7eoOaTUCaSbezM6UWEG64KeGUX47kOL1GlOludvRuP0BqVax0L8zYmgkUf96fF-5WJRQoLpf-CT-DBZsPZ7kIKw9dsBSkt_n_DZdJjkIF413QSlyellF4_cc0cmw7rw\",\"e\":\"AQAB\",\"d\":\"olqXw0iLoaTnZYH0kzVag18_GCAwzOX4mte52rjcqY1qwhG8y9vfzwEi22Jb7rwxF-LrC78aVRl5pzacoOGKJsHlPHc3Tk1VV5IrRyNAOgNT2p0NbLYVWi3UvxaVGSk8ZSMkjZdEeLdN29j6xU0KFt283s8rckVR-vCNSXaKW9J9cGT8SQBg66G1rJ3K59zSbstMny6_SYCHDv4dV6rFkcJ1LkcWB7UWykkkPZQzcPENsjZDVClu_kUEA-ywS5hySgE3Js5rX7sq2leVP7hRVQ8i-iDe4hm2JxbHV98VodZJg-e5IperTQFjXFXfjzeQZ0LitiCfrX6nZSZhEcgwAQ\",\"p\":\"6NsbRkxVVfO0IIcYZXuhBdBBBEuqAOegfG2_waCk4C593u4a2WIhzonVNxXibViCHHmCpH9HSgET8Vf8aqdLeIvplfTy0EpseJTTRGASHJ9oOGJWvY1Hhl-rPLe41o111jKppOZd68cKDK2DYoT0fSJsbfq9B1RAzqBCYzANQoE\",\"q\":\"zcWxZ9rwoALrqiMBtWoOeR5I0KdjvYtwg_yjHz-bVkDzmi96rNJIzxEWXmaRFTSNuNrvhR7V0YS9Iwy4U3aQizJsOUlIglQEv-V_F2wvSbSvXaEAmaqkcVF5QyPZOhLE-zyyZfQ4f0dXoK3HsK-13eyXayH_5eACl-cl0z1uBi8\",\"dp\":\"RqNZNc0oQgfJB-kTnI2Rfnr7jjsJ2nZpt_VvK2T5P0y7QGqI2JMtYENt2-UWqEcmSU8PM6mszaVgEGG5n_0aJvqCpMwG8Ory1u2G3YIrXGV7L0eduqjmHrxc7PFq8CM_sPfzgKOlsEXUJZ5pcfSVWnSb4g0jVNrZQhVZrk9AZoE\",\"dq\":\"Ar6CMSpgTyH61pgE69BwRPk8a1vwHpT_eSMjYcVNYwcWuJWtgySCyBAoRjYD9U5wHWj-DL5uh47HnvIKe0J3hwOzIchyaRqJva1n1n4g21DHje4ZvfLioog9n8GYogeiDN-wWV-6aSggaQooQZj2SfFfy5P2f7BPGIuCm6CUGQ0\",\"qi\":\"mjsArtpslk5jPTIHBSx0_lIJMDVKfgNXe1j1IkZtX2bupN1Tr6ysF3YzSOCvuV1yicWyg74q7u6pr6eM6GExt79fsTnZCeki0Cl_4NjX85sbc9qMD7sknCm-Ue62JPx8lyDaaS42BdmxSEUYaNk2hoOF8TWqSbLEfOxju34piyI\"}");
            Map<String, Object> publicKeyMap = JsonUtil.parseJson("{\"kty\":\"RSA\",\"kid\":\"6587743035498186987\",\"alg\":\"RS256\",\"n\":\"uytG0T1iG74KkQKjpyH_iPE9kL-yrOH6q_02DMPxKw9wQR55CUL9GEkkFRAHSDuDlb3eok5rcrk9UbU6QGfyx6NAVbnEOMw2D5jVrOrsB4JndQQkpXJbAKmtV7zwSqNdzdmRZ2AgWebPvU-TMa3JQhtTD2a4UKEgfxxaeFf-ELVhVPCeglTrkehai1n6ESrlFNwYJh4XwkAgQqMPYBt0Kwe7eoOaTUCaSbezM6UWEG64KeGUX47kOL1GlOludvRuP0BqVax0L8zYmgkUf96fF-5WJRQoLpf-CT-DBZsPZ7kIKw9dsBSkt_n_DZdJjkIF413QSlyellF4_cc0cmw7rw\",\"e\":\"AQAB\"}");

            PrivateKey privateKey = new RsaJsonWebKey(privateKeyMap).getRsaPrivateKey();
            PublicKey publicKey = new RsaJsonWebKey(publicKeyMap).getPublicKey();

            // RSA Config
            jwtAccessTokenConverter.setKeyPair(new KeyPair(publicKey, privateKey));
            // HMAC Config
            //jwtAccessTokenConverter.setSigningKey("Passw0rd");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jwtAccessTokenConverter;
    }

    /**
     * 自动装配filter Bean
     *
     * @return
     */
    @Bean(name = "filterRegistrationBean")
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        OIDCTokenFilter filter = new OIDCTokenFilter();
        filter.setJwtTokenStore(getJwtTokenStore());
        registrationBean.setFilter(filter);
        return registrationBean;
    }
}
