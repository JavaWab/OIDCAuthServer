package com.auth.openid.connect.token.keypair;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * OIDCKeypair
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
@Document(collection = "OIDCKeypair")
public class OIDCKeypair implements Serializable {
    private static final long serialVersionUID = 1931711788771752764L;
    private String kid;
    private String privateKey;
    private String publicKey;

    public OIDCKeypair() {
    }

    public OIDCKeypair(String kid, String privateKey, String publicKey) {
        this.kid = kid;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
