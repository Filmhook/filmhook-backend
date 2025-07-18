package com.annular.filmhook.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Promote;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.PaymentDetailsService;
import com.annular.filmhook.webmodel.LiveDetailsWebModel;
import com.annular.filmhook.webmodel.PaymentDetailsWebModel;

@RestController
@RequestMapping("/payment")
public class PaymentDetailsController {
	@Autowired
	private PromoteRepository promoteRepository; 
	@Autowired
	PaymentDetailsService paymentDetailsService;
	@Autowired
	private UserRepository userRepository;
	
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
    
    @PostMapping("/promotionPending")
    public ResponseEntity<?> promotionPending(@RequestBody PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            logger.info("promotionPending controller start");
            return paymentDetailsService.promotionPending(paymentDetailsWebModel);
        } catch (Exception e) {
            logger.error("promotionPending Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
    
    
    @PostMapping("/promotionFailed")
    public ResponseEntity<?> promotionFailed(@RequestBody PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            logger.info("promotionFailed controller start");
            return paymentDetailsService.promotionFailed(paymentDetailsWebModel);
        } catch (Exception e) {
            logger.error("promotionFailed Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/promotionForCron")
    public ResponseEntity<?> promotionForCron(@RequestBody PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            logger.info("promotionForCron controller start");
            return paymentDetailsService.promotionForCron(paymentDetailsWebModel);
        } catch (Exception e) {
            logger.error("promotionForCron Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @GetMapping("/retry-details")
    public ResponseEntity<?> getPromotionRetryDetails(@RequestParam Integer promotionId) {
        Promote promote = promoteRepository.findById(promotionId)
            .orElseThrow(() -> new RuntimeException("Promotion not found"));

        Integer userId = promote.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> data = new HashMap<>();
        data.put("promotionId", promote.getPromoteId());
        data.put("amount", promote.getAmount());
        data.put("name", user.getName());
        data.put("email", user.getEmail());

        return ResponseEntity.ok(new Response(1, "Retry payment data", data));
    }


    
}
