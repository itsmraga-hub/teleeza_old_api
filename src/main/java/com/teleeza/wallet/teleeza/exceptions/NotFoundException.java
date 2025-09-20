package com.teleeza.wallet.teleeza.exceptions;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotFoundException extends RuntimeException {
    private String message;
}