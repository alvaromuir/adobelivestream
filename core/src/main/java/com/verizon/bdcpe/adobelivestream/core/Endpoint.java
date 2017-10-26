package com.verizon.bdcpe.adobelivestream.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by alvaro on 7/3/17.
 */
public class Endpoint {
    private static final Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

    private String streamDomain;
    private String applicationId;
    private Integer maxConnections;
    private String streamReset;

    public String getStreamDomain() { return streamDomain; }
    public String getApplicationId() { return applicationId; }
    public Integer getMaxConnections() { return maxConnections; }
    public String getStreamReset(){ return streamReset; }


    private Endpoint(Builder builder) {
        this.streamDomain = builder.streamDomain;
        this.applicationId = builder.applicationId;
        this.maxConnections = builder.maxConnections;
        this.streamReset = builder.streamReset;
        log.info("Endpoint created: " + this.string());
    }

    public static class Builder {
        String streamDomain = Constants.STREAM_HOST;
        String applicationId;
        Integer maxConnections;
        String streamReset;

        public Builder(String applicationId, Integer maxConnections) {
            this.applicationId = applicationId;
            this.maxConnections = maxConnections;
        }

        Builder streamDomain(String streamDomain) {
            this.streamDomain = streamDomain;
            return this;
        }

        Builder streamReset(String streamReset) {
            String reset = streamReset.toLowerCase();
            if (Objects.equals(reset, "largest") || Objects.equals(reset, "smallest")){
                this.streamReset = streamReset;
            }
            return this;
        }

        public Endpoint build() { return new Endpoint(this); }
    }

    String getUrl() {
        String url = String.format("%s%s?maxConnections=%s", streamDomain, applicationId, maxConnections.toString());
        if(streamReset != null) { url += String.format("&reset=%s", streamReset); }
        return url;
    }

    // for debugging
    public String string() {
        return this.getClass().getSimpleName()
                + String.format(" { domain: %s, " +
                        "applicationId: %s, " +
                        "maxConnections: %s, " +
                        "reset: %s, " +
                        "constructedUrl: %s }",
                this.getStreamDomain(),
                this.getApplicationId(),
                this.getMaxConnections().toString(),
                this.getStreamReset(),
                this.getUrl());
    }
}