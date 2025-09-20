package com.teleeza.wallet.teleeza.async;

import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.notification.model.dtos.requests.PushNotificationRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.subscription.entity.SubscriptionEntity;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class CheckSubscriptionExpiryService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;

    @Autowired
    private AdvantaSmsApiImpl advantaSmsApiImpl;
    @Autowired
    private PushNotificationService pushNotificationService;

    @Scheduled(cron = "0/30 * * * * ?")
    public void updateSubscriptionStatus() {
//        SubscriptionEntity subscription = subscriptionRepository.getExpiredSubscription();

        CustomerEntity subscription = customerRegistrationRepository.getExpiredSubscription();


        if(subscription!=null){
            CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(subscription.getMobileNumber());
            customerRegistrationRepository.updateUserSubscriptionStatus(subscription.getMobileNumber());
            subscriptionRepository.updateSubscriptionTableInfo(customer.getMobileNumber());
//             send sms notification
            DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = FOMATTER.format(subscription.getExpirationTime());

            // send push notification to recipient
            PushNotificationRequest request = new PushNotificationRequest();
            request.setTitle("Teleeza");
            request.setMessage("Dear "
                    + customer.getFirstName() +
                    ", your Teleeza  Jiinue Package subscription expired on " +ldtString +
                    ". Please renew your subscription to continue enjoying Teleeza");
            request.setToken(customer.getFcmToken());
            request.setTopic("Jiinue Package");
            pushNotificationService.sendPushNotificationToRecipientToken(request);

        }
    }

//    @Scheduled(cron = "0/30 * * * * ?")
    public void getUserExpiringSubscriptionAndUpdateStatus(){
        SubscriptionEntity subscription = subscriptionRepository.getUserExpiringSubscription();

        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(subscription.getBeneficiaryAccountNumber());
        customerRegistrationRepository.updateUserSubscriptionStatus(subscription.getBeneficiaryAccountNumber());
        subscriptionRepository.updateSubscriptionTableInfo(customer.getCustomerAccountNumber());
//             send sms notification
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
        String ldtString = FOMATTER.format(subscription.getExpirationTime());
        advantaSmsApiImpl.sendSmsNotification(
                "Dear "
                        + customer.getFirstName() +
                        ", your Teleeza  Jiinue Package subscription has expired."+
                        "Please renew your subscription to continue enjoying Teleeza" ,
                customer.getMobileNumber()
        );
    }

//    @Scheduled(cron = "0 0 11 * * ?")
//    public void sendSubscriptionExpiryNotification(){
//        CustomerEntity subscription = customerRegistrationRepository.getExpiredSubscription();
//        CustomerEntity customer = customerRegistrationRepository.findByCustomerAccountNumber(subscription.getCustomerAccountNumber());
//
//        LocalDateTime now = LocalDateTime.now();
//        long daysLeftToExpiry = ChronoUnit.DAYS.between(now,subscription.getExpirationTime());
//
//        if(daysLeftToExpiry<=5){
//            // send push notification to recipient
//            PushNotificationRequest request = new PushNotificationRequest();
//            request.setTitle("Teleeza");
//            request.setMessage("Dear "
//                    + customer.getFirstName() +
//                    ", your Teleeza  Freemium Package subscription expired on " +ldtString +
//                    ". Please renew your subscription to continue enjoying Teleeza");
//            request.setToken(customer.getFcmToken());
//            request.setTopic("Authentication");
//            pushNotificationService.sendPushNotificationToRecipientToken(request);
//        }
//    }

}
