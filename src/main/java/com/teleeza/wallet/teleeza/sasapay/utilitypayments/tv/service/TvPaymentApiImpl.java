package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.requests.InternalTvPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.requests.TvPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.responses.TvPaymentResponse;
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
public class TvPaymentApiImpl implements TvPaymentApi {
    private final SasaPayConfig sasaPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final SasaPayApi sasaPayApi;

    public TvPaymentApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper, SasaPayApi sasaPayApi) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.sasaPayApi = sasaPayApi;
    }

    @Override
    public TvPaymentResponse payForTv(InternalTvPaymentRequest internalTvPaymentRequest) {

        log.info("access token response {}", sasaPayApi.getAccessToken().getAccessToken());
        TvPaymentRequest tvPaymentRequest = new TvPaymentRequest();
        tvPaymentRequest.setMerchantCode("669994");
        tvPaymentRequest.setServiceCode(internalTvPaymentRequest.getServiceCode());
        tvPaymentRequest.setAccountNumber(internalTvPaymentRequest.getAccountNumber());
        tvPaymentRequest.setBeneficiaryAccountNumber(internalTvPaymentRequest.getBeneficiaryAccountNumber());
        tvPaymentRequest.setMobileNumber(internalTvPaymentRequest.getMobileNumber());
        tvPaymentRequest.setAmount(internalTvPaymentRequest.getAmount());
        tvPaymentRequest.setCallBackUrl(sasaPayConfig.getTvPaymentCallBack());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(tvPaymentRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getLipaTVEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, sasaPayApi.getAccessToken().getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), TvPaymentResponse.class);
        } catch (IOException ex) {
            log.error(String.format("Unable to pay for tv -> %s", ex.getLocalizedMessage()));
            return null;
        }
    }
}
