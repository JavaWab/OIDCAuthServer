package com.auth.openid.connect.token.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ResourceSet
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
@Document(collection = "ResourceSet")
public class ResourceSet implements Serializable {

    private static final long serialVersionUID = -6443641076855823072L;
    @Id
    private String id;
    private String name;
    private String uri;
    private String type;
    private Set<String> scopes = new HashSet<>();
    private String iconUri;

    private String owner; // username of the person responsible for the registration (either directly or via OAuth token)
    private String clientId; // client id of the protected resource that registered this resource set via OAuth token

    private Collection<Policy> policies = new HashSet<>();

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Collection<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Collection<Policy> policies) {
        this.policies = policies;
    }
}
