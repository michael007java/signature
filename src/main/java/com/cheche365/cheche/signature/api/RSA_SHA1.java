package com.cheche365.cheche.signature.api;

import com.cheche365.cheche.signature.Base64;
import com.cheche365.cheche.signature.InvalidSecretException;
import com.cheche365.cheche.signature.Secrets;
import com.cheche365.cheche.signature.spi.APISignatureMethod;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by zhengwei on 5/15/15.
 */

public class RSA_SHA1 implements APISignatureMethod {

    public static final String NAME = "RSA-SHA1";

    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    
    private static final String KEY_TYPE = "RSA";

    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE";

    public RSA_SHA1() {
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String sign(String elements, Secrets secrets) {
    
        Signature sig;

        try {
            sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        byte[] decodedPrivKey;

        try {
            decodedPrivKey = Base64.decode(secrets.getAppSecret());
        }
        catch (IOException ioe) {
            throw new InvalidSecretException("读取私钥异常");
        }

        KeyFactory keyf;

        try {
            keyf = KeyFactory.getInstance(KEY_TYPE);
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivKey);
        
        RSAPrivateKey rsaPrivKey;

        try {
            rsaPrivKey = (RSAPrivateKey) keyf.generatePrivate(keySpec);
        }
        catch (InvalidKeySpecException ikse) {
            throw new IllegalStateException(ikse);
        }

        try {
            sig.initSign(rsaPrivKey);
        }
        catch (InvalidKeyException ike) {
            throw new IllegalStateException(ike);
        }

        try {
            sig.update(elements.getBytes());
        }
        catch (SignatureException se) {
            throw new IllegalStateException(se);
        }

        byte[] rsasha1;

        try {
            rsasha1 = sig.sign();
        }
        catch (SignatureException se) {
            throw new IllegalStateException(se);
        }

        return Base64.encode(rsasha1);
    }

    @Override
    public boolean verify(String elements, Secrets secrets, String signature) throws Exception {

        Signature sig;

        try {
            sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        RSAPublicKey rsaPubKey = null;

        String pubKey = secrets.getAppSecret();
        /*if (tmpkey.startsWith(BEGIN_CERT)) {
           *//* try {
                Certificate cert = null;
                ByteArrayInputStream bais = new ByteArrayInputStream(tmpkey.getBytes());
                BufferedInputStream bis = new BufferedInputStream(bais);
                CertificateFactory certfac = CertificateFactory.getInstance("X.509");
                while (bis.available() > 0) {
                    cert = certfac.generateCertificate(bis);
                }
                rsaPubKey = (RSAPublicKey) cert.getPublicKey();
            } catch (IOException ex) {
                Logger.getLogger(RSA_SHA1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CertificateException ex) {
                Logger.getLogger(RSA_SHA1.class.getName()).log(Level.SEVERE, null, ex);
            }*//*

        }*/
            byte[] keyBytes = Base64.decode(pubKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            rsaPubKey = (RSAPublicKey)keyFactory.generatePublic(spec);

        byte[] decodedSignature;

        try {
            decodedSignature = Base64.decode(signature);
        }
        catch (IOException ioe) {
            return false;
        }

        try {
            sig.initVerify(rsaPubKey);
        }
        catch (InvalidKeyException ike) {
            throw new IllegalStateException(ike);
        }

        try {
            sig.update(elements.getBytes());
        }
        catch (SignatureException se) {
            throw new IllegalStateException(se);
        }
        
        try {
            return sig.verify(decodedSignature);
        }
        catch (SignatureException se) {
            throw new IllegalStateException(se);
        }
    }
}
