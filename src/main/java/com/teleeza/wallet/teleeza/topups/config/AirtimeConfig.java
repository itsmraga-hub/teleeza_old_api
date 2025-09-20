package com.teleeza.wallet.teleeza.topups.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@lombok.Data
@Configuration
@ConfigurationProperties(prefix = "tupay.api")
public class AirtimeConfig {
    private String tokenUrl;
    private String statusUrl;
    private String balanceUrl;
    private String airtimeUrl;
    private String orderUrl;
    private String username;
    private String password;
}
