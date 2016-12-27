package com.example.openid.connect.service;

import com.example.openid.connect.token.keypair.OIDCKeypair;

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
