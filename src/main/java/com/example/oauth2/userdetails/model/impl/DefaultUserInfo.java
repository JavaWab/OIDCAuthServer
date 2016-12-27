package com.example.oauth2.userdetails.model.impl;

import com.example.oauth2.userdetails.model.Address;
import com.example.oauth2.userdetails.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

/**
 * DefaultUserInfo
 *
 * @author Anbang Wang
 * @date 2016/12/16
 */
@Document(collection = "UserInfo")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultUserInfo implements UserInfo {
    private static final long serialVersionUID = 6078310513185681918L;

    @Id
    private String id;
    @Indexed(unique = true)
    private String sub;
    private String preferredUsername;
    private String name;
    private String password;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String profile;
    private String picture;
    private String website;
    private String email;
    private Boolean emailVerified;
    private String gender;
    private String zoneinfo;
    private String locale;
    private String phoneNumber;
    private Boolean phoneNumberVerified;
    private DefaultAddress address;
    private String updatedTime;
    private String birthdate;
    private List<String> authorities;
    @Transient
    private transient JsonObject src; // source JSON if this is loaded remotely

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getSub() {
        return sub;
    }

    @Override
    public void setSub(String sub) {
        this.sub = sub;
    }

    @Override
    public String getPreferredUsername() {
        return preferredUsername;
    }

    @Override
    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getGivenName() {
        return givenName;
    }

    @Override
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Override
    public String getMiddleName() {
        return middleName;
    }

    @Override
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile =profile;
    }

    @Override
    public String getPicture() {
        return picture;
    }

    @Override
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String getWebsite() {
        return website;
    }

    @Override
    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Boolean getEmailVerified() {
        return emailVerified;
    }

    @Override
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String getZoneinfo() {
        return zoneinfo;
    }

    @Override
    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Boolean getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    @Override
    public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        if (address != null) {
            this.address = new DefaultAddress(address);
        } else {
            this.address = null;
        }
    }

    @Override
    public String getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String getBirthdate() {
        return birthdate;
    }

    @Override
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public void setAddress(DefaultAddress address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public JsonObject toJson() {
        if (src == null) {

            JsonObject obj = new JsonObject();

            obj.addProperty("sub", this.getSub());

            obj.addProperty("name", this.getName());
            obj.addProperty("preferred_username", this.getPreferredUsername());
            obj.addProperty("given_name", this.getGivenName());
            obj.addProperty("family_name", this.getFamilyName());
            obj.addProperty("middle_name", this.getMiddleName());
            obj.addProperty("nickname", this.getNickname());
            obj.addProperty("profile", this.getProfile());
            obj.addProperty("picture", this.getPicture());
            obj.addProperty("website", this.getWebsite());
            obj.addProperty("gender", this.getGender());
            obj.addProperty("zoneinfo", this.getZoneinfo());
            obj.addProperty("locale", this.getLocale());
            obj.addProperty("updated_at", this.getUpdatedTime());
            obj.addProperty("birthdate", this.getBirthdate());
            obj.addProperty("authorities", new JSONArray(this.authorities).toString());
            obj.addProperty("email", this.getEmail());
            obj.addProperty("email_verified", this.getEmailVerified());
            obj.addProperty("password", this.getPassword());
            obj.addProperty("phone_number", this.getPhoneNumber());
            obj.addProperty("phone_number_verified", this.getPhoneNumberVerified());

            if (this.getAddress() != null) {

                JsonObject addr = new JsonObject();
                addr.addProperty("formatted", this.getAddress().getFormatted());
                addr.addProperty("street_address", this.getAddress().getStreetAddress());
                addr.addProperty("locality", this.getAddress().getLocality());
                addr.addProperty("region", this.getAddress().getRegion());
                addr.addProperty("postal_code", this.getAddress().getPostalCode());
                addr.addProperty("country", this.getAddress().getCountry());

                obj.add("address", addr);
            }

            return obj;
        } else {
            return src;
        }
    }
    public static UserInfo fromJson(JsonObject obj) {
        DefaultUserInfo ui = new DefaultUserInfo();
        ui.setSource(obj);

        ui.setSub(nullSafeGetString(obj, "sub"));

        ui.setName(nullSafeGetString(obj, "name"));
        ui.setPreferredUsername(nullSafeGetString(obj, "preferred_username"));
        ui.setGivenName(nullSafeGetString(obj, "given_name"));
        ui.setFamilyName(nullSafeGetString(obj, "family_name"));
        ui.setMiddleName(nullSafeGetString(obj, "middle_name"));
        ui.setNickname(nullSafeGetString(obj, "nickname"));
        ui.setProfile(nullSafeGetString(obj, "profile"));
        ui.setPicture(nullSafeGetString(obj, "picture"));
        ui.setWebsite(nullSafeGetString(obj, "website"));
        ui.setGender(nullSafeGetString(obj, "gender"));
        ui.setZoneinfo(nullSafeGetString(obj, "zoneinfo"));
        ui.setLocale(nullSafeGetString(obj, "locale"));
        ui.setUpdatedTime(nullSafeGetString(obj, "updated_at"));
        ui.setBirthdate(nullSafeGetString(obj, "birthdate"));

        ui.setEmail(nullSafeGetString(obj, "email"));
        ui.setEmailVerified(obj.has("email_verified") && obj.get("email_verified").isJsonPrimitive() ? obj.get("email_verified").getAsBoolean() : null);

        ui.setPhoneNumber(nullSafeGetString(obj, "phone_number"));
        ui.setPhoneNumberVerified(obj.has("phone_number_verified") && obj.get("phone_number_verified").isJsonPrimitive() ? obj.get("phone_number_verified").getAsBoolean() : null);

        if (obj.has("address") && obj.get("address").isJsonObject()) {
            JsonObject addr = obj.get("address").getAsJsonObject();
            ui.setAddress(new DefaultAddress());

            ui.getAddress().setFormatted(nullSafeGetString(addr, "formatted"));
            ui.getAddress().setStreetAddress(nullSafeGetString(addr, "street_address"));
            ui.getAddress().setLocality(nullSafeGetString(addr, "locality"));
            ui.getAddress().setRegion(nullSafeGetString(addr, "region"));
            ui.getAddress().setPostalCode(nullSafeGetString(addr, "postal_code"));
            ui.getAddress().setCountry(nullSafeGetString(addr, "country"));

        }


        return ui;

    }
    @Override
    public JsonObject getSource() {
        return src;
    }

    public void setSource(JsonObject src) {
        this.src = src;
    }

    private static String nullSafeGetString(JsonObject obj, String field) {
        return obj.has(field) && obj.get(field).isJsonPrimitive() ? obj.get(field).getAsString() : null;
    }

}
