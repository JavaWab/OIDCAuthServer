package com.example.controller.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * TokenVO
 *
 * @author Anbang Wang
 * @date 2016/12/19
 */
public class TokenVO {
    private String grant_type;
    private String code;
    private String redirect_uri;
    private String client_id;
    private String client_secret;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public Map<String, String> toMap(){
        Map<String, String> map =  new HashMap<>();
        map.put("code", code);
        map.put("redirect_uri", redirect_uri);
        map.put("client_id", client_id);
        map.put("client_secret",client_secret);
        map.put("grant_type", grant_type);
        return map;
    }
}
