package com.example.openid.connect.token.model;

import com.example.openid.connect.token.model.Claim;

import java.util.Collection;
import java.util.Set;

/**
 * Policy
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class Policy {
    private Long id;
    private String name;
    private Collection<Claim> claimsRequired;
    private Set<String> scopes;
}
