package com.verizon.bdcpe.adobelivestream.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */

class TokenRequest {
    private final Logger log = LoggerFactory.getLogger(TokenRequest.class);

    private String clientId;
    private String clientSecret;
    private String tokenRequestUrl;
    private String proxyHost;
    private Integer proxyPortNumber;
    private String proxyUserName;
    private String proxyPassword;
    private CloseableHttpResponse response;

    TokenRequest(com.verizon.bdcpe.adobelivestream.core.Credentials credentials)
    {
        this.clientId = credentials.getClientId();
        this.clientSecret = credentials.getClientSecret();
        this.tokenRequestUrl = credentials.getTokenRequestUrl();
        this.proxyHost = credentials.getProxyHost();
        this.proxyPortNumber = credentials.getProxyPortNumber();
        this.proxyUserName = credentials.getProxyUserName();
        this.proxyPassword = credentials.getProxyPassword();
    }

    /** callEndpoint
     *
     * @return OAuth 2.0 token.
     */
    String newToken() throws IOException, AuthenticationException, MalformedChallengeException {
        String tokenResponse;
        HttpPost httpPost = new HttpPost(tokenRequestUrl);
        String errMsg;

        CloseableHttpClient client;
        if (proxyHost != null) {

            // this is cleans up any malformed proxy addresses (removes http://, https)
            if(proxyHost.startsWith("https://")) proxyHost =  proxyHost.replace("https://", "");
            if(proxyHost.startsWith("http://")) proxyHost = proxyHost.replace("http://", "");
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(proxyHost, proxyPortNumber),
                    new UsernamePasswordCredentials(proxyUserName, proxyPassword));

            client = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider).build();

            HttpHost proxy = new HttpHost(proxyHost, proxyPortNumber);
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();

            httpPost.setConfig(config);
        } else {
            client = HttpClients.createDefault();
        }

        try {
            List<NameValuePair> nameValuePairArrayList = new ArrayList<>();
            nameValuePairArrayList.add(new BasicNameValuePair("grant_type", "client_credentials"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairArrayList, Consts.UTF_8);
            httpPost.setEntity(entity);

            UsernamePasswordCredentials creds
                    = new UsernamePasswordCredentials(clientId, clientSecret);
            httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

            try {
                response = client.execute(httpPost);
            } catch (HttpHostConnectException e) {
                errMsg = String.format("An error has occurred while trying to connect to the proxy: %s", e.getMessage());
                log.error(errMsg);
            }

            try {
                ObjectMapper mapper = new ObjectMapper();
                Map jsonModel = mapper.readValue(EntityUtils.toString(response.getEntity()), Map.class);
                try {
                    tokenResponse = (String)jsonModel.get("access_token");
                } catch (Exception e) {
                    errMsg = "An error has occurred while attempting to read the token: " + e.getMessage();
                    log.error(errMsg);
                    throw new IOException("attribute 'access_token' not found, check response");
                }
            }
            finally {
                response.close();
            }
        } finally {
            client.close();
        }
        return tokenResponse;
    }
}