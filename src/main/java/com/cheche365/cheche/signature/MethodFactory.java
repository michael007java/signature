package com.cheche365.cheche.signature;


import com.cheche365.cheche.signature.spi.APISignatureMethod;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;


public class MethodFactory {

    private static final Set<APISignatureMethod> signatureMethods = new HashSet<>();

    static {
        ServiceLoader<APISignatureMethod> loader = ServiceLoader.load(APISignatureMethod.class);
        Iterator<APISignatureMethod> it = loader.iterator();
        if(!it.hasNext()){
            throw new NullPointerException("无可用签名方法");
        }

        while (it.hasNext()){
            signatureMethods.add(it.next());
        }
    }

    public static APISignatureMethod getInstance(String name) {
        APISignatureMethod method = null;
        for(APISignatureMethod apiSignatureMethod : signatureMethods) {
            if(apiSignatureMethod.name().equals(name)) {
                method = apiSignatureMethod;
                break;
            }
        }
        if(null == method) {
            throw new UnsupportedSignatureMethodException("不支持签名方法 "+name);
        }
        return method;
    }

}

