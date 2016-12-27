package com.example.oauth2.clientdetails;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * WXBaseClientDetails
 *
 * @author Anbang Wang
 * @date 2016/12/13
 */
@Document(collection = "baseClientDetails")
public class WXBaseClientDetails extends BaseClientDetails{
}
