package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalKycRequest {
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @JsonProperty("DocumentImageBack")
    private String documentImageBack;

    @JsonProperty("PassportSizePhoto")
    private String passportSizePhoto;

//    @JsonProperty("MerchantCode")
//    private String merchantCode;

    @JsonProperty("DocumentImageFront")
    private String documentImageFront;
}
