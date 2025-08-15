package com.glucocare.server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fcm")
public record FCMProperties(
        String serverEndpoint
) {
}
