package com.teleeza.wallet.teleeza.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("customer", responseObj);
        return new ResponseEntity<Object>(map, status);
    }

    public static ResponseEntity<Object> customerConfirmationResponse(HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("customer", responseObj);
        return new ResponseEntity<Object>(map, status);
    }
}
