package com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.service;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.requests.InternalPostPaidBillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.responses.PostPaidBillPaymentResponse;

public interface PostPaidApi {
    PostPaidBillPaymentResponse lipaPostPaidBills(InternalPostPaidBillPaymentRequest internalPostPaidBillPaymentRequest);

}
