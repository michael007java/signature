package com.cheche365.cheche.signature.client;

import com.cheche365.cheche.signature.SignatureException;
import com.cheche365.cheche.signature.spi.PreSignRequest;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

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

    @Override
    public String getEntityText() {
        if (getEntity() != null) {
            try(ByteArrayOutputStream baos = new ByteArrayOutputStream(2048)) {
                Object entity = getEntity();
                Class bodyType = entity.getClass();
                Type genericType = bodyType;
                if (entity instanceof GenericEntity) {
                    genericType = ((GenericEntity) entity).getType();
                }
                providers.getMessageBodyWriter(
                    bodyType, genericType, new Annotation[0], APPLICATION_JSON_TYPE
                ).writeTo(entity,
                    bodyType,
                    bodyType,
                    new Annotation[0],
                    APPLICATION_JSON_TYPE,
                    clientRequest.getHeaders(),
                    baos
                );
                return new String(baos.toByteArray(), UTF_8);
            } catch (Exception e) {
                throw new SignatureException("利用Jersey机制获取实体文本失败", e);
            }
        } else {
            return "";
        }
    }

}

