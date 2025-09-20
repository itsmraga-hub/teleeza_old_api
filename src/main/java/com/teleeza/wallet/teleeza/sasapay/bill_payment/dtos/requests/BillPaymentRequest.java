package com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillPaymentRequest {
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("BillRefNumber")
    private String billRefNumber;
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("SasaPayBillNumber")
    private Long sasaPayBillNumber;
    @JsonProperty("Amount")
    private Integer amount;
    @JsonProperty("MerchantCode")
    private String merchantCode;
    @JsonProperty("TransactionFee")
    private Integer transactionFee;
    @JsonProperty("Reason")
    private String reason;
    @JsonProperty("CallBackUrl")
    private String callBackUrl;
}
