package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

@lombok.Data
public class AccessTokenResponse {
    public String access_token;
    public Long issued_at;
    public Integer expires_in;
}
