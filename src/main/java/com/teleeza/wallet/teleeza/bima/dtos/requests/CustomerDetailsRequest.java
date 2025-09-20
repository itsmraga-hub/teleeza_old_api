package com.teleeza.wallet.teleeza.bima.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.teleeza.wallet.teleeza.bima.entities.Customer;
import com.teleeza.wallet.teleeza.bima.entities.FamilyMember;
import com.teleeza.wallet.teleeza.bima.entities.NextOfKin;
import lombok.Data;

import java.util.List;
@Data
public class CustomerDetailsRequest {

    private NextOfKin nextOfKin;

    private List<FamilyMember> familyMember;

    private Customer customer;

    private String startDate;
    private String endDate;
    private int loanAmount;
    private int assetValue;
    private int productPackId;
}
