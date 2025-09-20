package com.teleeza.wallet.teleeza.sasapay.monitor_balance.controller;

import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses.BalanceResponse;
import com.teleeza.wallet.teleeza.sasapay.monitor_balance.service.UtilityApi;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("api/v1/waas/merchant-balances")
@Slf4j
public class UtilityController {
//    @Autowired
//    private SmsServiceInterface smsService;

    private final SasaPayConfig sasaPayConfig;
    private final UtilityApi utilityApi;

    public static int Count = 0;
    @Autowired
    private AdvantaSmsApiImpl advantaSmsApiImpl;

    Logger logger = LoggerFactory.getLogger(UtilityController.class);

    public UtilityController(UtilityApi utilityApi, SasaPayConfig sasaPayConfig) {
        this.sasaPayConfig = sasaPayConfig;
        this.utilityApi = utilityApi;
    }

    @GetMapping
    public ResponseEntity<BalanceResponse> getBalances(@RequestParam("MerchantCode") String MerchantCode) {
        BalanceResponse balanceResponse = utilityApi.getBalances(MerchantCode);
        return ResponseEntity.ok(balanceResponse);
    }

//    @Scheduled(fixedRate = 60000L, initialDelay = 1000L)
    public void utilityBalance() throws InterruptedException {
        double utilityBalance = getBalances(sasaPayConfig.getMerchantCode()).getBody().getData().getAccounts().get(1).getAccount_balance();
        //Send only 3 times
        if (utilityBalance <= 1000 & Count < 4) {
//            if (utilityBalance <= 100000) {
//                Count = 0;
//            }
            //Send To Kabutha
            advantaSmsApiImpl.sendSmsNotification(
                    "ATTENTION! PayBill 122122 utility Balance of KES " + utilityBalance + " is TOO LOW! Kindly move funds to the PayBill ASAP! " + new Date(),
                    "254722525397"
            );
//            //Send To Emmanuel
            advantaSmsApiImpl.sendSmsNotification(
                    "ATTENTION! PayBill 122122 utility Balance of KES " + utilityBalance + " is TOO LOW! Kindly move funds to the PayBill ASAP! " + new Date(),
                    "254795217389"
            );
            //Send To James
            advantaSmsApiImpl.sendSmsNotification(
                    "ATTENTION! PayBill 122122 utility Balance of KES " + utilityBalance + " is TOO LOW! Kindly move funds to the PayBill ASAP! " + new Date(),
                    "254795105144"
            );


            Count++;
            System.out.println("Sent Message " + Count + " times");
        }
    }

//    @Scheduled(fixedRate = 6000L, initialDelay = 2000L)
    public void workingBalance() throws InterruptedException {
        double workingBalance = getBalances(sasaPayConfig.getMerchantCode()).getBody().getData().getAccounts().get(2).getAccount_balance();
        if (workingBalance <= 1000) {
            //Send To Victor
//            advantaSmsApiImpl.sendSmsNotification(
//                    "Working  Balance is "+ workingBalance,
//                    "254726100423"
//            );
            // Send To Emmanuel
//            smsService.sendSMS(new SmsOptions()
//                    .setMessage("Working Balance is above 750 " + new Date())
//                    .setMobileNo("0795284379")
//            );

        }
    }
}
