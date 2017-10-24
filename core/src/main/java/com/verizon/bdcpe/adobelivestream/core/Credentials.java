package com.verizon.bdcpe.adobelivestream.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */
public class Credentials {
    private static final Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

    private String clientId;
    private String clientSecret;
    private String tokenRequestUrl;
    private String proxyHost;
    private Integer proxyPortNumber;
    private String proxyUserName;
    private String proxyPassword;

    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getTokenRequestUrl() { return tokenRequestUrl; }
    public String getProxyHost() { return proxyHost; }
    public Integer getProxyPortNumber() { return proxyPortNumber; }
    public String getProxyUserName() { return proxyUserName; }
    public String getProxyPassword() { return proxyPassword; }

    Credentials(Builder builder) {
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
        String clientId;
        String clientSecret;
        String tokenRequestUrl = Constants.DEFAULT_TOKEN_REQUEST_URL;
        String proxyHost;
        Integer proxyPortNumber;
        String proxyUserName;
        String proxyPassword;


        public Builder(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;

        }

        public Builder tokenRequestUrl(String tokenRequestUrl) {
            this.tokenRequestUrl = tokenRequestUrl;
            return this;
        }

        public Builder proxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
            return this;
        }

        public Builder proxyPortNumber(Integer proxyPortNumber){
            this.proxyPortNumber = proxyPortNumber;
            return this;
        }

        public Builder proxyUserName(String proxyUserName) {
            this.proxyUserName = proxyUserName;
            return this;
        }

        public Builder proxyPassword(String proxyPassword) {
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
                        this.getProxyPassword().replaceAll("(?!^).(?!$)","*")
        );
    }
}
