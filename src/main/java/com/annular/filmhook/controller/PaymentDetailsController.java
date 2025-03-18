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
import com.annular.filmhook.service.PaymentDetailsService;
import com.annular.filmhook.webmodel.LiveDetailsWebModel;
import com.annular.filmhook.webmodel.PaymentDetailsWebModel;

@RestController
@RequestMapping("/payment")
public class PaymentDetailsController {
	
	@Autowired
	PaymentDetailsService paymentDetailsService;
	
	public static final Logger logger = LoggerFactory.getLogger(PaymentDetailsController.class);
	
    @PostMapping("/savePayment")
    public ResponseEntity<?> savePayment(@RequestBody PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            logger.info("savePayment controller start");
            return paymentDetailsService.savePayment(paymentDetailsWebModel);
        } catch (Exception e) {
            logger.error("savePayment Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
    
    @PostMapping("/emailSend")
    public ResponseEntity<?> emailSend(@RequestBody PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            logger.info("emailSend controller start");
            return paymentDetailsService.emailSend(paymentDetailsWebModel);
        } catch (Exception e) {
            logger.error("emailSend Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
    

}
