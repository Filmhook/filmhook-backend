package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.PaymentWebModel;
import com.annular.filmhook.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    public static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/generatePaymentToken")
    private ResponseEntity<?> generatePaymentToken(@RequestBody PaymentWebModel paymentWebModel) {
        try {
            PaymentWebModel tokenResponse = paymentService.generatePaymentToken(paymentWebModel);
            if (tokenResponse != null)
                return ResponseEntity.ok().body(new Response(1, "Success", tokenResponse));
        } catch (Exception e) {
            logger.error("Error at generatePaymentToken() -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error while generating the payment order token...");
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }

}
