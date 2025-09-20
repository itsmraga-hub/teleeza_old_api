package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class TupayAirtimeResponse {
    private String id;
    private String reference;
    private String message;
    private Integer status;
}
