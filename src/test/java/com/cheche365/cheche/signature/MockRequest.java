package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.spi.PreSignRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class MockRequest implements PreSignRequest {

    private HashMap<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();

    private HashMap<String, String> params = new HashMap<>();

    private String requestMethod;

    private String requestURL;

    private Object entity;

    public MockRequest() {
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String method) {
        requestMethod = method;
    }

    public MockRequest requestMethod(String method) {
        setRequestMethod(method);
        return this;
    }

    public URL getRequestURL() {
        try {
            return new URL(requestURL);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MockRequest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void setRequestURL(String url) {
        requestURL = url;
    }

    public MockRequest requestURL(String url) {
        setRequestURL(url);
        return this;
    }


    public List<String> getHeaderValues(String name) {
        return headers.get(name);
    }

    public void addHeaderValue(String name, String value) {
        ArrayList<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            headers.put(name, values);
        }
        values.add(value);
    }

    @Override
    public String getEntityText() {
        return entity.toString();
    }

    public MockRequest headerValue(String name, String value) {
        addHeaderValue(name, value);
        return this;
    }

    public Set<String> getParameterNames() {
        return params.keySet();
    }

    public String getParameterValue(String name) {
        return params.get(name);
    }

    @Override
    public Object getEntity() {
        return this.entity;
    }

    public MockRequest setEntity(Object entity) {
        this.entity = entity;
        return this;
    }

    public synchronized void addParameterValue(String name, String value) {
        params.put(name, value);
    }

    public MockRequest parameterValue(String name, String value) {
        addParameterValue(name, value);
        return this;
    }
}
