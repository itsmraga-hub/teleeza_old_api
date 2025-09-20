package com.teleeza.wallet.teleeza.kokotoa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.kokotoa.dtos.requests.KokotoaPostRequest;
import com.teleeza.wallet.teleeza.kokotoa.dtos.responses.KokotoaPostResponse;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static com.teleeza.wallet.teleeza.utils.Constants.JSON_MEDIA_TYPE;

@Service
@Slf4j
public class KokotoaImpl implements KokotoaApi {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final KokotoaConfig kokotoaConfig;

    public KokotoaImpl(OkHttpClient okHttpClient, ObjectMapper objectMapper, KokotoaConfig kokotoaConfig) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.kokotoaConfig = kokotoaConfig;
    }

    @Override
    public KokotoaPostResponse sendTransactionToKokotoa(KokotoaPostRequest kokotoaPostRequest) {

        KokotoaPostRequest postRequest = new KokotoaPostRequest();
        kokotoaPostRequest.setName(kokotoaPostRequest.getName());
        kokotoaPostRequest.setDescription(kokotoaPostRequest.getDescription());
        kokotoaPostRequest.setCategory(kokotoaPostRequest.getCategory());
        kokotoaPostRequest.setDate(kokotoaPostRequest.getDate());
        kokotoaPostRequest.setMobile(kokotoaPostRequest.getMobile());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(postRequest)
        ));

        Request request = new Request.Builder()
                .url(kokotoaConfig.getKokotoaApiEndpoint())
                .post(body)
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
//                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), KokotoaPostResponse.class);
        } catch (IOException ex) {
            log.error("post to kokotoa {}", ex.getLocalizedMessage());
            return null;
        }
    }
}
