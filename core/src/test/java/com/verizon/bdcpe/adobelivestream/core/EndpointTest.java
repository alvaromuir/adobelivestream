package com.verizon.bdcpe.adobelivestream.core;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */

class EndpointTest {
    private Logger log = LoggerFactory.getLogger(EndpointTest.class);

    //    Be sure to setup appropriate variables in TestConstants.java class . . .
    private String applicationId = TestConstants.APPLICATION_ID;

    private Integer maxConnections = 1;
    private Endpoint endpoint = new Endpoint.Builder(applicationId, maxConnections).build();
    private String expected = String.format(
            "%s%s?maxConnections=%s",
            Constants.STREAM_HOST,
            applicationId,
            maxConnections.toString()
    );

    @DisplayName("Testing setting and getting the domain from Endpoint class")
    @Tag("domain-endpoint-testing")
    @Test
    void getStreamDomain() {
        String domain = endpoint.getStreamDomain();
        assertNotNull(domain);
        assertTrue(Objects.equals(domain, Constants.STREAM_HOST));

        String newDomain = "https://new.livestream.adobe.net/api/2/stream/";
        endpoint = new Endpoint.Builder(applicationId, maxConnections)
                .streamDomain(newDomain)
                .build();
        domain = endpoint.getStreamDomain();
        assertNotNull(domain);
        assertEquals(domain, newDomain);
        log.info("the stream domain is : " + domain);
    }

    @Disabled
    @DisplayName("Testing setting and getting the appID from Endpoint class")
    @Tag("applicationId-endpoint-testing")
    @Test
    void getApplicationId() {
        String appId = endpoint.getApplicationId();
        assertNotNull(appId);
        assertEquals(appId, applicationId);

        String newAppId = "some-other-appId";
        endpoint = new Endpoint.Builder(newAppId, maxConnections).build();
        appId = endpoint.getApplicationId();
        assertNotNull(appId);
        assertEquals(appId, newAppId);
        log.info("the application id is " + appId);
    }

    @DisplayName("Testing setting and getting 'maxConnections' from Endpoint class")
    @Tag("maxconnection-endpoint-testing")
    @Test
    void getMaxConnections() {
        Integer maxConns = endpoint.getMaxConnections();
        assertNotNull(maxConns);
        assertEquals(maxConns, maxConnections);

        Integer newMaxConnections = 5;
        endpoint = new Endpoint.Builder(applicationId, newMaxConnections).build();
        maxConns = endpoint.getMaxConnections();
        assertNotNull(maxConns);
        assertEquals(maxConns, newMaxConnections);
        log.info("the endpoint is " + endpoint);
    }

    @Disabled
    @DisplayName("Testing setting and getting the reset flag from Endpoint class")
    @Tag("reset-endpoint-testing")
    @Test
    void getStreamReset() {
        String reset = endpoint.getStreamReset();
        assertNull(reset);
        assertTrue(true);

        String lrgReset = "largest";
        endpoint = new Endpoint.Builder(applicationId, maxConnections).streamReset(lrgReset).build();
        reset = endpoint.getStreamReset();
        assertNotNull(reset);
        assertEquals(reset, lrgReset);

        String smlReset = "smallest";
        endpoint = new Endpoint.Builder(applicationId, maxConnections).streamReset(smlReset).build();
        reset = endpoint.getStreamReset();
        assertNotNull(reset);
        assertEquals(reset, smlReset);


        String badReset = "invalidReset";
        endpoint = new Endpoint.Builder(applicationId, maxConnections).streamReset(badReset).build();
        reset = endpoint.getStreamReset();
        assertNull(reset);
        assertTrue(true);
    }

    @DisplayName("Testing getting the final constructed url from Endpoint class")
    @Tag("url-endpoint-testing")
    @Test
    void getUrl() {
        String url = endpoint.getUrl();
        assertNotNull(url);
        assertEquals(url, expected);

        String newDomain = "https://new.livestream.adobe.net/api/2/stream/";
        String newAppId = "some-other-appId";
        Integer newMaxConnections = 5;
        String lrgReset = "largest";

        expected = String.format(
                "%s%s?maxConnections=%s&reset=%s",
                newDomain,
                newAppId,
                newMaxConnections.toString(),
                lrgReset
        );

        endpoint = new Endpoint.Builder(newAppId, newMaxConnections)
                .streamDomain(newDomain)
                .streamReset(lrgReset)
                .build();

        assertEquals(endpoint.getUrl(), expected);
    }
}