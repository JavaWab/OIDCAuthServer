package com.example.controller;

import com.example.controller.vo.TokenVO;
import com.example.controller.vo.UserVO;
import com.example.oauth2.clientdetails.MongoClientDetailsService;
import com.example.oauth2.userdetails.model.UserInfo;
import com.example.oauth2.userdetails.model.impl.DefaultAddress;
import com.example.oauth2.userdetails.model.impl.DefaultUserInfo;
import com.example.openid.connect.service.impl.DefaultOAuth2ProviderTokenService;
import com.example.openid.connect.service.impl.DefaultOIDCAuthorizationCodeServices;
import com.example.service.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
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

import java.util.*;

/**
 * UserController
 *
 * @author Anbang Wang
 * @date 2016/12/16
 */
@Controller
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class APIController implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private MongoClientDetailsService clientDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultOIDCAuthorizationCodeServices defaultOIDCAuthorizationCodeServices;
    @Autowired
    private DefaultOAuth2ProviderTokenService defaultOAuth2ProviderTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private OAuth2RequestFactory oAuth2RequestFactory;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "Hello Test";
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> userInfo() {
        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        String userStr = auth.getTokenValue();
        userStr = userStr.split("\\.")[1];
        JSONObject jsonObject = new JSONObject(new String(org.springframework.security.crypto.codec.Base64.decode(userStr.getBytes())));
        Map<String, Object> userinfo = new HashMap<String, Object>();
        UserInfo user = userService.getUserByUsername(jsonObject.getString("sub"));

        userinfo.put("sub", user.getSub());
        userinfo.put("name", user.getName());
        userinfo.put("preferred_username", user.getPreferredUsername());
        userinfo.put("email", user.getEmail());
        userinfo.put("email_verified", user.getEmailVerified());

        return userinfo;
    }

    @RequestMapping(value = "/token", method = RequestMethod.POST,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public OAuth2AccessToken getAccessToken(@ModelAttribute TokenVO tokenVO) {
        String client_id = tokenVO.getClient_id();
        Map<String, String> parameters = tokenVO.toMap();
        ClientDetails authenticatedClient = this.clientDetailsService.loadClientByClientId(client_id);
        if (authenticatedClient == null) {
            throw new InvalidClientException("Client not found: " + client_id);
        }
        TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(parameters, authenticatedClient);
        new DefaultOAuth2RequestValidator().validateScope(tokenRequest, authenticatedClient);
        String grantType = tokenRequest.getGrantType();
        if (!StringUtils.hasText(grantType)) {
            throw new InvalidRequestException("Missing grant type");
        } else if (grantType.equals("implicit")) {
            throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
        } else {
            if (this.isAuthCodeRequest(parameters) && !tokenRequest.getScope().isEmpty()) {
                LOG.debug("Clearing scope of incoming token request");
                tokenRequest.setScope(Collections.<String>emptySet());
            }

            if (this.isRefreshTokenRequest(parameters)) {
                tokenRequest.setScope(OAuth2Utils.parseParameterList((String) parameters.get("scope")));
            }

            OAuth2AccessToken token = this.getTokenGranter(grantType).grant(grantType, tokenRequest);
            if (token == null) {
                throw new UnsupportedGrantTypeException("Unsupported grant type: " + grantType);
            } else {
                return token;
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(this.clientDetailsService != null, "ClientDetailsService must be provided");
        this.oAuth2RequestFactory = new DefaultOAuth2RequestFactory(this.clientDetailsService);
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
    }

    protected TokenGranter getTokenGranter(String grantType) {
        if ("authorization_code".equals(grantType)) {
            return new AuthorizationCodeTokenGranter(this.defaultOAuth2ProviderTokenService, this.defaultOIDCAuthorizationCodeServices, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("password".equals(grantType)) {
            return new ResourceOwnerPasswordTokenGranter(this.authenticationManager, this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("refresh_token".equals(grantType)) {
            return new RefreshTokenGranter(this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("client_credentials".equals(grantType)) {
            return new ClientCredentialsTokenGranter(this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else if ("implicit".equals(grantType)) {
            return new ImplicitTokenGranter(this.defaultOAuth2ProviderTokenService, this.clientDetailsService, this.oAuth2RequestFactory);
        } else {
            throw new UnsupportedGrantTypeException("Unsupport grant_type: " + grantType);
        }
    }
}
