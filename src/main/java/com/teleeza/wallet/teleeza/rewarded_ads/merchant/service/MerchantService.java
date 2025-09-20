package com.teleeza.wallet.teleeza.rewarded_ads.merchant.service;

import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.Merchant;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository.CompaniesRepository;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final CompaniesRepository companiesRepository;

    public Map<String, Object> createMerchant(Merchant merchant) {
        Map<String, Object> map = new HashMap<>();
        Optional<Merchant> optionalMerchant = merchantRepository.findMerchantByMerchantNameAndPhone(
                merchant.getMerchantName(),
                merchant.getPhone()
        );

        if (optionalMerchant.isPresent()) {
            map.put("message", "Merchant exists with given name or phone");
            map.put("statusCode", "1");
            return map;
        } else {
            merchantRepository.save(merchant);
            map.put("message", "Merchant created successfully");
            map.put("statusCode", "0");
            return map;
        }
    }

    public Map<String, Object> getCompanies() {
        Map<String, Object> map = new HashMap<>();
        map.put("companies", companiesRepository.getAllCompanies());

        return map;
    }
}
