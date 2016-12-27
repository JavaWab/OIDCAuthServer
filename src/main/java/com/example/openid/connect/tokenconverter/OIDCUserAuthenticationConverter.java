package com.example.openid.connect.tokenconverter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

public class OIDCUserAuthenticationConverter implements UserAuthenticationConverter {

	private Collection<? extends GrantedAuthority> defaultAuthorities;
	private UserDetailsService userDetailsService;

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public void setDefaultAuthorities(String[] defaultAuthorities) {
		this.defaultAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(StringUtils.arrayToCommaDelimitedString(defaultAuthorities));
	}

	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		Map response = new LinkedHashMap();
		response.put("user_name", authentication.getName());
		if ((authentication.getAuthorities() != null) && (!(authentication.getAuthorities().isEmpty()))) {
			response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
		}
		return response;
	}

	public Authentication extractAuthentication(Map<String, ?> map) {
		System.out.println("extractAuthentication");
		if (map.containsKey("sub")) {
			Object principal = map.get("sub");
			Collection authorities = getAuthorities(map);
			if (this.userDetailsService != null) {
				UserDetails user = this.userDetailsService.loadUserByUsername((String) map.get("sub"));
				authorities = user.getAuthorities();
				principal = user;
			}
			return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
		}
		return null;
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
		System.out.println("getAuthorities");
		if (!(map.containsKey("authorities"))) {
			return this.defaultAuthorities;
		}
		Object authorities = map.get("authorities");
		if (authorities instanceof String) {
			return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
		}
		if (authorities instanceof Collection) {
			return AuthorityUtils.commaSeparatedStringToAuthorityList(
					StringUtils.collectionToCommaDelimitedString((Collection) authorities));
		}

		throw new IllegalArgumentException("Authorities must be either a String or a Collection");
	}

}