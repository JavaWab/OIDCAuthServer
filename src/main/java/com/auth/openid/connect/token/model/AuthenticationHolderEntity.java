package com.auth.openid.connect.token.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * AuthenticationHolderEntity
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class AuthenticationHolderEntity implements Serializable{
    private static final long serialVersionUID = -3199375371702779439L;

    private String id;

    private SavedUserAuthentication userAuth;

    private Collection<? extends GrantedAuthority> authorities;

    private Set<String> resourceIds;

    private boolean approved;

    private String redirectUri;

    private Set<String> responseTypes;

    private Map<String, Serializable> extensions;

    private String clientId;

    private Set<String> scope;

    private Map<String, String> requestParameters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserAuth(SavedUserAuthentication userAuth) {
        this.userAuth = userAuth;
    }

    public SavedUserAuthentication getUserAuth() {
        return userAuth;
    }

    public OAuth2Authentication getAuthentication() {
        return new OAuth2Authentication(createOAuth2Request(), getUserAuth());
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        OAuth2Request o2Request = authentication.getOAuth2Request();
        setAuthorities(o2Request.getAuthorities());
        setClientId(o2Request.getClientId());
        setExtensions(o2Request.getExtensions());
        setRedirectUri(o2Request.getRedirectUri());
        setRequestParameters(o2Request.getRequestParameters());
        setResourceIds(o2Request.getResourceIds());
        setResponseTypes(o2Request.getResponseTypes());
        setScope(o2Request.getScope());
        setApproved(o2Request.isApproved());

        if (authentication.getUserAuthentication() != null) {
            this.userAuth = new SavedUserAuthentication(authentication.getUserAuthentication());
        } else {
            this.userAuth = null;
        }
    }
    private OAuth2Request createOAuth2Request() {
        return new OAuth2Request(requestParameters, clientId, authorities, approved, scope, resourceIds, redirectUri, responseTypes, extensions);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public Set<String> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(Set<String> responseTypes) {
        this.responseTypes = responseTypes;
    }

    public Map<String, Serializable> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Serializable> extensions) {
        this.extensions = extensions;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }
}
