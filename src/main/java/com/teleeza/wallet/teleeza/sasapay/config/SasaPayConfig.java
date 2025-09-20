package com.teleeza.wallet.teleeza.sasapay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sasapay.wallet")
public class SasaPayConfig {
    private String clientID;
    private String clientSecret;
    private String grantType;
    private String oauthEndPoint;
    private String kycOauthEndpoint;
    private String merchantCode;
    private String confirmationUrl;
    private String validationUrlEndpoint;
    private String confirmationUrlEndpoint;
    private String callBackUrl;
    private String topupVerification;
    private String c2cCallBack;

    private String beneficiaryOnBoardingEndpoint;
    private String customerRegistrationConfirmationEndpoint;
    private String loadCustomerWalletEndpoint;
    private String customerToCustomerTransferEndpoint;
    private String customerToMobileMoneyEndpoint;
    private String beneficiaryToMerchantEndpoint;
    private String billPaymentEndpoint;
    private String lipaTillEndpoint;
    private String walletCallBack;
    private String cashoutEndpoint;
    private String bankTransferEndpoint;
    private String buyAirtimeEndpoint;
    private String buyKplcTokensEndpoint;
    private String lipaFareEndpoint;
    private String lipaTVEndpoint;
    private String postPaidEndpoint;
    private String kycUpdateEndpoint;
    //callback urls
    private String subscriptionCallback;
    private String mobileTransferCallback;
    private String bankTransferValidation;
    private String payBillsCallBack;
    private String lipaTillsCallback;
    private String lipaFareCallBack;
    private String loadWalletCallBack;
    private String kplcCallBack;
    private String tvPaymentCallBack;
    private String airtimeCallBack;
    private String postPaidValidation;
    private String freemiumValidationCallBack;
    private String subscriptionExtensionValidation;
    private String cashoutValidationCallBack;
    private String registrationCallBack;

    // Merchant to Beneficiary
    private String merchantToBeneficiarryCliendID;
    private String merchantToBeneficiarrySecret;
    private String merchantToBeneficiarryCallBack;
    private String merchantToBeneficaryDiscountCallBack;
    private String merchantToBeneficaryCashbackCallBack;
    private String merchantToBeneficaryResidualCallBack;
}
