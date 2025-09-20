package com.teleeza.wallet.teleeza.rewarded_ads.service;

import com.teleeza.wallet.teleeza.rewarded_ads.repository.CustomVoucherRepository;

public class CustomVoucherService {
    private final CustomVoucherRepository customVoucherRepository;

    public CustomVoucherService(CustomVoucherRepository customVoucherRepository) {
        this.customVoucherRepository = customVoucherRepository;
    }
}
