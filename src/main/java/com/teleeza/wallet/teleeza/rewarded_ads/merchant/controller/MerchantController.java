package com.teleeza.wallet.teleeza.rewarded_ads.merchant.controller;

import com.teleeza.wallet.teleeza.customer_registration.service.KycStorageService;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.Merchant;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.service.MerchantService;
import com.teleeza.wallet.teleeza.rewarded_ads.service.RewardedAdsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/merchant/api")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;
    private final RewardedAdsServiceImpl rewardedAdsService;
    private final KycStorageService kycStorageService;
    

    // @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createMerchant(
            @RequestBody Merchant merchant
    ) {
        return ResponseEntity.ok(merchantService.createMerchant(merchant));
    }

    @GetMapping("/companies")
    public ResponseEntity<?> getCompanies() {
        return ResponseEntity.ok(merchantService.getCompanies());
    }
    
    @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")
    @PostMapping("/validate-voucher")
    public ResponseEntity<?> validateVoucher(
            @RequestParam(value = "voucherCode") String voucherCode,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "amount") String amount,
            @RequestParam(value = "adType") String adType,
            @RequestParam(value = "adID") Long adID,
            @RequestParam(value = "userPhone") String userPhone
    ) {
        return ResponseEntity.ok(rewardedAdsService.validateVoucher(voucherCode, phone, amount, userPhone, adType, adID));
    }

    @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")

    @GetMapping("/redeemed-vouchers")
    public ResponseEntity<?> getMerchantRedeemedVouchers(
            @RequestParam(value = "phone") String phoneNumber,
            @RequestParam(value = "days") Long days
    ) {
        return ResponseEntity.ok(rewardedAdsService.getMerchantRedeemedVouchers(phoneNumber,days));
    }

    @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")
    @GetMapping("/redeemed-vouchers-all")
    public ResponseEntity<?> getAllMerchantRedeemedVouchers(
            @RequestParam(value = "phone") String phoneNumber
    ) {
        return ResponseEntity.ok(rewardedAdsService.getAllMerchantRedeemedVouchers(phoneNumber));
    }

    @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")
    @GetMapping("/text-redeemed-vouchers")
    public ResponseEntity<?> getAllTextMerchantRedeemedVouchers(
            @RequestParam(value = "phone") String phoneNumber
    ) {
        return ResponseEntity.ok(rewardedAdsService.getAllTextAdsMerchantRedeemedVouchers(phoneNumber));
    }

    @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")
    @GetMapping("/video-redeemed-vouchers")
    public ResponseEntity<?> getAllVideoMerchantRedeemedVouchers(
            @RequestParam(value = "phone") String phoneNumber
    ) {
        return ResponseEntity.ok(rewardedAdsService.getAllVideoAdsMerchantRedeemedVouchers(phoneNumber));
    }

    @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")
    @GetMapping("/audio-redeemed-vouchers")
    public ResponseEntity<?> getAllAudioMerchantRedeemedVouchers(
            @RequestParam(value = "phone") String phoneNumber
    ) {
        return ResponseEntity.ok(rewardedAdsService.getAllAudioAdsMerchantRedeemedVouchers(phoneNumber));
    }

    @PreAuthorize("hasAnyRole('ROLE_MERCHANT','ROLE_ADMIN')")
    @PostMapping("/upload-profile")
    public ResponseEntity<?> uploadMerchantProfile(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "phone") String phone
    ) throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("imageUrl", kycStorageService.uploadMerchantProfileImage(file, phone));
        return ResponseEntity.ok(response);
    }
}
