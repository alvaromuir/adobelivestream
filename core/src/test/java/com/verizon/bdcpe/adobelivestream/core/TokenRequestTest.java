package com.verizon.bdcpe.adobelivestream.core;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */

class TokenRequestTest {
    private static final Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

//    Be sure to setup appropriate variables in TestConstants.java class . . .
    private Credentials credentials;

    private String clientId = TestConstants.CLIENT_ID;
    private String clientSecret = TestConstants.CLIENT_SECRET;


    @Disabled
    @DisplayName("Testing token endpoint connection and response")
    @Tag("connection-testing")
    @Test
    void newTokenTestReturnsResponseTest() throws Exception {
        credentials = new Credentials
                .Builder(clientId, clientSecret)
                .build();

        TokenRequest request = new TokenRequest(credentials);

        String tokenResponse = request.newToken();
        assertNotNull(tokenResponse);
    }

    @Disabled
    @Test
    @DisplayName("Testing proxy connection for tokens")
    @Tag("proxy-testing")
    void newTokenTestViaProxyTest() throws Exception {
        String PROXY_HOST = TestConstants.PROXY_SERVER;
        Integer PROXY_PORT = TestConstants.PROXY_PORT;
        String PROXY_USER =  TestConstants.PROXY_USERNAME;
        String PROXY_PASS =  TestConstants.PROXY_PASSWORD;
        credentials = new Credentials
                .Builder(clientId, clientSecret)
                // This adds proxy creds; be sure you have them or skip this test . . .
                .proxyHost(PROXY_HOST)
                .proxyPortNumber(PROXY_PORT)
                .proxyUserName(PROXY_USER)
                .proxyPassword(PROXY_PASS)
                .build();

        // be sure to have a working proxy server available, or this fails!
        TokenRequest request = new TokenRequest(credentials);

        String tokenResponse = request.newToken();
        assertNotNull(tokenResponse);
    }

    @Disabled
    @Test
    @DisplayName("Testing token validity")
    @Tag("validity-testing")
    void newTokenIsValidTest() throws Exception {
        credentials = new Credentials
                .Builder(clientId, clientSecret)
                .build();

        TokenRequest request = new TokenRequest(credentials);

        String accessToken = request.newToken();
        assertNotNull(accessToken);
        assertTrue(accessToken.length() > 255);
        assertTrue(accessToken.startsWith("ey"));
        log.info("response is: " + accessToken);
    }
}