package com.glucocare.server.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CgmEntry(
        @JsonProperty("sgv") Integer sgv,
        @JsonProperty("date") Long date
) {
}
