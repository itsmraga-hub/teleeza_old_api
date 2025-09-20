package com.teleeza.wallet.teleeza.authentication.teleeza.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {
    private Boolean error;
    private String message;

    public Message(String message,Boolean error) {
        this.message = message;
        this.error = error;
    }
}
