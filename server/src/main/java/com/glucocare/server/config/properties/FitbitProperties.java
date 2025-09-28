package com.glucocare.server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fitbit")
public record FitbitProperties(
        String clientId,
        String clientSecret,
        String redirectUri
) {
}
