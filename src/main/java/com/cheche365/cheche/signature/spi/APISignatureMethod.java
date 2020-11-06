
package com.cheche365.cheche.signature.spi;


import com.cheche365.cheche.signature.Secrets;

/**
 * Created by zhengwei on 5/13/15.
 */
public interface APISignatureMethod {

    String name();

    String sign(String elements, Secrets secrets) ;

    boolean verify(String elements, Secrets secrets, String signature) throws Exception ;
}
