package com.verizon.bdcpe.adobelivestream.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */


// low-level class that returns both a closable client as well as a closeable response
public class Connection implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    private Credentials credentials;
    private Endpoint endpoint;
    private String accessToken;
    private CloseableHttpClient client;
    private CloseableHttpResponse response;
    private BlockingQueue<String> eventQueue;

    public void run() {
        log.info("Attempting to connect to endpoint " + endpoint.getUrl());
        HttpGet httpGet = new HttpGet(endpoint.getUrl());
        httpGet.addHeader("Authorization", "Bearer " + accessToken);

        if (credentials.getProxyHost() != null) {
            String proxyHost = credentials.getProxyHost();

            // this is cleans up any malformed proxy addresses (removes http://, https)
            if (proxyHost.startsWith("https://")) {
                proxyHost = proxyHost.replace("https://", "");
            }
            if (proxyHost.startsWith("http://")) {
                proxyHost = proxyHost.replace("http://", "");
            }

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(proxyHost, credentials.getProxyPortNumber()),
                    new UsernamePasswordCredentials(credentials.getProxyUserName(), credentials.getProxyPassword()));

            client = HttpClients.custom()
                    .setRetryHandler(retryHandler)
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();

            HttpHost proxy = new HttpHost(proxyHost, credentials.getProxyPortNumber());
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();

            httpGet.setConfig(config);
        } else {
            client = HttpClients.custom()
                    .setRetryHandler(retryHandler)
                    .build();
        }

        try {
            response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String errMsg = "A HTTP error has occurred while attempting to connect to endpoint: " + response.getStatusLine();
                throw new Exception(errMsg);
            }
            else {
                BufferedReader reader = new BufferedReader((new InputStreamReader(response.getEntity().getContent())));
                String event;
                while ((event = reader.readLine()) != null) {
                    eventQueue.put(event);
                }
            }
        } catch (Exception e) {
            try {
                response.close();
            } catch (IOException i) {
                log.error("Response close error: " + i.getMessage());
            }
        }
    }


    void stop() {
        log.info("Shutdown called.");
        try {
            response.close();
            log.info("Closed endpoint response.");
        } catch (IOException i) {
            String errMsg = "A HTTP error has occurred while attempting to close the response: " + i.getMessage();
            log.error(errMsg);
        }

        try {
            client.close();
            log.info("Closed down http socket.");
        } catch (IOException i) {
            String errMsg = "A HTTP error has occurred while attempting to close the client: " + i.getMessage();
            log.error(errMsg);
        }
    }


    private Connection(Builder builder) {
        this.credentials = builder.credentials;
        this.endpoint = builder.endpoint;
        this.accessToken = builder.accessToken;
        this.eventQueue = builder.eventQueue;
        log.info("Connection created: " +this.toString());
    }

    public static class Builder {
        private Credentials credentials;
        private Endpoint endpoint;
        private String accessToken;
        private BlockingQueue<String> eventQueue;

        Builder(Credentials credentials, Endpoint endpoint, String accessToken) {
            this.credentials = credentials;
            this.endpoint = endpoint;
            this.accessToken = accessToken;
        }

        Builder eventQueue(BlockingQueue<String> eventQueue) {
            this.eventQueue = eventQueue;
            return this;
        }

        public Connection build() {
            return new Connection(this);
        }
    }

    private HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {

        if (executionCount >= 5) {
            return false;
        } /* toDo: make retries number a parameter */
        if (exception instanceof InterruptedIOException) {
            return false;
        } // Timeout
        if (exception instanceof UnknownHostException) {
            return false;
        } // Unknown host

        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        return !(request instanceof HttpEntityEnclosingRequest);
    };

    // for debugging
    public String string() {
        return this.getClass().getSimpleName()
                + String.format(" { endpointUrl: %s, " +
                "accessToken: %s, " +
                "eventQueue: %s }", endpoint.getUrl(), null, null);
    }
}