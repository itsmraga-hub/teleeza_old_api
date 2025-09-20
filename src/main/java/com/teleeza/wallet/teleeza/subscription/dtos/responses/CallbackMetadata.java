package com.teleeza.wallet.teleeza.subscription.dtos.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CallbackMetadata {

    @JsonProperty("Item")
    private List<ItemItem> item;
}