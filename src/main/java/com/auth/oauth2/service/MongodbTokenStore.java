package com.auth.oauth2.service;

import com.auth.openid.connect.token.OAuth2AccessTokenEntity;
import com.auth.openid.connect.token.OAuth2RefreshTokenEntity;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * MongodbTokenStore
 *
 * @author Anbang Wang
 * @date 2017/1/11
 */

public class MongodbTokenStore implements TokenStore {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken oAuth2AccessToken) {
        return this.readAuthentication(oAuth2AccessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String tokenValue) {
        Map result = mongoTemplate.findOne(Query.query(Criteria.where("token_value").is(tokenValue)), Map.class, "Access_Token");
        if (result != null) {
            return (OAuth2Authentication) result.get("authentication");
        } else {
            return null;
        }
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        OAuth2AccessTokenEntity accessTokenEntity = (OAuth2AccessTokenEntity)oAuth2AccessToken;
        String clientID = accessTokenEntity.getClient().getClientId();
        String username = accessTokenEntity.getAuthenticationHolder().getUserAuth().getName();
        Map<String, Object> obj = new HashMap();
        obj.put("access_token", SerializationUtils.serialize(oAuth2AccessToken));
        obj.put("client_id", clientID);
        obj.put("username", username);
        obj.put("authentication", SerializationUtils.serialize(oAuth2Authentication));
        obj.put("token_value", oAuth2AccessToken.getValue());
        mongoTemplate.save(obj, "Access_Token");
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        Map result = mongoTemplate.findOne(Query.query(Criteria.where("token_value").is(tokenValue)), Map.class, "Access_Token");
        if (result != null) {
            return (OAuth2AccessTokenEntity)(result.get("access_token"));
        } else {
            return null;
        }
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        mongoTemplate.remove(Query.query(Criteria.where("token_value").is(oAuth2AccessToken.getValue())), "Access_Token");
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> obj = new HashMap();
        obj.put("refresh_token", SerializationUtils.serialize(oAuth2RefreshToken));
        obj.put("authentication", SerializationUtils.serialize(oAuth2Authentication));
        obj.put("token_value", oAuth2RefreshToken.getValue());
        mongoTemplate.save(obj, "Refresh_Token");
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {

        Map result = mongoTemplate.findOne(Query.query(Criteria.where("token_value").is(tokenValue)), Map.class, "Refresh_Token");
        if (result != null) {
            OAuth2RefreshTokenEntity tokenEntity = (OAuth2RefreshTokenEntity)(SerializationUtils.deserialize((byte[]) result.get("refresh_token")));
            try {
                JWT jwt = JWTParser.parse(tokenValue);
                tokenEntity.setJwt(jwt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return tokenEntity;
        } else {
            return null;
        }
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        Map result = mongoTemplate.findOne(Query.query(Criteria.where("refresh_token").is(oAuth2RefreshToken)), Map.class, "Refresh_Token");
        if (result != null) {
            return (OAuth2Authentication)(result.get("authentication"));
        } else {
            return null;
        }
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        mongoTemplate.remove(Query.query(Criteria.where("refresh_token").is(oAuth2RefreshToken)), "Refresh_Token");
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        OAuth2RefreshTokenEntity refreshToken = (OAuth2RefreshTokenEntity) oAuth2RefreshToken;
        mongoTemplate.remove(Query.query(Criteria.where("authentication").is(refreshToken.getAuthenticationHolder().getAuthentication())), "Access_Token");
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication oAuth2Authentication) {
        Map result = mongoTemplate.findOne(Query.query(Criteria.where("authentication").is(oAuth2Authentication)), Map.class, "Access_Token");
        if (result != null) {
            return (OAuth2AccessToken) result.get("access_token");

        } else {
            return null;
        }
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {

        Collection<Map> results = mongoTemplate.find(Query.query(Criteria.where("client_id").is(clientId).and("username").is(userName)), Map.class, "Access_Token");
        if (results != null) {
            return OAuth2AccessTokenEntityListConverter(results);
        } else {
            return null;
        }
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        Collection<Map> results = mongoTemplate.find(Query.query(Criteria.where("client_id").is(clientId)), Map.class, "Access_Token");
        if (results != null) {
            return OAuth2AccessTokenEntityListConverter(results);
        } else {
            return null;
        }
    }

    private Collection<OAuth2AccessToken> OAuth2AccessTokenEntityListConverter(Collection<Map> list) {
        Collection<OAuth2AccessToken> results = new ArrayList<>();
        for (Map entity : list) {
            Object access_token = entity.get("access_token");
            Object token_vlaue = entity.get("token_value");
            OAuth2AccessTokenEntity tokenEntity = (OAuth2AccessTokenEntity)(SerializationUtils.deserialize((byte[])access_token));
            try {
                JWT jwt = JWTParser.parse(token_vlaue.toString());
                tokenEntity.setJwt(jwt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            results.add(tokenEntity);
        }
        return results;
    }
}
