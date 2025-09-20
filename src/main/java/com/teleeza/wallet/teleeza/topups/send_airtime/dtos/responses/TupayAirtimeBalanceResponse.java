package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

import com.fasterxml.jackson.databind.JsonNode;

@lombok.Data
public class TupayAirtimeBalanceResponse {
    public Integer status;
    public Double balance;
    public Double amount;
    public String currency;
}
