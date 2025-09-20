package com.teleeza.wallet.teleeza.bima.dtos.requests;

import lombok.Data;

@Data
public class ClaimRequest{
    //	private int policyId;
    private int productId;
    private int requestedAmount;
    private String customerMobileNumber;
    private String dateOfDischarge;
    private String incident;
    private String dateOfIncident;
    private String incidentLocation;
}
