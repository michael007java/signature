

package com.cheche365.cheche.signature.spi;

import java.net.URL;
import java.util.List;
import java.util.Set;

/*
 * Created by zhengwei on 5/13/15.
 * 待签名的request信息，从HTTP Request中提取出关键信息，用做签名的消息源。
 * TODO 单独定义body
 */
public interface PreSignRequest {

    String getRequestMethod();

    URL getRequestURL();

    Set<String> getParameterNames();

    String getParameterValue(String name);

    List<String> getHeaderValues(String name); //http 允许同名的header

    Object getEntity();

    void addHeaderValue(String name, String value) throws IllegalStateException;

    String getEntityText();

}
