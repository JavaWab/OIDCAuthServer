package com.auth.openid.connect.service.impl;

import com.auth.openid.connect.service.KeyPairService;
import com.auth.openid.connect.token.keypair.OIDCKeypair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * DefaultKeyPairService
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
@Service
public class DefaultKeyPairService implements KeyPairService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveKeyPair(OIDCKeypair keypair) {
        mongoTemplate.save(keypair);
    }

    @Override
    public OIDCKeypair getKeyPair(String kid) {
        OIDCKeypair oidcKeypair = mongoTemplate.findOne(Query.query(Criteria.where("kid").is(kid)), OIDCKeypair.class);
        return oidcKeypair;
    }
}
