package com.teleeza.wallet.teleeza.subscription.service;

import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.response.MerchantToBeneficiaryResponse;
import com.teleeza.wallet.teleeza.subscription.dtos.requests.SubscriptionDto;
import com.teleeza.wallet.teleeza.subscription.dtos.responses.SubscriptionResponse;

public interface FreemiumService {
    SubscriptionResponse freemiumSubscription(SubscriptionDto subscriptionRequest);
    MerchantToBeneficiaryResponse sendSubscriptionDiscount(String accountNumber, Integer amount);
    MerchantToBeneficiaryResponse sendReferralCommission(String accountNumber, Integer amount);
    MerchantToBeneficiaryResponse sendCashback(String accountNumber, Integer amount);

    MerchantToBeneficiaryResponse sendResidualIncome(String accountNumber, Integer amount);
}
