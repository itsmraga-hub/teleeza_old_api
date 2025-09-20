package com.teleeza.wallet.teleeza.subscription.controller;

import com.teleeza.wallet.teleeza.subscription.entity.PlanEntity;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/teleeza-wallet")
public class SubscriptionPlanController {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanController(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @GetMapping("/subscription-plans")
    public Map<String, List<PlanEntity>> getSubscriptionPlans() {
        Map<String, List<PlanEntity>> response = new HashMap<String, List<PlanEntity>>();
        response.put("plans", subscriptionPlanRepository.findAll());
        return response;
    }
}
