package com.glucocare.server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cgm")
public record CgmProperties(
        String apiSecret
) {
}
