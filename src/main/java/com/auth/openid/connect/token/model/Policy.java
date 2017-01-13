package com.auth.openid.connect.token.model;

import java.util.Collection;
import java.util.Set;

/**
 * Policy
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class Policy {
    private String id;
    private String name;
    private Collection<Claim> claimsRequired;
    private Set<String> scopes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Claim> getClaimsRequired() {
        return claimsRequired;
    }

    public void setClaimsRequired(Collection<Claim> claimsRequired) {
        this.claimsRequired = claimsRequired;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }
}
