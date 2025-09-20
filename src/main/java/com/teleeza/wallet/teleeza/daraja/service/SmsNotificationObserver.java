package com.teleeza.wallet.teleeza.daraja.service;

import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.daraja.entity.MpesaTransactions;
import com.teleeza.wallet.teleeza.daraja.repository.MpesaTransactionsRepository;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushAsyncResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Slf4j
public class SmsNotificationObserver implements DarajaObserver {
    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private AdvantaSmsApiImpl advantaSmsApiImpl;

    @Override
    public void update(StkPushAsyncResponse stkPushAsyncResponse) {
//        log.info("=======SMS OBSERVER LISTENING======");
//        MpesaTransactions transactions = mpesaTransactionsRepository.findByMerchantRequestId(stkPushAsyncResponse.getBody().getStkCallback().getMerchantRequestID());
//        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(transactions.getAccountReference());
//        String customerPhone = customer.getMobileNumber();
//        String phone = "" + customerPhone.substring(1);
//
//        // Calculate the first day of the next month
//        // TODO send bima sms based on first day of month. If current day is first day of month bima cover starts today
//        // TODO else bima starts first day of next month
//        LocalDate firstDayOfNextMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
//
//        if (stkPushAsyncResponse.getBody().getStkCallback().getResultCode() == 0) {
//            if (transactions.getSubscriptionPlan().equals("Monthly")) {
//                DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
//                String ldtString = FOMATTER.format(LocalDateTime.now().plusDays(30));
//                if (customer.getIsInitialSubscription().equals(true)
//                        && customer.getIsRenewal().equals(false)
//                ) {
//                    log.info("===INITIAL SUBSCRIPTION >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ", thank you for subscribing to Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Karibu "
//                                    + customer.getFirstName() +
//                                    " to Teleeza Jiinue Package." +
//                                    " with access to Ksh 115k in annual benefits. Bima is active from " + firstDayOfNextMonth +
//                                    ". Enjoy & keep referring!",
//                            phone
//                    );
//                } else if (customer.getIsInitialSubscription().equals(false)
//                        && customer.getIsRenewal().equals(true)
//                ) {
//                    log.info("=== SUBSCRIPTION RENEWAL >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ", thank your for renewing your subscription to the Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Asante "
//                                    + customer.getFirstName() +
//                                    " for your payment dated." + LocalDate.now() +
//                                    ".Your Bima will remain active and will be extended accordingly.",
//                            phone
//                    );
//                }
//            } else if (transactions.getSubscriptionPlan().equals("Quarterly")) {
//                log.info("Quarterly Sub");
//                DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
//                String ldtString = FOMATTER.format(LocalDateTime.now().plusDays(90));
//                if (customer.getIsInitialSubscription().equals(true)
//                        && customer.getIsRenewal().equals(false)
//                ) {
//                    log.info("===INITIAL SUBSCRIPTION >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ", thank you for subscribing to Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Karibu "
//                                    + customer.getFirstName() +
//                                    " to Teleeza Jiinue Package." +
//                                    " with access to Ksh 115k in annual benefits. Bima is active from " + firstDayOfNextMonth +
//                                    ". Enjoy & keep referring!",
//                            phone
//                    );
//                } else if (customer.getIsInitialSubscription().equals(false)
//                        && customer.getIsRenewal().equals(true)
//                ) {
//                    log.info("=== SUBSCRIPTION RENEWAL >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ", thank your for renewing your subscription to the Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Asante "
//                                    + customer.getFirstName() +
//                                    " for your payment dated." + LocalDate.now() +
//                                    ".Your Bima will remain active and will be extended accordingly.",
//                            phone
//                    );
//                }
//            } else if (transactions.getSubscriptionPlan().equals("Semi-Annual")) {
//                DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
//                String ldtString = FOMATTER.format(LocalDateTime.now().plusDays(180));
//                if (customer.getIsInitialSubscription().equals(true)
//                        && customer.getIsRenewal().equals(false)
//                ) {
//                    log.info("===INITIAL SUBSCRIPTION >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ", thank you for subscribing to Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Karibu "
//                                    + customer.getFirstName() +
//                                    " to Teleeza Jiinue Package." +
//                                    " with access to Ksh 115k in annual benefits. Bima is active from " + firstDayOfNextMonth +
//                                    ". Enjoy & keep referring!",
//                            phone
//                    );
//                } else if (customer.getIsInitialSubscription().equals(false)
//                        && customer.getIsRenewal().equals(true)
//                ) {
//                    log.info("=== SUBSCRIPTION RENEWAL >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ",thank your for renewing your subscription to the Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Asante "
//                                    + customer.getFirstName() +
//                                    " for your payment dated." + LocalDate.now() +
//                                    ".Your Bima will remain active and will be extended accordingly.",
//                            phone
//                    );
//                }
//            } else if (transactions.getSubscriptionPlan().equals("Annual")) {
//                DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
//                String ldtString = FOMATTER.format(LocalDateTime.now().plusDays(364));
//                if (customer.getIsInitialSubscription().equals(true)
//                        && customer.getIsRenewal().equals(false)
//                ) {
//                    log.info("===INITIAL SUBSCRIPTION >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ", thank you for subscribing to Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Karibu "
//                                    + customer.getFirstName() +
//                                    " to Teleeza Jiinue Package." +
//                                    " with access to Ksh 115k in annual benefits. Bima is active from " + firstDayOfNextMonth +
//                                    ". Enjoy & keep referring!",
//                            phone
//                    );
//                } else if (customer.getIsInitialSubscription().equals(false)
//                        && customer.getIsRenewal().equals(true)
//                ) {
//                    log.info("=== SUBSCRIPTION RENEWAL >>SENDING SMS====");
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Dear "
//                                    + customer.getFirstName() +
//                                    ", thank your for renewing your subscription to the Teleeza Jiinue Package." +
//                                    " Your subscription starts today and will expire on " + ldtString,
//                            phone
//                    );
//
//                    advantaSmsApiImpl.sendSmsNotification(
//                            "Asante "
//                                    + customer.getFirstName() +
//                                    " for your payment dated." + LocalDate.now() +
//                                    ".Your Bima will remain active and will be extended accordingly.",
//                            phone
//                    );
//                }
//            }
//        }

    }
}
