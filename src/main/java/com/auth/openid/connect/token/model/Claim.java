package com.auth.openid.connect.token.model;

import com.google.gson.JsonElement;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

/**
 * Claim
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
@Document(collection = "Claim")
public class Claim implements Serializable {

    private static final long serialVersionUID = 769374619701006169L;
    @Id
    private String id;
    private String name;
    private String friendlyName;
    private String claimType;
    private JsonElement value;
    private Set<String> claimTokenFormat;
    private Set<String> issuer;

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

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

    public Set<String> getClaimTokenFormat() {
        return claimTokenFormat;
    }

    public void setClaimTokenFormat(Set<String> claimTokenFormat) {
        this.claimTokenFormat = claimTokenFormat;
    }

    public Set<String> getIssuer() {
        return issuer;
    }

    public void setIssuer(Set<String> issuer) {
        this.issuer = issuer;
    }
}
