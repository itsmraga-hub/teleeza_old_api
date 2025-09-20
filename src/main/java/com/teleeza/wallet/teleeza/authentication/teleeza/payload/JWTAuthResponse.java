package com.teleeza.wallet.teleeza.authentication.teleeza.payload;

import com.teleeza.wallet.teleeza.authentication.teleeza.entity.User;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import lombok.Getter;
import lombok.Setter;

public class JWTAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Boolean error;
    private String message;
    private String email;
    @Getter
    @Setter
    private CustomerEntity user;
    @Setter
    @Getter
    private String status;
    private Boolean isMerchant;

    public JWTAuthResponse(String accessToken,
                           Boolean error,
                           String message,
                           String email,
                           String status,
                           Boolean isMerchant,
                           CustomerEntity user
                           ) {
        this.accessToken = accessToken;
        this.error = error;
        this.user = user;
        this.message = message;
        this.email = email;
        this.status = status;
        this.isMerchant = isMerchant;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Boolean getMerchant() {
        return isMerchant;
    }

    public void setMerchant(Boolean merchant) {
        isMerchant = merchant;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
