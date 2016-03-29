package com.cheche365.cheche.signature.client;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;

class RequestUtil {

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    public static MediaType getMediaType(ClientRequest request) {
    
        final Object header = request.getMetadata().getFirst("Content-Type");

        if (header == null) {
            return null;
        }

        if (header instanceof MediaType) {
            return (MediaType)header;
        }

        return MediaType.valueOf(header.toString());
    }

    public static MultivaluedMap<String, String> getQueryParameters(ClientRequest request) {
    
        URI uri = request.getURI();

        if (uri == null) {
            return null;
        }

        return UriComponent.decodeQuery(uri, true);
    }

    public static MultivaluedMap<String, String>
    getEntityParameters(ClientRequest request, Providers providers) {

        Object entity = request.getEntity();
        String method = request.getMethod();
        MediaType mediaType = getMediaType(request);

        // no entity, not a post or not x-www-form-urlencoded: return empty map
        if (entity == null || method == null || !method.equalsIgnoreCase("POST") ||
        mediaType == null || !mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
            return new MultivaluedMapImpl();
        }

        // it's ready to go if already expressed as a multi-valued map
        if (entity instanceof MultivaluedMap) {
            return (MultivaluedMap)entity;
        }

        Type entityType = entity.getClass();

        // if the entity is generic, get specific type and class
        if (entity instanceof GenericEntity) {
            final GenericEntity generic = (GenericEntity)entity;
            entityType = generic.getType(); // overwrite
            entity = generic.getEntity();
        }

        final Class entityClass = entity.getClass();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        MessageBodyWriter writer = providers.getMessageBodyWriter(entityClass,
         entityType, EMPTY_ANNOTATIONS, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        try {
            writer.writeTo(entity, entityClass, entityType,
             EMPTY_ANNOTATIONS, MediaType.APPLICATION_FORM_URLENCODED_TYPE, null, out);
        }
        catch (WebApplicationException wae) {
            throw new IllegalStateException(wae);
        }
        catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        MessageBodyReader reader = providers.getMessageBodyReader(MultivaluedMap.class,
         MultivaluedMap.class, EMPTY_ANNOTATIONS, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        try {
            return (MultivaluedMap<String, String>)reader.readFrom(MultivaluedMap.class,
             MultivaluedMap.class, EMPTY_ANNOTATIONS, MediaType.APPLICATION_FORM_URLENCODED_TYPE, null, in);
        }
        catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
}

