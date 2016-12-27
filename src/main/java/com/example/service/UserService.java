package com.example.service;

import com.example.oauth2.userdetails.model.UserInfo;
import com.example.oauth2.userdetails.model.impl.DefaultUserInfo;
import com.example.utils.BlowfishEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserService
 *
 * @author Anbang Wang
 * @date 2016/12/16
 */
@Service
public class UserService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BlowfishEncryptor blowfishEncryptor;

    public DefaultUserInfo addUser(DefaultUserInfo user)throws Exception{
        try {
            user.setPassword(blowfishEncryptor.encryptString(user.getPassword()));
            mongoTemplate.save(user);
            return user;
        } catch (DuplicateKeyException exception ){
            exception.printStackTrace();
            throw exception;
        }
    }

    public UserInfo getUserByUsername(String username) throws UsernameNotFoundException {
        DefaultUserInfo user = mongoTemplate.findOne(Query.query(Criteria.where("sub").is(username)), DefaultUserInfo.class);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException(username + " is not found");
    }
}