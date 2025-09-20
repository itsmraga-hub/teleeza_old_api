package com.teleeza.wallet.teleeza.daraja.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mpesa.daraja")
public class MpesaConfiguration {
    private String consumerKey;
    private String consumerSecret;
    private String grantType;
    private String oauthEndpoint;
    private String registerUlrEndPoint;
    private String simulateC2BTransactionEndpoint;
    private String shortCode;
    private String confirmationURL;
    private String validationURL;
    private String responseType;
    private String initiatorPassword;
    private String initiatorName;
    private String b2cTransactionEndpoint;
    private String b2cResultUrl;
    private String b2cQueueTimeoutUrl;
    private String b2cConsumerKey;
    private String b2cConsumerSecret;
    private String b2cShortCode;
    private String commandId;
    private String stkPushShortCode;
    private String stkPassKey;
    private String jiinueCallBack;
    private String stkPushRequestUrl;
    private String checkAccountBalanceUrl;
    private String rewardedAdsResultUrl;
    private String referralUrl;

    @Override
    public String toString() {
        return String.format("{consumerKey='%s', consumerSecret='%s', grantType='%s', oauthEndpoint='%s'}",
                consumerKey, consumerSecret, grantType, oauthEndpoint);
    }
}
