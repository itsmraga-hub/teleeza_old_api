package com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.requests.AirtimeRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.requests.InternalAirtimeRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.responses.AirtimeResponse;
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
public class AirtimeApiImpl implements AirtimeApi {
    private final SasaPayConfig sasaPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final SasaPayApi sasaPayApi;

    public AirtimeApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper, SasaPayApi sasaPayApi) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.sasaPayApi = sasaPayApi;
    }

    @Override
    public AirtimeResponse buyAirtime(InternalAirtimeRequest internalAirtimeRequest) {

        AirtimeRequest airtimeRequest = new AirtimeRequest();
        airtimeRequest.setMerchantCode("669994");
        airtimeRequest.setNetworkCode(internalAirtimeRequest.getNetworkCode());
        airtimeRequest.setBeneficiaryAccountNumber(internalAirtimeRequest.getBeneficiaryAccountNumber());
        airtimeRequest.setMobileNumber(internalAirtimeRequest.getMobileNumber());
        airtimeRequest.setAmount(internalAirtimeRequest.getAmount());
        airtimeRequest.setCallBackUrl(sasaPayConfig.getAirtimeCallBack());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(airtimeRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getBuyAirtimeEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, sasaPayApi.getAccessToken().getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), AirtimeResponse.class);
        } catch (IOException ex) {
            log.error(String.format("Unable to buy airtime -> %s", ex.getLocalizedMessage()));
            return null;
        }
    }
}
