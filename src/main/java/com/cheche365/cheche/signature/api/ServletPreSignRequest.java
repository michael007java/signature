package com.cheche365.cheche.signature.api;

import com.cheche365.cheche.signature.spi.PreSignRequest;
import com.sun.jersey.api.uri.UriComponent;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhengwei on 12/20/15.
 */
public class ServletPreSignRequest implements PreSignRequest {

    private HttpServletRequest request;

    public ServletPreSignRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getRequestMethod() {

        return request.getMethod();
    }

    @Override
    public URL getRequestURL() {
        try {
            return new URL(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL 格式错误，"+request.getRequestURL().toString(), e);
        }
    }

    @Override
    public Set<String> getParameterNames() {
        Set<String> set = new HashSet<>();
        String queryString = request.getQueryString();
        if(null != queryString && !"".equals(queryString)) {
            String[] queryValues = queryString.split("&");
            for(String q : queryValues) {
                set.add(q.substring(0, q.indexOf("=")));
            }
        }
        return set;
    }

    @Override
    public String getParameterValue(String name) {
        return request.getParameter(name);
    }

    @Override
    public List<String> getHeaderValues(String name) {
        return null==request.getHeader(name) ? null : Arrays.asList(new String[]{request.getHeader(name)});
    }

    @Override
    public Object getEntity() {
        Object obj = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            InputStream is = request.getInputStream();
            byte[] buf = new byte[512];
            int len ;
            while((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
                baos.flush();
            }
            obj = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }catch(IOException ex) {
            ex.printStackTrace();
        }finally {
            if(baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }


    //sever端在验证签名的时候不需要修改header，只需要读取并校验。
    @Override
    public void addHeaderValue(String name, String value) throws IllegalStateException {
        throw new UnsupportedOperationException("Server端不支持验证签名时修改request header");

    }
}
