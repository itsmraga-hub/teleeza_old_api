package com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests;

import lombok.Data;

@Data
public class TopUpVerificationRequest {
    private String MerchantRequestID;
    private String BillRefNumber;
}
