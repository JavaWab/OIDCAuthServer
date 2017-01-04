package com.auth.openid.connect.service.impl;

import com.auth.openid.connect.service.OIDCAuthorizationCodeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

/**
 * DefaultOIDCAuthorizationCodeServices
 *
 * @author Anbang Wang
 * @date 2016/12/19
 */
public class DefaultOIDCAuthorizationCodeServices implements OIDCAuthorizationCodeServices {
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RandomValueStringGenerator generator = new RandomValueStringGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    private String prefix = "";

    @Override
    public String createAuthorizationCode(OAuth2Authentication oAuth2Authentication) {
        String code = this.generator.generate();
        this.store(code, oAuth2Authentication);
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication auth = this.remove(code);
        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        } else {
            return auth;
        }
    }

    protected void store(String code, OAuth2Authentication oAuth2Authentication) {
        RedisConnection conn = jedisConnectionFactory.getConnection();
        byte[] codeKey = this.serializeKey("authorization_code:" + code);
        byte[] serializedAuth = this.serialize((Object) oAuth2Authentication);
        try {
            conn.set(codeKey, serializedAuth);
        } finally {
            conn.close();
        }
    }

    ;

    protected OAuth2Authentication remove(String code) {
        RedisConnection conn = jedisConnectionFactory.getConnection();
        byte[] bytes1;
        try {
            byte[] codeKey = this.serializeKey("authorization_code:" + code);
            bytes1 = conn.get(codeKey);
            conn.del(codeKey);
        } finally {
            conn.close();
        }
        OAuth2Authentication oAuth2Authentication = this.deserializeAuthentication(bytes1);
        if (oAuth2Authentication != null) {
            return oAuth2Authentication;
        }
        return null;

    }

    ;

    private byte[] serialize(Object object) {
        return this.serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String string) {
        return this.serializationStrategy.serialize(prefix + string);
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return (OAuth2Authentication) this.serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }
}
