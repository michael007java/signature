package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.spi.PreSignRequest;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhengwei on 5/13/15.
 * 签名所需参数，主要用于标记第三方app信息，签名算法，签名相关等。
 *
 * 示例：
 * Authorization : OAuth app_id=dpf43f3p2l4k3l03, signature_method=HMAC-SHA1, version=1.0, signature=QtTEQJNlixi9vdvNsBr6C7cyNRw=
 */

public class Parameters extends HashMap<String, String> {

    public static final String AUTHORIZATION_HEADER = "Authorization";  //签名header name

    public static final String SCHEME = "OAuth";  //签名header value前缀

    public static final String APP_ID = "app_id";  //第三方应用唯一标识

    public static final String SIGNATURE_METHOD = "signature_method";  //签名算法

    public static final String SIGNATURE = "signature";  //签名

    public static final String VERSION = "version";  //车车签名算法版本

    private static final String SCHEME_SPACE = SCHEME + ' ';


    public String getAppId() {
        return get(APP_ID);
    }

    public void setAppId(String appId) {
        put(APP_ID, appId);
    }

    public Parameters appId(String appId) {
        setAppId(appId);
        return this;
    }

    public String getSignatureMethod() {
        return get(SIGNATURE_METHOD);
    }

    public void setSignatureMethod(String signatureMethod) {
        put(SIGNATURE_METHOD, signatureMethod);
    }

    public Parameters signatureMethod(String signatureMethod) {
        setSignatureMethod(signatureMethod);
        return this;
    }

    public String getSignature() {
        return get(SIGNATURE);
    }

    public void setSignature(String signature) {
        put(SIGNATURE, signature);
    }

    public Parameters signature(String signature) {
        setSignature(signature);
        return this;
    }


    public String getVersion() {
        return get(VERSION);
    }

    public void setVersion(String version) {
        put(VERSION, version);
    }

    public Parameters version(String version) {
        setVersion(version);
        return this;
    }

    public void setVersion() {
        setVersion("1.0");
    }

    public Parameters version() {
        setVersion();
        return this;
    }

    public Parameters readRequest(PreSignRequest request) {

        List<String> headers = request.getHeaderValues(AUTHORIZATION_HEADER);
        if (headers == null) { return this; }

        for (String header : headers) {
            if (!header.regionMatches(true, 0, SCHEME_SPACE, 0, SCHEME_SPACE.length())) {
                continue;
            }
            for (String param : header.substring(SCHEME_SPACE.length()).trim().split(",")) {
                String[] nv = param.split("=", 2);
                if (nv.length != 2) {
                    continue;
                }
                put(nv[0].trim(), nv[1].trim());
            }
        }

        return this;
    }

    public Parameters writeRequest(PreSignRequest request) {
        StringBuilder buf = new StringBuilder(SCHEME);

        boolean comma = false;
        for (String key : keySet()) {
            String value = get(key);
            if (value == null) {
                continue;
            }
            buf.append(comma ? ", " : " ").append(key);
            buf.append("=").append(value);

            comma = true;
        }
        request.addHeaderValue(AUTHORIZATION_HEADER, buf.toString());
        return this;
    }
}

