package com.example.openid.connect.token.model;

import com.google.gson.JsonElement;

import java.util.Set;

/**
 * Claim
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class Claim {
    private Long id;
    private String name;
    private String friendlyName;
    private String claimType;
    private JsonElement value;
    private Set<String> claimTokenFormat;
    private Set<String> issuer;
}
