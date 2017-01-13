package com.auth.oauth2.clientdetails;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * WXBaseClientDetails
 *
 * @author Anbang Wang
 * @date 2016/12/13
 */
@Document(collection = "WXBaseClientDetails")
public class WXBaseClientDetails extends BaseClientDetails{
    private static final long serialVersionUID = -219437707076642278L;
    private boolean clearAccessTokensOnRefresh = true; // do we clear access tokens on refresh?
    private boolean reuseRefreshToken = true; // do we let someone reuse a refresh token?

    public boolean isAllowRefresh() {
        if (getAuthorizedGrantTypes() != null) {
            return getAuthorizedGrantTypes().contains("refresh_token");
        } else {
            return false; // if there are no grants, we can't be refreshing them, can we?
        }
    }

    public boolean isClearAccessTokensOnRefresh() {
        return clearAccessTokensOnRefresh;
    }

    public void setClearAccessTokensOnRefresh(boolean clearAccessTokensOnRefresh) {
        this.clearAccessTokensOnRefresh = clearAccessTokensOnRefresh;
    }

    public boolean isReuseRefreshToken() {
        return reuseRefreshToken;
    }

    public void setReuseRefreshToken(boolean reuseRefreshToken) {
        this.reuseRefreshToken = reuseRefreshToken;
    }
}
