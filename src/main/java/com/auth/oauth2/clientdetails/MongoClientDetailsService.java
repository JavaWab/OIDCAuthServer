package com.auth.oauth2.clientdetails;

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
        return mongoTemplate.findOne(Query.query(Criteria.where("clientId").is(client_id)), WXBaseClientDetails.class);
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        mongoTemplate.save(clientDetails);
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        WXBaseClientDetails wxBaseClientDetails = (WXBaseClientDetails)clientDetails;
        Update update = Update.update("resourceIds", wxBaseClientDetails.getResourceIds())
                .set("clientSecret", wxBaseClientDetails.getClientSecret())
                .set("authorizedGrantTypes", wxBaseClientDetails.getAuthorizedGrantTypes())
                .set("registeredRedirectUris", wxBaseClientDetails.getRegisteredRedirectUri())
                .set("accessTokenValiditySeconds", wxBaseClientDetails.getAccessTokenValiditySeconds())
                .set("refreshTokenValiditySeconds", wxBaseClientDetails.getRefreshTokenValiditySeconds())
                .set("scope", wxBaseClientDetails.getScope());
        mongoTemplate.updateFirst(Query.query(Criteria.where("clientId").is(wxBaseClientDetails.getClientId())), update, WXBaseClientDetails.class);
    }

    @Override
    public void updateClientSecret(String s, String s1) throws NoSuchClientException {
        mongoTemplate.updateFirst(Query.query(Criteria.where("clientId").is(s)), Update.update("clientSecret", s1), WXBaseClientDetails.class);
    }

    @Override
    public void removeClientDetails(String s) throws NoSuchClientException {
        mongoTemplate.remove(Query.query(Criteria.where("clientId").is(s)), WXBaseClientDetails.class);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        List<WXBaseClientDetails> list = mongoTemplate.findAll(WXBaseClientDetails.class);
        return new ArrayList<>(list);
    }
}
