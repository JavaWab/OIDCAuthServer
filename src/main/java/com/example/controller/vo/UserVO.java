package com.example.controller.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * UserVO
 *
 * @author Anbang Wang
 * @date 2016/12/16
 */
public class UserVO {
    @NotNull(message = "username is null")
    private String username;
    @NotNull(message = "username is null")
    private String nickname;
    @NotNull(message = "password is null")
    private String password;
    @NotNull(message = "email is null")
    @JsonProperty("email")
    private String email;

    private String icon;
    @NotNull(message = "gender is null")
    private String gender;
    @NotNull(message = "birthday is null")
    private Date birthday;

    @JsonCreator
    public UserVO(@JsonProperty("login_name") String loginName, @JsonProperty("nick_name") String nickname, @JsonProperty("password") String password, @JsonProperty("email") String email, @JsonProperty("gender") String gender, @JsonProperty("birthday") Date birthday) {
        this.username = loginName;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
