package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests;

@lombok.Data
public class AirtimeRequest {
    public String account;
    public Double amount;
    public String currency;
    public String reference;
}
