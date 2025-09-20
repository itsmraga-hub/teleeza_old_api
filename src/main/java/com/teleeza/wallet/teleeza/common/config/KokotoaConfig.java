package com.teleeza.wallet.teleeza.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kokotoa.callback")
public class KokotoaConfig {
    private String testBaseUrl;
    private String kokotoaApiEndpoint;
    private String prodBaseUrl;
}
