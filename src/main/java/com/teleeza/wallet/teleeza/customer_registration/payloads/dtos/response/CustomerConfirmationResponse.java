package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerConfirmationResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("result")
    private Result result;

    @JsonProperty("cumulative_points")
    private int cumulativePoints;

    @JsonProperty("customer_balance")
    private String customerBalance;

    @JsonProperty("customer_account_number")
    private String customerAccountNumber;

    @JsonProperty("redeemed_points")
    private int redeemedPoints;

    @JsonProperty("available_points")
    private int availablePoints;

    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("cumulative_spent_amount")
    private String cumulativeSpentAmount;

    @JsonProperty("status")
    private int status;

}
