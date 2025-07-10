package com.annular.filmhook.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.annular.filmhook.service.AuditionService;
import com.annular.filmhook.service.PaymentService;

import java.security.MessageDigest;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/payment")
public class PayUHashController {

    @Value("${payu.key}")
    private String merchantKey;

    @Value("${payu.salt}")
    private String merchantSalt;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AuditionService auditionService;

    @PostMapping("/generateHash")
    public Map<String, String> generateHash(@RequestBody Map<String, String> request) {
        String txnid = request.get("txnid");
        String amount = request.get("amount");
        String productinfo = request.get("productinfo");
        String firstname = request.get("firstname");
        String email = request.get("email");

        // Format: key|txnid|amount|productinfo|firstname|email||||||||||salt
        String hashString = merchantKey + "|" + txnid + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" + merchantSalt;

        String hash = hashCal("SHA-512", hashString);

        Map<String, String> response = new HashMap<>();
        response.put("hash", hash);
        return response;
    }

    private String hashCal(String type, String str) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] hashseq = str.getBytes();
            byte[] digest = md.digest(hashseq);
            StringBuilder hexString = new StringBuilder();
            for (byte aDigest : digest) {
                String hex = Integer.toHexString(0xFF & aDigest);
                if (hex.length() == 1) hexString.append("0");
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    @PostMapping("/payment-success")
    public ResponseEntity<?> handlePaymentSuccess(@RequestParam Map<String, String> params) {
        String status = params.get("field9"); // should be "success"
        String txnid = params.get("txnid");
        String mihpayid = params.get("mihpayid"); // PayU txn id
        String amount = params.get("amount");

        // TODO: Verify hash (optional but recommended)

        // Update payment status in your database
        auditionService.updatePaymentStatus(txnid, "SUCCESS", mihpayid, amount);

        // Redirect to frontend success page (optional)
        return ResponseEntity.ok("Payment Success");
    }
    
    @PostMapping("/payment-failure")
    public ResponseEntity<?> handlePaymentFailure(@RequestParam Map<String, String> params) {
        try {
           
            String status = params.get("field9");
            String txnid = params.get("txnid");
            String mihpayid = params.get("mihpayid");
            String amount = params.get("amount");

            if (txnid == null || mihpayid == null || amount == null) {
                System.out.println("Missing one or more required parameters");
                return ResponseEntity.badRequest().body("Missing required params");
            }

            auditionService.updatePaymentStatus(txnid, status, mihpayid, amount);

            System.out.println("✔️ Payment failure status updated successfully.");

            return ResponseEntity.ok("");
        } catch (Exception e) {
            System.err.println("❌ Error handling payment failure:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + e.getMessage());
        }
    }


}