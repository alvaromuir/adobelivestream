package com.verizon.bdcpe.adobelivestream.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */

class CredentialsTest {
    //    Be sure to setup appropriate variables in TestConstants.java class . . .
    private String clientId = TestConstants.CLIENT_ID;
    private String clientSecret = TestConstants.CLIENT_SECRET;

    private Credentials credentials = new Credentials.Builder(clientId, clientSecret).build();

    @Disabled
    @DisplayName("Testing setting and getting an appID/clientID from Endpoint class")
    @Tag("appId-endpoint-testing")
    @Test
    void getClientId() {
        String id = credentials.getClientId();
        assertNotNull(id);
        assertEquals(id, clientId);

        String newClientId = "some-bad-clientId";
        credentials = new Credentials.Builder(newClientId, clientSecret).build();
        id = credentials.getClientId();
        assertNotNull(id);
        assertEquals(id, newClientId);
    }

    @Disabled
    @DisplayName("Testing setting and getting an appSecret/clientSecret from Endpoint class")
    @Tag("secret-endpoint-testing")
    @Test
    void getClientSecret() {
        String secret = credentials.getClientSecret();
        assertNotNull(secret);
        assertEquals(secret, clientSecret);

        String newClientSecret = "incorrect-password";
        credentials = new Credentials.Builder(clientId, newClientSecret).build();
        secret = credentials.getClientSecret();
        assertNotNull(secret);
        assertEquals(secret, newClientSecret);
    }

    @Disabled
    @DisplayName("Testing setting and getting an token url from Endpoint class")
    @Tag("tokenurl-endpoint-testing")
    @Test
    void getTokenRequestUrl() {
        String tokenUrl = credentials.getTokenRequestUrl();
        assertNotNull(tokenUrl);
        String tokenRequestUrl = TestConstants.DEFAULT_TOKEN_REQUEST_URL;
        assertEquals(tokenUrl, tokenRequestUrl);

        String newTokenUrl = "https://api2.sometokenendpoint.com/";
        credentials = new Credentials.Builder(clientId, clientSecret)
                .tokenRequestUrl(newTokenUrl)
                .build();

        tokenUrl = credentials.getTokenRequestUrl();
        assertNotNull(tokenUrl);
        assertEquals(tokenUrl, newTokenUrl);
    }

    @Disabled
    @DisplayName("Testing setting and getting a proxy host from Endpoint class")
    @Tag("proxyhost-endpoint-testing")
    @Test
    void getProxyHost() {
        String host = "http://localhost";
        credentials = new Credentials.Builder(clientId, clientSecret)
                .proxyHost(host)
                .build();
        String proxyHost = credentials.getProxyHost();
        assertNotNull(proxyHost);
    }

    @Disabled
    @DisplayName("Testing setting and getting a proxy port from Endpoint class")
    @Tag("proxyport-endpoint-testing")
    @Test
    void getProxyPortNumber() {
        Integer port = TestConstants.PROXY_PORT;
        credentials = new Credentials.Builder(clientId, clientSecret)
                .proxyPortNumber(port)
                .build();
        Integer proxyPortNumber = credentials.getProxyPortNumber();
        assertNotNull(proxyPortNumber);
    }

    @Disabled
    @DisplayName("Testing setting and getting a proxy username from Endpoint class")
    @Tag("proxyuser-endpoint-testing")
    @Test
    void getProxyUserName() {
        String username = "username";
        credentials = new Credentials.Builder(clientId, clientSecret)
                .proxyUserName(username)
                .build();
        String proxyUserName = credentials.getProxyUserName();
        assertNotNull(proxyUserName);
    }

    @Disabled
    @DisplayName("Testing setting and getting a proxy password from Endpoint class")
    @Tag("proxypassword-endpoint-testing")
    @Test
    void getProxyPassword() {
        String password = "password";
        credentials = new Credentials.Builder(clientId, clientSecret)
                .proxyPassword(password)
                .build();
        String proxyPassword = credentials.getProxyPassword();
        assertNotNull(proxyPassword);
    }

}