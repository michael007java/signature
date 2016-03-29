package com.cheche365.cheche.signature.api;

import com.cheche365.cheche.signature.Base64;
import com.cheche365.cheche.signature.Secrets;
import com.cheche365.cheche.signature.spi.APISignatureMethod;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Created by zhengwei on 5/16/15.
 */

public class HMAC_SHA1 implements APISignatureMethod {

    public static final String NAME = "HMAC-SHA1";

    private static final String SIGNATURE_ALGORITHM = "HmacSHA1";

    public HMAC_SHA1() {
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String sign(String elements, Secrets secrets) {

        Mac mac;

        try {
            mac = Mac.getInstance(SIGNATURE_ALGORITHM);
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }


        byte[] key;

        try {
            key = secrets.getAppSecret().toString().getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }

        SecretKeySpec spec = new SecretKeySpec(key, SIGNATURE_ALGORITHM);

        try {
            mac.init(spec);
        }
        catch (InvalidKeyException ike) {
            throw new IllegalStateException(ike);
        }

        return Base64.encode(mac.doFinal(elements.getBytes()));
    }

    @Override
    public boolean verify(String elements, Secrets secrets, String signature) {
        return sign(elements, secrets).equals(signature);
    }
}

