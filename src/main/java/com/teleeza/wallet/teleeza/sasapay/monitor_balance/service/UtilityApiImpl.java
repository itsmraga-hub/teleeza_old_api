package com.teleeza.wallet.teleeza.sasapay.monitor_balance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses.BalanceResponse;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.teleeza.wallet.teleeza.utils.Constants.*;

@Service
@Slf4j
@Component
public class UtilityApiImpl implements UtilityApi {
    private final SasaPayConfig sasaPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public UtilityApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AccessTokenResponse getAccessToken() {
        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s",
                sasaPayConfig.getMerchantToBeneficiarryCliendID(), sasaPayConfig.getMerchantToBeneficiarrySecret()));

        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", sasaPayConfig.getOauthEndPoint(), sasaPayConfig.getGrantType()))
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            // Deserialize response body to Java object
            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public BalanceResponse getBalances(String MerchantCode) {
        Request request = new Request.Builder()
                .url(String.format("%s?MerchantCode=%s", "https://api.sasapay.app/api/v1/payments/check-balance", "122122"))
                .get()
                .addHeader("Authorization", String.format("Bearer %s", getAccessToken().getAccessToken()))
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            // Deserialize response body to Java object
            return objectMapper.readValue(response.body().string(), BalanceResponse.class);

        } catch (IOException e) {
            return null;
        }
    }
}
