package com.cheche365.cheche.signature.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * 检查response状态不是是200重试请求，默认3次
 * Created by zhaozhong on 2016/1/15.
 */
public class ErrorRepeatFilter extends ClientFilter {

    private int times = 3;

    public ErrorRepeatFilter() {
    }

    public ErrorRepeatFilter(int times) {
        this.times = times;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        ClientResponse response = getNext().handle(cr);
        if (200 != response.getStatus()) {
            response = repeatRequest(cr);
        }
        return response;
    }

    Client createJerseyClient() {
        HttpClient apacheClient = HttpClientBuilder.create().build();
        Client client = new Client(new ApacheHttpClient4Handler(apacheClient, new BasicCookieStore(), true));
        return client;
    }

    ClientResponse repeatRequest(ClientRequest request) {
        ClientResponse response = null;
        for (int i = 0; i < times; i++) {
            response = createJerseyClient().handle(request.clone());
            if (200 == response.getStatus()) {
                break;
            }
        }
        return response;
    }
}
