package com.auth.openid.connect.token.model;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Requirement;

/**
 * PKCEAlgorithm
 *
 * @author Anbang Wang
 * @date 2016/12/15
 */
public class PKCEAlgorithm extends Algorithm {
    public static final PKCEAlgorithm plain = new PKCEAlgorithm("plain", Requirement.REQUIRED);

    public static final PKCEAlgorithm S256 = new PKCEAlgorithm("S256", Requirement.OPTIONAL);

    public PKCEAlgorithm(String name, Requirement req) {
        super(name, req);
    }

    public PKCEAlgorithm(String name) {
        super(name, null);
    }

    public static PKCEAlgorithm parse(final String s) {
        if (s.equals(plain.getName())) {
            return plain;
        } else if (s.equals(S256.getName())) {
            return S256;
        } else {
            return new PKCEAlgorithm(s);
        }
    }
}
