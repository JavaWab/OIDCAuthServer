package com.example.openid.connect.token.model;

import java.util.Set;

/**
 * Permission
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class Permission {
    private Long id;
    private ResourceSet resourceSet;
    private Set<String> scopes;
}
