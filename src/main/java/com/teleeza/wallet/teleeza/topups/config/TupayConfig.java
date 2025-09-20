package com.teleeza.wallet.teleeza.topups.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@lombok.Data
@Configuration
//@ConfigurationProperties(prefix = "tupay.api")
public class TupayConfig {
    private String airtimeApiKey;
    private String mobileApiKey;
    private String baseUrl;

    public TupayConfig() {
    }

    public TupayConfig(String airtimeApiKey, String mobileApiKey, String baseUrl) {
        this.airtimeApiKey = airtimeApiKey;
        this.mobileApiKey = mobileApiKey;
        this.baseUrl = baseUrl;
    }
}
