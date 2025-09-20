package com.teleeza.wallet.teleeza.subscription.controller;

import com.teleeza.wallet.teleeza.subscription.entity.Organisation;
import com.teleeza.wallet.teleeza.subscription.entity.PlanEntity;
import com.teleeza.wallet.teleeza.subscription.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/teleeza-wallet")
public class OrganisationController {
    @Autowired
    private OrganisationRepository organisationRepository;

    @GetMapping("/subscription-organisations")
    public Map<String, List<Organisation>> getSubscriptionPlans() {
        Map<String, List<Organisation>> response = new HashMap<String, List<Organisation>>();
        response.put("organisations", organisationRepository.findAll());
        return response;
    }
}
