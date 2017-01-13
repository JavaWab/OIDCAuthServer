package com.auth.openid.connect.token.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

/**
 * Permission
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
@Document(collection = "Permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = -9043336787379883755L;
    @Id
    private String id;
    private ResourceSet resourceSet;
    private Set<String> scopes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    public void setResourceSet(ResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }
}
