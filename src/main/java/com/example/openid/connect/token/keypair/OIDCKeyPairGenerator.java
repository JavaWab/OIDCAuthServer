package com.example.openid.connect.token.keypair;

import com.example.openid.connect.service.impl.DefaultKeyPairService;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.codec.Codecs;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OIDCKeyPairGenerator
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
@Component
public class OIDCKeyPairGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(OIDCKeyPairGenerator.class);
    @Autowired
    private DefaultKeyPairService defaultKeyPairService;

    public OIDCKeyPairGenerator() {
    }

    public OIDCKeypair generate() {
        String keyId = String.valueOf(generateNumber());

        RsaJsonWebKey jwk;
        try {
            jwk = RsaJwkGenerator.generateJwk(2048);
        } catch (JoseException var6) {
            throw new IllegalStateException(var6);
        }

        jwk.setKeyId(keyId);
        jwk.setAlgorithm("RS256");

        String privateKey = jwk.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE);
        String publicKey = jwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);
        if (defaultKeyPairService.getKeyPair("default_kid") == null) {
            defaultKeyPairService.saveKeyPair(new OIDCKeypair("default_kid", privateKey, publicKey));
        } else {
            defaultKeyPairService.saveKeyPair(new OIDCKeypair(keyId, privateKey, publicKey));
        }

        LOG.info("Generate new OIDCKeyPair: keyId: {} privateKey: {} publicKey: {}", new Object[]{keyId, privateKey, publicKey});

        return new OIDCKeypair(keyId, privateKey, publicKey);
    }

    public KeyPair getDefaultKeyPair() {
        OIDCKeypair keyPair = defaultKeyPairService.getKeyPair("default_kid");
        if (keyPair != null) {
            return parseKeyPair(keyPair);
        } else {
            return parseKeyPair(generate());
        }
    }

    public KeyPair getKeyPair(String kid) {
        OIDCKeypair keyPair = defaultKeyPairService.getKeyPair(kid);
        if (keyPair != null) {
            return parseKeyPair(keyPair);
        }
        return null;
    }

    public long generateNumber() {
        long number;
        do {
            number = UUID.randomUUID().getMostSignificantBits();
        } while (number <= 0L);

        return number;
    }

    public KeyPair parseKeyPair(String pemData) {
        Pattern PEM_DATA = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", 32);
        Matcher m = PEM_DATA.matcher(pemData.trim());
        if (!m.matches()) {
            throw new IllegalArgumentException("String is not PEM encoded data");
        } else {
            String type = m.group(1);
            byte[] content = Codecs.b64Decode(Codecs.utf8Encode(m.group(2)));
            PrivateKey privateKey = null;

            try {
                KeyFactory e = KeyFactory.getInstance("RSA");
                PublicKey publicKey;
                ASN1Sequence seq;
                RSAPublicKeySpec pubSpec;
                if (type.equals("RSA PRIVATE KEY")) {
                    seq = ASN1Sequence.getInstance(content);
                    if (seq.size() != 9) {
                        throw new IllegalArgumentException("Invalid RSA Private Key ASN1 sequence.");
                    }

                    org.bouncycastle.asn1.pkcs.RSAPrivateKey key = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(seq);
                    pubSpec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
                    RSAPrivateCrtKeySpec privSpec = new RSAPrivateCrtKeySpec(key.getModulus(), key.getPublicExponent(), key.getPrivateExponent(), key.getPrime1(), key.getPrime2(), key.getExponent1(), key.getExponent2(), key.getCoefficient());
                    publicKey = e.generatePublic(pubSpec);
                    privateKey = e.generatePrivate(privSpec);
                } else if (type.equals("PUBLIC KEY")) {
                    X509EncodedKeySpec seq1 = new X509EncodedKeySpec(content);
                    publicKey = e.generatePublic(seq1);
                } else {
                    if (!type.equals("RSA PUBLIC KEY")) {
                        throw new IllegalArgumentException(type + " is not a supported format");
                    }

                    seq = ASN1Sequence.getInstance(content);
                    RSAPublicKey key1 = RSAPublicKey.getInstance(seq);
                    pubSpec = new RSAPublicKeySpec(key1.getModulus(), key1.getPublicExponent());
                    publicKey = e.generatePublic(pubSpec);
                }

                return new KeyPair(publicKey, privateKey);
            } catch (InvalidKeySpecException var11) {
                throw new RuntimeException(var11);
            } catch (NoSuchAlgorithmException var12) {
                throw new IllegalStateException(var12);
            }
        }
    }

    private KeyPair parseKeyPair(OIDCKeypair oidcKeypair) {
        try {
            Map<String, Object> privateKeyMap = JsonUtil.parseJson(oidcKeypair.getPrivateKey());
            Map<String, Object> publicKeyMap = JsonUtil.parseJson(oidcKeypair.getPublicKey());

            PrivateKey privateKey = new RsaJsonWebKey(privateKeyMap).getRsaPrivateKey();
            PublicKey publicKey = new RsaJsonWebKey(publicKeyMap).getPublicKey();

            return new KeyPair(publicKey, privateKey);
        } catch (JoseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
