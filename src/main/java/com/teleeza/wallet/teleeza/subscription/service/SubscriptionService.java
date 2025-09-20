package com.teleeza.wallet.teleeza.subscription.service;

import com.teleeza.wallet.teleeza.subscription.dtos.SubscriptionDto;
import com.teleeza.wallet.teleeza.subscription.dtos.requests.InternalSubscriptionRequest;
import com.teleeza.wallet.teleeza.subscription.dtos.responses.SubscriptionResponse;
import com.teleeza.wallet.teleeza.subscription.entity.SubscriptionEntity;
import org.springframework.stereotype.Service;

@Service
public interface SubscriptionService extends CrudService<SubscriptionEntity>{
//    SubscriptionResponse beneficiaryToMerchantSubscription(InternalSubscriptionRequest subscriptionRequest);
//
//    SubscriptionResponse freemiumSubscription(SubscriptionDto subscriptionDto);

}
