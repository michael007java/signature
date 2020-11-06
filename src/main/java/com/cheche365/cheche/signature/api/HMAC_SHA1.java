package com.cheche365.cheche.signature.api;

import com.cheche365.cheche.signature.Base64;
import com.cheche365.cheche.signature.Secrets;
import com.cheche365.cheche.signature.spi.APISignatureMethod;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/***************************************************************************/
/*                              HMAC_SHA1.java                 */
/*   文   件 名: HMAC_SHA1.java                                  */
/*   模  块： 车险ToB平台                                                */
/*   功  能:  车车api签名实现算法                            */
/*   初始创建:2015/5/16                                            */
/*   版本更新:V1.0                                                         */
/*   版权所有:北京车与车科技有限公司                                       */
/***************************************************************************/

/**
 * Created by zhengwei on 5/16/15.
 */

public class HMAC_SHA1 implements APISignatureMethod {

    private static final Logger LOGGER = Logger.getLogger(HMAC_SHA1.class.getName());

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

        String sign = Base64.encode(mac.doFinal(elements.getBytes()));
        LOGGER.info(" sign result : " + sign);
        return sign;
    }

    @Override
    public boolean verify(String elements, Secrets secrets, String signature) throws Exception {
        LOGGER.info("verify signature : " + signature);
        String calculateSign = sign(elements, secrets);
        LOGGER.info("signature elements : " + elements);
        LOGGER.info("calculate signature : " + calculateSign);
        boolean result = calculateSign.equals(signature);
        if (!result) {
            LOGGER.severe("数字验签失败，elements：" + elements + "；secrets：" + secrets + "；signature：" + signature);
        }
        return result;
    }
}

