package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.api.HMAC_SHA1;
import com.cheche365.cheche.signature.api.RSA_SHA1;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by zhengwei on 5/16/15.
 * 车车API签名／验签单元测试类。测试支持两种算法：
 * HMAC_SHA1 : 签名时先做SHA1消息摘要，然后用密钥签名。验签顺序一致。对称加密算法，签名／验签密钥一致。
 * RSA_SHA1 : 签名时先做SHA1消息摘要，然后用私钥签名。验签时用公钥验证。非对称加密算法，签名／验签密钥不一致。
 *
 * 签名示例：
 * Authorization : OAuth app_id=dpf43f3p2l4k3l03, signature_method=HMAC-SHA1, version=1.0, signature=QtTEQJNlixi9vdvNsBr6C7cyNRw=
 * Authorization : OAuth app_id=dpf43f3p2l4k3l03, signature_method=RSA-SHA1, version=1.0, signature=OcQZeEsWBVwoZ+Z3GpQRMl5IMUwTICwLSP14trQN8GnX6jPg8yLNt8UloA7cuQPtHmJqq6KGLLQdi9FOleJIv/Q7CsikT9uO3qPnkhyuq5hTxOaJCmuPJr8jHLRv5aev60ORk92t9uA/oY2XX9yYwLRXH87ajk+yXsejvBGmjig=, timestamp=1196666512
 */
public class SignatureTest extends TestCase {

    public static final String APP_ID = "dpf43f3p2l4k3l03";
    public static final String HMAC_APP_SECRET = "kd94hf93k423kf44";
    public static final String SIGNATURE_METHOD = HMAC_SHA1.NAME;
    public static final String VERSION = "1.0";

    public static final String HMAC_SIGNATURE = "Dr/gekCNJ94JLvRAcegeG2nrpvI=";

