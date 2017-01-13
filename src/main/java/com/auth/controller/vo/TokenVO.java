package com.auth.controller.vo;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * TokenVO
 *
 * @author Anbang Wang
 * @date 2016/12/19
 */

public class TokenVO implements Serializable {
    private static final long serialVersionUID = 4512873694756755815L;
    @NotEmpty(message = "Missing grant_type")
    private String grant_type;
    private String code;
    private String redirect_uri;
    private String client_id;
    private String client_secret;
    private String username;
    private String password;
    private String state;
    private String refresh_token;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Map<String, String> toMap(){
        Map<String, String> map =  new HashMap<>();
        if (this.code != null) {
            map.put("code", this.code);
        }

        if (this.redirect_uri != null) {
            map.put("redirect_uri", this.redirect_uri);
        }

        if (this.client_id != null) {
            map.put("client_id", this.client_id);
        }

        if (this.client_secret != null) {
            map.put("client_secret",this.client_secret);
        }

        if (this.grant_type != null) {
            map.put("grant_type", this.grant_type);
        }

        if (this.username != null) {
            map.put("username", this.username);
        }

        if (this.password != null) {
            map.put("password", this.password);
        }

        if (this.state != null) {
            map.put("state", this.state);
        }

        if (this.refresh_token != null) {
            map.put("refresh_token", this.refresh_token);
        }
        return map;
    }
}
