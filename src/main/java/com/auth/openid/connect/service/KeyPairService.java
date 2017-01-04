package com.auth.openid.connect.service;

import com.auth.openid.connect.token.keypair.OIDCKeypair;

/**
 * KeyPairService
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public interface KeyPairService {
    public void saveKeyPair(OIDCKeypair keypair);
    public OIDCKeypair getKeyPair(String kid);
}
