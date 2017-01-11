package com.auth.controller;

import com.auth.controller.vo.TokenVO;
import com.auth.controller.vo.UserVO;
import com.auth.oauth2.clientdetails.MongoClientDetailsService;
import com.auth.oauth2.userdetails.model.UserInfo;
import com.auth.oauth2.userdetails.model.impl.DefaultAddress;
import com.auth.oauth2.userdetails.model.impl.DefaultUserInfo;
import com.auth.openid.connect.service.impl.DefaultOAuth2ProviderTokenService;
import com.auth.openid.connect.service.impl.DefaultOIDCAuthorizationCodeServices;
import com.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * UserController
 *
 * @author Anbang Wang
 * @date 2016/12/16
 */
@Controller
@RequestMapping(value = "/api")
public class APIController {
    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/users/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String addUser(@RequestBody @Validated UserVO user) throws Exception {
        DefaultAddress location = new DefaultAddress();
        location.setId(UUID.randomUUID().toString());
        location.setCountry("China");
        location.setPostalCode("100001");
        location.setRegion("HaiDian");

        DefaultUserInfo userInfo = new DefaultUserInfo();
        userInfo.setSub(user.getUsername());
        userInfo.setName(user.getUsername());
        userInfo.setPreferredUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setPassword(user.getPassword());
        userInfo.setEmailVerified(true);
        userInfo.setNickname(user.getNickname());
        userInfo.setBirthdate(user.getBirthday().toString());
        userInfo.setGender(user.getGender());
        userInfo.setAddress(location);
        List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_USER");
        userInfo.setAuthorities(authorities);

        userService.addUser(userInfo);
        return userInfo.toJson().toString();
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, Object> userInfo(Authentication authentication) {
//        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String userStr = auth.getTokenValue();
//        userStr = userStr.split("\\.")[1];
//        JSONObject jsonObject = new JSONObject(new String(org.springframework.security.crypto.codec.Base64.decode(userStr.getBytes())));
        Map<String, Object> userinfo = new HashMap<String, Object>();
//        UserInfo user = userService.getUserByUsername(jsonObject.getString("sub"));
        User tUser = (User) (authentication.getPrincipal());
        UserInfo user = userService.getUserByUsername(tUser.getUsername());
        userinfo.put("sub", user.getSub());
        userinfo.put("name", user.getName());
        userinfo.put("preferred_username", user.getPreferredUsername());
        userinfo.put("email", user.getEmail());
        userinfo.put("email_verified", user.getEmailVerified());

        return userinfo;
    }


}
