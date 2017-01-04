package com.auth.openid.connect.token.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ResourceSet
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class ResourceSet {
    private Long id;
    private String name;
    private String uri;
    private String type;
    private Set<String> scopes = new HashSet<>();
    private String iconUri;

    private String owner; // username of the person responsible for the registration (either directly or via OAuth token)
    private String clientId; // client id of the protected resource that registered this resource set via OAuth token

    private Collection<Policy> policies = new HashSet<>();
}
