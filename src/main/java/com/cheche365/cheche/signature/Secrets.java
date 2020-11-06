package com.cheche365.cheche.signature;

/**
 * Created by zhengwei on 5/13/15.
 * 签名密钥，根据不同实现算法，有不类型的key。若后续实现OAuth可以把access token加入到密钥生成规则里。
 */

public class Secrets {

    private String appSecret;

    public String getAppSecret() {
    	return appSecret;
    }

    public void setAppSecret(String appSecret) {
    	this.appSecret = appSecret;
    }

    public Secrets appSecret(String consumerSecret) {
    	setAppSecret(consumerSecret);
    	return this;
    }

    @Override
    public String toString() {
        return appSecret;
    }

}

