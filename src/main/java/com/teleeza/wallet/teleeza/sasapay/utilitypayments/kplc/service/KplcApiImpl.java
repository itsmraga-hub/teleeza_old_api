package com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.requests.InternalKplcRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.requests.KplcRequests;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.responses.KplcResponse;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static com.teleeza.wallet.teleeza.utils.Constants.*;

@Service
@Slf4j
public class KplcApiImpl implements KplcApi {
    private final SasaPayConfig sasaPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final SasaPayApi sasaPayApi;

    public KplcApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper, SasaPayApi sasaPayApi) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.sasaPayApi = sasaPayApi;
    }

    @Override
    public KplcResponse buyKplcTokens(InternalKplcRequest internalKplcRequest) {
        KplcRequests kplcRequests = new KplcRequests();
        kplcRequests.setMerchantCode("669994");
        kplcRequests.setMeterNumber(internalKplcRequest.getMeterNumber());
        kplcRequests.setBeneficiaryAccountNumber(internalKplcRequest.getBeneficiaryAccountNumber());
        kplcRequests.setMobileNumber(internalKplcRequest.getMobileNumber());
        kplcRequests.setAmount(internalKplcRequest.getAmount());
        kplcRequests.setCallBackUrl(sasaPayConfig.getKplcCallBack());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(kplcRequests)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getBuyKplcTokensEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, sasaPayApi.getAccessToken().getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), KplcResponse.class);
        } catch (IOException ex) {
            log.error(String.format("Unable to buy tokens -> %s", ex.getLocalizedMessage()));
            return null;
        }
    }
}
