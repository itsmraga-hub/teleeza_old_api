package com.teleeza.wallet.teleeza.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "meliora.app")
public class MelioraConfig {
    private String baseUrlEndpoint;
    private String productId;
}
