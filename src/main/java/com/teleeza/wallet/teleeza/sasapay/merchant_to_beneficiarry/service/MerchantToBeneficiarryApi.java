package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.service;

import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.MerchantToBeneficiarryAuthResponse;

public interface MerchantToBeneficiarryApi {
    MerchantToBeneficiarryAuthResponse getAccessToken();
}
