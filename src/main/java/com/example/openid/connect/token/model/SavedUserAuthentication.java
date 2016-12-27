package com.example.openid.connect.token.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * SavedUserAuthentication
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class SavedUserAuthentication implements Authentication {
    private Long id;

    private String name;

    private Collection<? extends GrantedAuthority> authorities;

    private boolean authenticated;

    private String sourceClass;

    public SavedUserAuthentication(Authentication src) {
        setName(src.getName());
        setAuthorities(src.getAuthorities());
        setAuthenticated(src.isAuthenticated());

        if (src instanceof SavedUserAuthentication) {
            // if we're copying in a saved auth, carry over the original class name
            setSourceClass(((SavedUserAuthentication) src).getSourceClass());
        } else {
            setSourceClass(src.getClass().getName());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getName();
    }
}
