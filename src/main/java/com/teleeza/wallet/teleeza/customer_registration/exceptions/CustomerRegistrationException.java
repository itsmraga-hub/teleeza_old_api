package com.teleeza.wallet.teleeza.customer_registration.exceptions;

import lombok.Data;

@Data
public class CustomerRegistrationException {
    private String message;
    private String statusCode;

}
