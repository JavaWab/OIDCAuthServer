package com.example.oauth2.clientdetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoClientDetailsService
 *
 * @author Anbang Wang
 * @date 2016/12/13
 */

public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ClientDetails loadClientByClientId(String client_id) throws ClientRegistrationException {
        return mongoTemplate.findOne(Query.query(Criteria.where("clientId").is(client_id)), BaseClientDetails.class);
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        mongoTemplate.save(clientDetails);
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        Update update = Update.update("resourceIds", clientDetails.getResourceIds())
                .set("clientSecret", clientDetails.getClientSecret())
                .set("authorizedGrantTypes", clientDetails.getAuthorizedGrantTypes())
                .set("registeredRedirectUris", clientDetails.getRegisteredRedirectUri())
                .set("accessTokenValiditySeconds", clientDetails.getAccessTokenValiditySeconds())
                .set("refreshTokenValiditySeconds", clientDetails.getRefreshTokenValiditySeconds())
                .set("scope", clientDetails.getScope());
        mongoTemplate.updateFirst(Query.query(Criteria.where("clientId").is(clientDetails.getClientId())), update, BaseClientDetails.class);
    }

    @Override
    public void updateClientSecret(String s, String s1) throws NoSuchClientException {
        mongoTemplate.updateFirst(Query.query(Criteria.where("clientId").is(s)), Update.update("clientSecret", s1), BaseClientDetails.class);
    }

    @Override
    public void removeClientDetails(String s) throws NoSuchClientException {
        mongoTemplate.remove(Query.query(Criteria.where("clientId").is(s)), BaseClientDetails.class);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        List<BaseClientDetails> list = mongoTemplate.findAll(BaseClientDetails.class);
        return new ArrayList<>(list);
    }
}
