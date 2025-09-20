package com.teleeza.wallet.teleeza.handlers;

import com.teleeza.wallet.teleeza.authentication.teleeza.exception.AuthApiException;
import com.teleeza.wallet.teleeza.authentication.teleeza.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerErrorException;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(ServerErrorException.class)
//    public ResponseEntity<ApiResponse> handeServerErrorException(Exception ex) {
//        System.out.println(ex.getMessage());
//        System.out.println(ex.getLocalizedMessage());
//        System.out.println(ex.getCause());
//        ApiResponse apiResponse = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
//    }

//    @ExceptionHandler(AuthApiException.class)
//    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthApiException ex){
//        System.out.println(ex.getMessage());
//        System.out.println(Arrays.toString(ex.getStackTrace()));
//        ApiResponse apiResponse = new ApiResponse(ex.getStatus().value(), ex.getMessage());
//        return ResponseEntity.status(ex.getStatus().value()).body(apiResponse);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException ex){
        System.out.println("Cuase : " + ex.getCause());
        System.out.println("Stack Trace : " + Arrays.toString(ex.getStackTrace()));
     return ex.getLocalizedMessage();
    }
}
