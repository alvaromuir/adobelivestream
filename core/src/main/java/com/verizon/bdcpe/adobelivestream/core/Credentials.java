package com.verizon.bdcpe.adobelivestream.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */

public class Credentials {
    private static final Logger log = LoggerFactory.getLogger(Credentials.class);

    private String clientId;
    private String clientSecret;
    private String tokenRequestUrl;
    private String proxyHost;
    private Integer proxyPortNumber;
    private String proxyUserName;
    private String proxyPassword;

    String getClientId() { return clientId; }
    String getClientSecret() { return clientSecret; }
    String getTokenRequestUrl() { return tokenRequestUrl; }
    String getProxyHost() { return proxyHost; }
    Integer getProxyPortNumber() { return proxyPortNumber; }
    String getProxyUserName() { return proxyUserName; }
    String getProxyPassword() { return proxyPassword; }

    private Credentials(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.tokenRequestUrl = builder.tokenRequestUrl;
        this.proxyHost = builder.proxyHost;
        this.proxyPortNumber = builder.proxyPortNumber;
        this.proxyUserName = builder.proxyUserName;
        this.proxyPassword = builder.proxyPassword;
        log.info("Credentials created: " + this.string());
    }

    public static class Builder {
        private String clientId;
        private String clientSecret;
        private String tokenRequestUrl = Constants.DEFAULT_TOKEN_REQUEST_URL;
        private String proxyHost;
        private Integer proxyPortNumber;
        private String proxyUserName;
        private String proxyPassword;


        Builder(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;

        }

        Builder tokenRequestUrl(String tokenRequestUrl) {
            this.tokenRequestUrl = tokenRequestUrl;
            return this;
        }

        Builder proxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
            return this;
        }

        Builder proxyPortNumber(Integer proxyPortNumber){
            this.proxyPortNumber = proxyPortNumber;
            return this;
        }

        Builder proxyUserName(String proxyUserName) {
            this.proxyUserName = proxyUserName;
            return this;
        }

        Builder proxyPassword(String proxyPassword) {
            this.proxyPassword = proxyPassword;
            return this;
        }

        public Credentials build() {
            return new Credentials(this);
        }

    }

    // for debugging
    public String string() {
        return this.getClass().getSimpleName()
                + String.format(" { clientId: %s, " +
                        "clientSecret: %s, " +
                        "tokenRequestUrl: %s " +
                        "proxyHost: %s " +
                        "proxyPortNumber: %s " +
                        "proxyUserName: %s " +
                        "proxyPassword: %s }",
                        this.getClientId(),
                        this.getClientSecret(),
                        this.getTokenRequestUrl(),
                        this.getProxyHost(),
                        this.getProxyPortNumber(),
                        this.getProxyUserName(),
                        this.getProxyPassword()
        );
    }
}
