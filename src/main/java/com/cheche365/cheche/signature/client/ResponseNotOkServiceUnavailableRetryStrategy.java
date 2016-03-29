package com.cheche365.cheche.signature.client;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * 非200状态重试策略，使用HttpClient ServiceUnavailableRetryStrategy
 * Created by Jason on 01/19/2016.
 */
public class ResponseNotOkServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {

    private long retryInterval = 3;

    public ResponseNotOkServiceUnavailableRetryStrategy() {
    }

    public ResponseNotOkServiceUnavailableRetryStrategy(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    @Override
    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
        return getRetryInterval() > executionCount && HttpStatus.SC_OK != response.getStatusLine().getStatusCode();
    }

    @Override
    public long getRetryInterval() {
        return this.retryInterval;
    }
}
