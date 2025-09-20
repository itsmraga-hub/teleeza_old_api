package com.teleeza.wallet.teleeza.topups.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "topups.api")
public class TopupsConfig {
    private String airtimeApiKey;
    private String mobileApiKey;
    private String topupBaseUrl;

    public TopupsConfig() {
    }

    public TopupsConfig(String airtimeApiKey, String mobileApiKey, String topupBaseUrl) {
        this.airtimeApiKey = airtimeApiKey;
        this.mobileApiKey = mobileApiKey;
        this.topupBaseUrl = topupBaseUrl;
    }

    public String getAirtimeApiKey() {
        return airtimeApiKey;
    }

    public void setAirtimeApiKey(String airtimeApiKey) {
        this.airtimeApiKey = airtimeApiKey;
    }

    public String getMobileApiKey() {
        return mobileApiKey;
    }

    public void setMobileApiKey(String mobileApiKey) {
        this.mobileApiKey = mobileApiKey;
    }

    public String getTopupBaseUrl() {
        return topupBaseUrl;
    }

    public void setTopupBaseUrl(String topupBaseUrl) {
        this.topupBaseUrl = topupBaseUrl;
    }
}
