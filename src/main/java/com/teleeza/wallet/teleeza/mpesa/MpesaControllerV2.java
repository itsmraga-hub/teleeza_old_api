package com.teleeza.wallet.teleeza.mpesa;


import com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses.B2CTransactionResponse;
import com.teleeza.wallet.teleeza.daraja.service.DarajaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("mpesa")
//public class MpesaControllerV2 {
//    @Autowired
//    private final DarajaApi darajaApi;
//
//
//    public MpesaControllerV2(DarajaApi darajaApi) {
//        this.darajaApi = darajaApi;
//    }
//
//    @PostMapping("/send")
//    public B2CTransactionResponse sendMoney(
//            @RequestParam(value = "phoneNumber") String phoneNumber,
//            @RequestParam(value = "amount") int amount) {
//        System.out.println("Sending money to " + phoneNumber + " amount " + amount);
//        return darajaApi.sendMoney(phoneNumber, amount);
//    }
//}
