package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.client.ClientSignatureFilter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

class ClientApp {

    public static void execute(int port) {

        String base = "http://localhost:" + port;

        HttpClient apacheClient = HttpClientBuilder.create().build();

        Client client = new Client(new ApacheHttpClient4Handler(apacheClient, new BasicCookieStore(), true));
        client.addFilter(new LoggingFilter());
        Providers providers = client.getProviders();

        Parameters params = new Parameters().appId(SignatureTest.APP_ID).version(SignatureTest.VERSION).signatureMethod(SignatureTest.SIGNATURE_METHOD);
        Secrets secrets = new Secrets().appSecret(SignatureTest.HMAC_APP_SECRET);

        ClientSignatureFilter filter = new ClientSignatureFilter(providers, params, secrets);
        MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
        queryParams.add("orderNo", "I20150515000019");

        WebResource resource = client.resource(base + "/orders");
        resource.addFilter(filter);
        resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE).post(String.class, "{\"name1\":\"value1\",\"name2\":\"value2\"}");
    }
}

