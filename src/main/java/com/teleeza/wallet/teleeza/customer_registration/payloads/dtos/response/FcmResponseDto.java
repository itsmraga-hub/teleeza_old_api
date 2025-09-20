package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FcmResponseDto {
    private String beneficiaryAccNo;
    private String fcmToken;
    private String message;
}
