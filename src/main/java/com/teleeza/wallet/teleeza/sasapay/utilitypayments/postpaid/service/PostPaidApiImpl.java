package com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.requests.InternalPostPaidBillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.requests.PostPaidBillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.responses.PostPaidBillPaymentResponse;
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

@Slf4j
@Service
public class PostPaidApiImpl implements PostPaidApi {
    private final SasaPayConfig sasaPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final SasaPayApi sasaPayApi;
    private final TransactionsRepository transactionsRepository;

    public PostPaidApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper, SasaPayApi sasaPayApi, TransactionsRepository transactionsRepository) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.sasaPayApi = sasaPayApi;
        this.transactionsRepository = transactionsRepository;
    }

    @Override
    public PostPaidBillPaymentResponse lipaPostPaidBills(InternalPostPaidBillPaymentRequest internalPostPaidBillPaymentRequest) {
        log.info("access token response {}",sasaPayApi.getAccessToken().getAccessToken());
        PostPaidBillPaymentRequest postPaidBillPaymentRequest = new PostPaidBillPaymentRequest();
        postPaidBillPaymentRequest.setMerchantCode("669994");
        postPaidBillPaymentRequest.setServiceCode(internalPostPaidBillPaymentRequest.getServiceCode());
        postPaidBillPaymentRequest.setBeneficiaryAccountNumber(internalPostPaidBillPaymentRequest.getBeneficiaryAccountNumber());
        postPaidBillPaymentRequest.setAccountNumber(internalPostPaidBillPaymentRequest.getAccountNumber());
        postPaidBillPaymentRequest.setMobileNumber(internalPostPaidBillPaymentRequest.getMobileNumber());
        postPaidBillPaymentRequest.setCurrency("KES");
        postPaidBillPaymentRequest.setAmount(internalPostPaidBillPaymentRequest.getAmount());
        postPaidBillPaymentRequest.setCallBackUrl(sasaPayConfig.getPostPaidValidation());
//        postPaidBillPaymentRequest.setCallBackUrl("https://teleeza.com/callback/waas/v1/v1-request/utility-bills/");

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(postPaidBillPaymentRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getPostPaidEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, sasaPayApi.getAccessToken().getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), PostPaidBillPaymentResponse.class);
        } catch (IOException ex) {
            log.error(String.format("Unable to mal post paid payment -> %s", ex.getLocalizedMessage()));
            return null;
        }
    }

//
}
