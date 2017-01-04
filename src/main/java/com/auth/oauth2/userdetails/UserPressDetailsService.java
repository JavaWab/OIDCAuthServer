package com.auth.oauth2.userdetails;

import com.auth.oauth2.userdetails.model.UserInfo;
import com.auth.oauth2.userdetails.model.impl.DefaultUserInfo;
import com.auth.utils.BlowfishEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * UserPressDetailsService
 *
 * @author Anbang Wang
 * @date 2016/12/13
 */
public class UserPressDetailsService implements UserDetailsService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BlowfishEncryptor blowfishEncryptor;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DefaultUserInfo user = mongoTemplate.findOne(Query.query(Criteria.where("sub").is(username)), DefaultUserInfo.class);
        if (user != null) {
            List<GrantedAuthority> authorityList = new ArrayList<>();
            for (String authorities:user.getAuthorities()) {
                authorityList.add(new SimpleGrantedAuthority(authorities));
            }
            User idsuser = new User(username, blowfishEncryptor.decryptString(user.getPassword()), authorityList);
            return idsuser;
        }
        throw new UsernameNotFoundException(username + " is not found");
    }

    public UserInfo getUserByUsername(String username) throws UsernameNotFoundException {
        DefaultUserInfo user = mongoTemplate.findOne(Query.query(Criteria.where("sub").is(username)), DefaultUserInfo.class);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException(username + " is not found");
    }
}
