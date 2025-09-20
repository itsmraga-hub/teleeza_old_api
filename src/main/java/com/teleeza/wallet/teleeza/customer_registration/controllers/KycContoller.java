package com.teleeza.wallet.teleeza.customer_registration.controllers;

import com.teleeza.wallet.teleeza.customer_registration.service.KycStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/teleeza-wallet")
@Slf4j
public class KycContoller {
    @Autowired
    private KycStorageService service;

    @PostMapping(path = "/upload-mugshot", headers = "Content-Type=multipart/form-data")
    public Map<String, Object> uploadUseMugshot(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "accountNumber") String accountNumber

    ) throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("imageUrl", service.uploadUseMugshot(file, accountNumber));
        return response;
    }

    @PostMapping(path = "/upload-direcly-to-s3", headers = "Content-Type=multipart/form-data")
    public Map<String, Object> uploadDirectlyToS3(
            @RequestParam(value = "file") MultipartFile file)
            throws IOException {
        Map<String,Object> response = new HashMap<>();
        response.put("result",service.uploadFileToS3Directly(file));
        return response;

    }

    @PostMapping(path = "/upload-id-front", headers = "Content-Type=multipart/form-data")
    public Map<String, Object> uplaodIdFront(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "accountNumber") String accountNumber

    ) throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("imageUrl", service.uploadIdFront(file, accountNumber));
        return response;
    }

    @PostMapping(path = "/upload-id-back", headers = "Content-Type=multipart/form-data")
    public Map<String, Object> uploadIdBack(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "accountNumber") String accountNumber

    ) throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("imageUrl", service.uploadIdBAck(file, accountNumber));
        return response;
    }

    @PostMapping(path = "/upload-to-sasapay", headers = "Content-Type=multipart/form-data")
    public Map<String, Object> uploadKycToSasaPay(
            @RequestParam(value = "beneficiaryAccNumber") String beneficiaryAccNumber,
            @RequestPart(value = "passport") MultipartFile passport,
            @RequestPart(value = "idFront") MultipartFile idFront,
            @RequestPart(value = "idBack") MultipartFile idBack
    ) throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("upload", service.uploadKyc(
                beneficiaryAccNumber,
                passport,
                idFront,
                idBack));
        return response;
    }
}
