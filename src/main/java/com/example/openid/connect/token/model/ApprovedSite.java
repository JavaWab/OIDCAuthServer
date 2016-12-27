package com.example.openid.connect.token.model;

import java.util.Date;
import java.util.Set;

/**
 * ApprovedSite
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class ApprovedSite {
    // unique id
    private Long id;

    // which user made the approval
    private String userId;

    // which OAuth2 client is this tied to
    private String clientId;

    // when was this first approved?
    private Date creationDate;

    // when was this last accessed?
    private Date accessDate;

    // if this is a time-limited access, when does it run out?
    private Date timeoutDate;

    // what scopes have been allowed
    // this should include all information for what data to access
    private Set<String> allowedScopes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

    public Date getTimeoutDate() {
        return timeoutDate;
    }

    public void setTimeoutDate(Date timeoutDate) {
        this.timeoutDate = timeoutDate;
    }

    public Set<String> getAllowedScopes() {
        return allowedScopes;
    }

    public void setAllowedScopes(Set<String> allowedScopes) {
        this.allowedScopes = allowedScopes;
    }
}
