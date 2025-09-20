package com.teleeza.wallet.teleeza.bima.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bima.api")
public class BimaConfig {

    private String token;
    private Integer productPackId;

    private String bimaEndpoint;
    private String externalRef;
    private String bimaGetPolicyEndpoint;
    private Integer productId;

    public BimaConfig() {

    }

    public BimaConfig(String token, Integer productPackId, String bimaEndpoint, String externalRef,
                      String bimaGetPolicyEndpoint,
                      Integer productId
    ) {
        this.token = token;
        this.productPackId = productPackId;
        this.bimaEndpoint = bimaEndpoint;
        this.externalRef = externalRef;
        this.bimaGetPolicyEndpoint = bimaGetPolicyEndpoint;
        this.productId =  productId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getProductPackId() {
        return productPackId;
    }

    public void setProductPackId(Integer productPackId) {
        this.productPackId = productPackId;
    }

    public String getBimaEndpoint() {
        return bimaEndpoint;
    }

    public void setBimaEndpoint(String bimaEndpoint) {
        this.bimaEndpoint = bimaEndpoint;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public String getBimaGetPolicyEndpoint() {
        return bimaGetPolicyEndpoint;
    }

    public void setBimaGetPolicyEndpoint(String bimaGetPolicyEndpoint) {
        this.bimaGetPolicyEndpoint = bimaGetPolicyEndpoint;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
