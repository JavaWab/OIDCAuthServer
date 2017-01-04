package com.auth.openid.connect.filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/28.
 */
public class OIDCTokenFilter extends GenericFilterBean implements InitializingBean {

    private JwtTokenStore jwtTokenStore;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String token = request.getHeader("Authorization");

            if (token != null){
                SecurityContextHolder.getContext().setAuthentication(jwtTokenStore.readAuthentication(token.substring(7)));
            }

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Populated SecurityContextHolder with anonymous token: \'" + SecurityContextHolder.getContext().getAuthentication() + "\'");
            }
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("SecurityContextHolder not populated with anonymous token, as it already contained: \'" + SecurityContextHolder.getContext().getAuthentication() + "\'");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public JwtTokenStore getJwtTokenStore() {
        return jwtTokenStore;
    }

    public void setJwtTokenStore(JwtTokenStore jwtTokenStore) {
        this.jwtTokenStore = jwtTokenStore;
    }
}
