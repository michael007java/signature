package com.cheche365.cheche.signature.client;

import com.cheche365.cheche.signature.spi.PreSignRequest;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Providers;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestWrapper implements PreSignRequest {

    private final ClientRequest clientRequest;

    private final Providers providers;

    private MultivaluedMap<String, String> parameters = null;

    private void setParameters() {
        parameters = new MultivaluedMapImpl();
        parameters.putAll(RequestUtil.getQueryParameters(clientRequest));
    }

    public RequestWrapper(final ClientRequest clientRequest, final Providers providers) {
        this.clientRequest = clientRequest;
        this.providers = providers;
        setParameters();
    }

    @Override
    public String getRequestMethod() {
        return clientRequest.getMethod();
    }

    @Override
    public URL getRequestURL() {
        try {
            final URI uri = clientRequest.getURI();
            return uri.toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(RequestWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<String> getParameterNames() {
        return parameters.keySet();
    }

    @Override
    public String getParameterValue(final String name) {
        return parameters.get(name)==null ? null : parameters.get(name).get(0);
    }

    @Override
    public List<String> getHeaderValues(final String name) {

        ArrayList<String> list = new ArrayList();

        for (Object header : clientRequest.getHeaders().get(name)) {
            list.add(ClientRequest.getHeaderValue(header));
        }

        return list;
    }

    @Override
    public Object getEntity() {
        return clientRequest.getEntity();
    }

    @Override
    public void addHeaderValue(final String name, final String value) {
        clientRequest.getHeaders().add(name, value);
    }
}

