package com.verizon.bdcpe.adobelivestream.core;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */

class ConnectionTest {
    private static final Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

    //    Be sure to setup appropriate variables in TestConstants.java class . . .
    private String clientId = TestConstants.CLIENT_ID;
    private String clientSecret = TestConstants.CLIENT_SECRET;
    private String applicationId = TestConstants.APPLICATION_ID;

    private Integer maxConnections = 1;
    private Credentials credentials = new Credentials.Builder(clientId, clientSecret).build();
    private Endpoint endpoint = new Endpoint.Builder(applicationId, maxConnections).build();
    private String accessToken;


    @BeforeEach
    void setUp() {
        try {
            accessToken = new TokenRequest(credentials).newToken();
        } catch (Exception e) {
            String errMsg = String.format("An error has occurred while trying to obtain a new token: %s", e.getMessage());
            log.error(errMsg);
        }
    }

    @Disabled
    @DisplayName("Testing creating a new instance of a collection")
    @Tag("new-collection-testing")
    @Test
    void newConnectionTest() {
        Connection connection = new Connection.Builder(credentials, endpoint, accessToken ).build();
        assertNotNull(connection);
    }

    @Disabled
    @DisplayName("Testing starting a collection and reading it's queue")
    @Tag("connection-queue-testing")
    @Test
    void start() throws Exception {
        BlockingQueue<String> events = new LinkedBlockingQueue<>();
        Connection connection = new Connection.Builder(credentials, endpoint, accessToken )
                .eventQueue(events)
                .build();
        Thread producer = new Thread(connection);
        producer.start();
        int limit = 1;
        String hit = "";
        while(limit > 0) {
            try { hit = events.take(); } catch (Exception e) { log.error(e.getMessage());}
            limit -= 1;
        }
        assertNotNull(hit);
        assertTrue(hit.startsWith("{\"reportSuite\":\"verizontelecomres\",\"timeGMT\":"));
    }

    @Disabled
    @DisplayName("Testing stopping a collection")
    @Tag("connection-stop-testing")
    @Test
    void stop() throws InterruptedException {
        BlockingQueue<String> events = new LinkedBlockingQueue<>();
        Connection connection = new Connection.Builder(credentials, endpoint, accessToken )
                .eventQueue(events)
                .build();
        Thread producer = new Thread(connection);
        producer.start();

        // sleeping here because connection must be running first . . .
        Thread.sleep(3000);
        connection.stop();
    }

}