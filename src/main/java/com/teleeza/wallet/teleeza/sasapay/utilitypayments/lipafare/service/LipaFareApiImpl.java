package com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.requests.InternalLipaFareRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.requests.LipaFareRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.responses.LipaFareResponse;
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
public class LipaFareApiImpl implements LipaFareApi {
    private final SasaPayConfig sasaPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final SasaPayApi sasaPayApi;

    public LipaFareApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper, SasaPayApi sasaPayApi) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.sasaPayApi = sasaPayApi;
    }

    @Override
    public LipaFareResponse lipaFare(InternalLipaFareRequest internalLipaFareRequest) {

        LipaFareRequest lipaFareRequest = new LipaFareRequest();
        lipaFareRequest.setTransactionReference(HelperUtility.getTransactionUniqueNumber());
        lipaFareRequest.setBillRefNumber(HelperUtility.getBillRefNumber());
        lipaFareRequest.setBeneficiaryAccountNumber(internalLipaFareRequest.getBeneficiaryAccountNumber());
        lipaFareRequest.setMatatuBillNumber(internalLipaFareRequest.getMatatuBillNumber());
        lipaFareRequest.setAmount(internalLipaFareRequest.getAmount());
        lipaFareRequest.setMerchantCode("669994");
        lipaFareRequest.setTransactionFee(0);
        lipaFareRequest.setReason("Lipa Fare");
        lipaFareRequest.setCallBackUrl(sasaPayConfig.getLipaFareCallBack());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(lipaFareRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getLipaFareEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, sasaPayApi.getAccessToken().getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), LipaFareResponse.class);
        } catch (IOException ex) {
            log.error(String.format("Unable to lipa fare -> %s", ex.getLocalizedMessage()));
            return null;
        }
    }
}
