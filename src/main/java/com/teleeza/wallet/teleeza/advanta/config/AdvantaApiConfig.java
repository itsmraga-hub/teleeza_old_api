package com.teleeza.wallet.teleeza.advanta.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "advanta.sms")
public class AdvantaApiConfig {
    private String apiKey;
    private String partnerID;
    private String shortCode;
    private String baseUrl;
}
