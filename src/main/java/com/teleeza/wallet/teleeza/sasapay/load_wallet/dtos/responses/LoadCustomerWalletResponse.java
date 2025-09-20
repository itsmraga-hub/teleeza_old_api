package com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
public class LoadCustomerWalletResponse {

    @JsonProperty("manual_payment")
    private String manualPayment;

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("MerchantReference")
    private String merchantReference;

    @JsonProperty("TransactionReference")
    private String transactionReference;

    public LoadCustomerWalletResponse(String manualPayment, String message, String statusCode, String merchantReference, String transactionReference) {
        this.manualPayment = manualPayment;
        this.message = message;
        this.statusCode = statusCode;
        this.merchantReference = merchantReference;
        this.transactionReference = transactionReference;
    }
}