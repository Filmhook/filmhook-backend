package com.annular.filmhook.util;

import java.security.MessageDigest;

public class PayUUtil {

    private static final String MERCHANT_KEY = "your-merchant-key";
    private static final String SALT = "your-salt";

    public static String generateHash(String txnid, String amount, String productinfo, String firstname, String email) {
        String hashString = MERCHANT_KEY + "|" + txnid + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" + SALT;
        return hashCal("SHA-512", hashString);
    }

    public static String hashCal(String type, String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance(type);
            byte[] hash = digest.digest(str.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}