    public static final String RSA_PRIVATE_KEY =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALRiMLAh9iimur8V" +
            "A7qVvdqxevEuUkW4K+2KdMXmnQbG9Aa7k7eBjK1S+0LYmVjPKlJGNXHDGuy5Fw/d" +
            "7rjVJ0BLB+ubPK8iA/Tw3hLQgXMRRGRXXCn8ikfuQfjUS1uZSatdLB81mydBETlJ" +
            "hI6GH4twrbDJCR2Bwy/XWXgqgGRzAgMBAAECgYBYWVtleUzavkbrPjy0T5FMou8H" +
            "X9u2AC2ry8vD/l7cqedtwMPp9k7TubgNFo+NGvKsl2ynyprOZR1xjQ7WgrgVB+mm" +
            "uScOM/5HVceFuGRDhYTCObE+y1kxRloNYXnx3ei1zbeYLPCHdhxRYW7T0qcynNmw" +
            "rn05/KO2RLjgQNalsQJBANeA3Q4Nugqy4QBUCEC09SqylT2K9FrrItqL2QKc9v0Z" +
            "zO2uwllCbg0dwpVuYPYXYvikNHHg+aCWF+VXsb9rpPsCQQDWR9TT4ORdzoj+Nccn" +
            "qkMsDmzt0EfNaAOwHOmVJ2RVBspPcxt5iN4HI7HNeG6U5YsFBb+/GZbgfBT3kpNG" +
            "WPTpAkBI+gFhjfJvRw38n3g/+UeAkwMI2TJQS4n8+hid0uus3/zOjDySH3XHCUno" +
            "cn1xOJAyZODBo47E+67R4jV1/gzbAkEAklJaspRPXP877NssM5nAZMU0/O/NGCZ+" +
            "3jPgDUno6WbJn5cqm8MqWhW1xGkImgRk+fkDBquiq4gPiT898jusgQJAd5Zrr6Q8" +
            "AO/0isr/3aa6O6NLQxISLKcPDk2NOccAfS/xOtfOz4sJYM3+Bs4Io9+dZGSDCA54" +
            "Lw03eHTNQghS0A==";
    public static final String RSA_CERTIFICATE =
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIBpjCCAQ+gAwIBAgIBATANBgkqhkiG9w0BAQUFADAZMRcwFQYDVQQDDA5UZXN0\n" +
            "IFByaW5jaXBhbDAeFw03MDAxMDEwODAwMDBaFw0zODEyMzEwODAwMDBaMBkxFzAV\n" +
            "BgNVBAMMDlRlc3QgUHJpbmNpcGFsMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB\n" +
            "gQC0YjCwIfYoprq/FQO6lb3asXrxLlJFuCvtinTF5p0GxvQGu5O3gYytUvtC2JlY\n" +
            "zypSRjVxwxrsuRcP3e641SdASwfrmzyvIgP08N4S0IFzEURkV1wp/IpH7kH41Etb\n" +
            "mUmrXSwfNZsnQRE5SYSOhh+LcK2wyQkdgcMv11l4KoBkcwIDAQABMA0GCSqGSIb3\n" +
            "DQEBBQUAA4GBAGZLPEuJ5SiJ2ryq+CmEGOXfvlTtEL2nuGtr9PewxkgnOjZpUy+d\n" +
            "4TvuXJbNQc8f4AMWL/tO9w0Fk80rWKp9ea8/df4qMq5qlFWlx6yOLQxumNOmECKb\n" +
            "WpkUQDIDJEoFUzKMVuJf4KO/FJ345+BNLGgbJ6WujreoM1X/gYfdnJ/J\n" +
            "-----END CERTIFICATE-----";
    public static final String RSA_SIGNATURE_METHOD = RSA_SHA1.NAME;
    public static final String RSA_SIGNATURE = "I3NpxqMJP0htSt4wEl8AlHSlOniAV97idsvGpeU2UNMi1N0e3ooTHd06g6SGwWLl/ixbscY5zx1tNiwiYmh2pZrMn0Z1nbcrOZg47kMzruKNku15Vk9BuNSnEiGHGkLuGiLoIrajowwm1c7y07J0LfXa0TN60Su+jvSVxWWOxog=";

    
    public SignatureTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SignatureTest.class);
    }

    /**
     * 测试HMAC_SHA1算法。
     */
    public void testHMACSHA1() {

        //模拟客户端HTTP请求
        MockRequest request = new MockRequest().requestMethod("GET").
                                  requestURL("http://www.cheche365.com/v1.4/orders").
                                  parameterValue("status", "finished").parameterValue("page", "0").parameterValue("size", "10")
                                    .setEntity("{\"name\":\"value\"}")
            ;

        //组织签名相关参数
        Parameters params = new Parameters().appId(APP_ID).signatureMethod(SIGNATURE_METHOD).version(VERSION);
        //签名密钥
        Secrets secrets = new Secrets().appSecret(HMAC_APP_SECRET);

        String signature = null;

        try {
            signature = APISignature.generate(request, params, secrets);  //使用工具类生成签名
        } catch (SignatureException se) {
            fail(se.getMessage());
        }
        assertEquals(signature, HMAC_SIGNATURE);

        Parameters saved = (Parameters)params.clone();

        try {
            APISignature.sign(request, params, secrets);  //使用工具类签名并将签名相关信息写到request header里
        } catch (SignatureException se) {
            fail(se.getMessage());
        }

        assertTrue(params.equals(saved));
        assertTrue(params.getSignature() == null);

        //模拟服务器收到request，验证签名
        params = new Parameters();
        params.readRequest(request);
        assertEquals(params.getAppId(), APP_ID);
        assertEquals(params.getSignatureMethod(), SIGNATURE_METHOD);
        assertEquals(params.getVersion(), VERSION);
        assertEquals(params.getSignature(), HMAC_SIGNATURE);

        try {
            assertTrue(APISignature.verify(request, params, secrets)); //验签
        } catch (SignatureException se) {
            fail(se.getMessage());
        }
    }
    

    public void testRSASHA1() {
        MockRequest request = new MockRequest().requestMethod("POST").
                              requestURL("http://localhost:7310/partner/autohome/m/index.html").parameterValue("orderNo", "I20150515000019");

        Parameters params = new Parameters().appId(APP_ID).signatureMethod(RSA_SIGNATURE_METHOD).version(VERSION);
        Secrets secrets = new Secrets().appSecret(RSA_PRIVATE_KEY);  //用私钥来签名

        String signature = null;

        try {
            signature = APISignature.generate(request, params, secrets);
        }
        catch (SignatureException se) {
            fail(se.getMessage());
        }
        assertEquals(signature, RSA_SIGNATURE);

        Parameters saved = (Parameters)params.clone();

        try {
            APISignature.sign(request, params, secrets);
        }
        catch (SignatureException se) {
            fail(se.getMessage());
        }

        assertTrue(params.equals(saved));
        assertTrue(params.getSignature() == null);

        params = new Parameters();
        params.readRequest(request);
        assertEquals(params.getAppId(), APP_ID);
        assertEquals(params.getSignatureMethod(), RSA_SIGNATURE_METHOD);
        assertEquals(params.getVersion(), VERSION);
        assertEquals(params.getSignature(), RSA_SIGNATURE);

        secrets = new Secrets().appSecret(RSA_CERTIFICATE);  //用公钥来验证签名
        try {
            assertTrue(APISignature.verify(request, params, secrets));
        }
        catch (SignatureException se) {
            fail(se.getMessage());
        }
    }


}
