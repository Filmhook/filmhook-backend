package com.annular.filmhook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
public class HashService {

    @Value("${payu.key}")
    private String merchantKey;

    @Value("${payu.salt}")
    private String merchantSalt;

    /**
     * Generate a hash for PayU payment request
     */
    public String generateHash(String txnid, String amount, String productinfo, String firstname, String email) {
        String hashString = merchantKey + "|" + txnid + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" + merchantSalt;
        return hashCal("SHA-512", hashString);
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
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
