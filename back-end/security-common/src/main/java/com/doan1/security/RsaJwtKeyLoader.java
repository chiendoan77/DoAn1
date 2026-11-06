package com.doan1.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaJwtKeyLoader {
    private RsaJwtKeyLoader() {
    }

    public static PrivateKey privateKey(String base64Der) {
        try {
            byte[] encoded = Base64.getDecoder().decode(base64Der);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception exception) {
            throw new IllegalArgumentException("JWT private key must be a Base64-encoded PKCS#8 RSA key.", exception);
        }
    }

    public static PublicKey publicKey(String base64Der) {
        try {
            byte[] encoded = Base64.getDecoder().decode(base64Der);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (Exception exception) {
            throw new IllegalArgumentException("JWT public key must be a Base64-encoded X.509 RSA key.", exception);
        }
    }
}
