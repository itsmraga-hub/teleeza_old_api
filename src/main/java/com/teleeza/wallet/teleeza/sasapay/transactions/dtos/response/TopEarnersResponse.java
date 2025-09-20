package com.teleeza.wallet.teleeza.sasapay.transactions.dtos.response;

import lombok.Data;

@Data
public class TopEarnersResponse {
    private Long id;
    private String benefiarryAccNumber;
    private Double amount;

}
