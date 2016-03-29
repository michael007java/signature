/**
 * Created by zhengwei on 12/18/15.
 *
 *  车车API签名验签相关服务。
 *  适用业务场景如下：
 *  第三方应用(TP)需要调用车车(CC)的服务，车车需要安全性验证，验证内容包括：
 *  1. TP身份认证。必须是CC认证过的TP，不同TP由app_id唯一标识
 *  2. 数据完整性。通过签名验证，保证传输过程中数据没有被篡改
 *
 *  <p>目前实现了两种签名算法：
 *   {@link com.cheche365.cheche.signature.api.HMAC_SHA1} : 签名时先做SHA1消息摘要，然后用密钥签名。验签顺序一致。对称加密算法，签名／验签密钥一致。 相对于RSA_SHA1，速度更快，对于没有特别安全需求的TP，建议用这个。
 *   {@link com.cheche365.cheche.signature.api.RSA_SHA1} : 签名时先做SHA1消息摘要，然后用私钥签名。验签时用公钥验证。非对称加密算法，签名／验签密钥不一致。
 *
 *  <p>签名统一放到HTTP Header里，如：
 *  Authorization : OAuth app_id=dpf43f3p2l4k3l03, signature_method=HMAC-SHA1, version=1.0, signature=QtTEQJNlixi9vdvNsBr6C7cyNRw=
 *
 *  Authorization， header name。TP签名后生成这个header，CC收到请求后根据这个header验证签名。
 *  OAuth，header value前缀，防止和其他同名header混淆。
 *  app_id， TP唯一标识，由车车生成。生成app_id同时生成app_secret。app_id不保密，app_secret保密。最终签名key就是app_secret。
 *  signature_method，签名算法，目前支持HMAC－SHA1和RSA－SHA1（注意：和类名不一样）。TP和CC协商好使用哪种算法，建议同一个TP只使用一种算法。
 *  version，签名版本，为防止升级后不兼容。
 *  signature，签名。
 *
 *  <p>签名生成算法主要有以下几步完成：
 *  1. 准备签名相关参数(app_id,签名算法等){@link com.cheche365.cheche.signature.Parameters}和密钥{@link com.cheche365.cheche.signature.Secrets}
 *  2. 将HTTP Request转成一个{@link com.cheche365.cheche.signature.spi.PreSignRequest}实例
 *  3. {@link com.cheche365.cheche.signature.APISignature#sign()}方法把#1和＃2信息签名，并写入request header中
 *
 *  <p>验证签名步骤：
 *  1. 通过{@link com.cheche365.cheche.signature.Parameters#readRequest()}方法从request中取出签名header
 *  2. 根据request和＃1取出的参数生成签名
 *  3. 比较＃2生成的签名和＃1取出的签名是否一致。
 *
 *  签名核心步骤请参考{@link com.cheche365.cheche.signature.APISignature#elements()}
 *
 */
package com.cheche365.cheche.signature;
