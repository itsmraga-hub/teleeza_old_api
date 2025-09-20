package com.teleeza.wallet.teleeza.bima.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRequest {
    @JsonProperty("customer")
    private CustomerItem customer;
    @JsonProperty("familyMembers")
    private List<FamilyMembersItem> familyMembers;

    @JsonProperty("nextOfKin")
    private NextOfKinItem nextOfKin;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("endDate")
    private String endDate;
//    @JsonProperty("loanAmount")
//    private int loanAmount;
//    @JsonProperty("assetValue")
//    private int assetValue;
    @JsonProperty("productPackId")
    private int productPackId;
}


