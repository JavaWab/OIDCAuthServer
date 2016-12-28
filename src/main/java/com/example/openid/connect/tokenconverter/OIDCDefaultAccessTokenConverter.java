package com.example.openid.connect.tokenconverter;

import com.google.common.base.Splitter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.*;

public class OIDCDefaultAccessTokenConverter implements AccessTokenConverter {

	private UserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();

	private boolean includeGrantType;

	/**
	 * Converter for the part of the data in the token representing a user.
	 * 
	 * @param userTokenConverter
	 *            the userTokenConverter to set
	 */
	public void setUserTokenConverter(UserAuthenticationConverter userTokenConverter) {
		this.userTokenConverter = userTokenConverter;
	}

	/**
	 * Flag to indicate the the grant type should be included in the converted
	 * token.
	 * 
	 * @param includeGrantType
	 *            the flag value (default false)
	 */
	public void setIncludeGrantType(boolean includeGrantType) {
		this.includeGrantType = includeGrantType;
	}

	public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		Map<String, Object> response = new HashMap<String, Object>();
		OAuth2Request clientToken = authentication.getOAuth2Request();

		if (!authentication.isClientOnly()) {
			response.putAll(userTokenConverter.convertUserAuthentication(authentication.getUserAuthentication()));
		} else {
			if (clientToken.getAuthorities() != null && !clientToken.getAuthorities().isEmpty()) {
				response.put(UserAuthenticationConverter.AUTHORITIES,
						AuthorityUtils.authorityListToSet(clientToken.getAuthorities()));
			}
		}

		if (token.getScope() != null) {
			response.put(SCOPE, token.getScope());
		}
		if (token.getAdditionalInformation().containsKey(JTI)) {
			response.put(JTI, token.getAdditionalInformation().get(JTI));
		}

		if (token.getExpiration() != null) {
			response.put(EXP, token.getExpiration().getTime() / 1000);
		}

		if (includeGrantType && authentication.getOAuth2Request().getGrantType() != null) {
			response.put(GRANT_TYPE, authentication.getOAuth2Request().getGrantType());
		}

		response.putAll(token.getAdditionalInformation());

		response.put(CLIENT_ID, clientToken.getClientId());
		if (clientToken.getResourceIds() != null && !clientToken.getResourceIds().isEmpty()) {
			response.put(AUD, clientToken.getResourceIds());
		}
		return response;
	}

	public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(value);
		Map info = new HashMap(map);
		info.remove("exp");
		info.remove("aud");
		info.remove("client_id");
		info.remove("scope");
		if (map.containsKey("exp")) {
			token.setExpiration(new Date(((Long) map.get("exp")).longValue() * 1000L));
		}
		if (map.containsKey("jti")) {
			info.put("jti", map.get("jti"));
		}

		Object scope = map.get("scope");

		if (scope != null) {
			if (scope instanceof Collection) {
				token.setScope(new HashSet((Collection) scope));
			} else {
				token.setScope(new HashSet(Splitter.on(",").trimResults().splitToList((String) scope)));
			}
		}
		token.setAdditionalInformation(info);
		return token;
	}

	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		Map<String, String> parameters = new HashMap<String, String>();

		Object mapScope = map.get(SCOPE);
		Collection scopeCollection = null;
		if (mapScope != null) {
			if (mapScope instanceof Collection) {
				scopeCollection = new HashSet((Collection) mapScope);
			} else {
				scopeCollection = new HashSet(Splitter.on(" ").trimResults().splitToList((String) mapScope));
			}
		}

		Set<String> scope = new LinkedHashSet<String>(map.containsKey(SCOPE) ? scopeCollection : Collections.<String> emptySet());
		Authentication user = userTokenConverter.extractAuthentication(map);
		String clientId = (String) map.get(CLIENT_ID);
		parameters.put(CLIENT_ID, clientId);
		if (includeGrantType && map.containsKey(GRANT_TYPE)) {
			parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
		}
		Set<String> resourceIds = new LinkedHashSet<String>(map.containsKey(AUD) ? getAudience(map) : Collections.<String> emptySet());

		Collection<? extends GrantedAuthority> authorities = null;
		if (user == null && map.containsKey(AUTHORITIES)) {
			String[] roles = ((Collection<String>) map.get(AUTHORITIES)).toArray(new String[0]);
			authorities = AuthorityUtils.createAuthorityList(roles);
		}
		OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, null,
				null, null);
		return new OAuth2Authentication(request, user);
	}

	private Collection<String> getAudience(Map<String, ?> map) {
		Object auds = map.get(AUD);
		if (auds instanceof Collection) {
			Collection<String> result = (Collection<String>) auds;
			return result;
		}
		return Collections.singleton((String) auds);
	}

}