package com.auth.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * WebSecurityConfig
 *
 * @author Anbang Wang
 * @date 2016/12/13
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//允许进入页面方法前检验
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("filterRegistrationBean")
    private FilterRegistrationBean filterRegistrationBean;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(filterRegistrationBean.getFilter(), AnonymousAuthenticationFilter.class);
        http.authorizeRequests()
            .antMatchers(
//                StaticParams.PATHREGX.API,
                StaticParams.PATHREGX.CSS,
                StaticParams.PATHREGX.JS,
                StaticParams.PATHREGX.IMG,
                "/api/token",
                "/oauth/token").permitAll()
            .antMatchers("/api/userinfo").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
            .anyRequest().authenticated()
            .and()
            .formLogin().permitAll()
            .and()
            .logout().permitAll()
            .and()
            .httpBasic().disable()
            .csrf().disable();

    }
}
