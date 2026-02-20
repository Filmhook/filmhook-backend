
package com.annular.filmhook.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashGenerator {
	

    @Value("${payu.key}")
    private String merchantKey;

    @Value("${payu.salt}")
    private String merchantSalt;

    public static String generateHash(String key, String txnid, String amount, String productinfo,
                                      String firstname, String email, String salt) {
    	
    
        // Print key and salt values
        System.out.println("Key: " + key);
        System.out.println("Salt: " + salt);
        String input = key + "|" + txnid + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" +salt;
        return sha512(input);
    }

    private static String sha512(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not found", e);
        }
    }
    
    // 🔹 Request Hash (Before Payment)
    public String generateRequestHash(
            String txnid,
            String amount,
            String productinfo,
            String firstname,
            String email,
            String udf1,
            String udf2,
            String udf3
    ) {

        StringJoiner joiner = new StringJoiner("|");

        joiner.add(merchantKey)
              .add(txnid)
              .add(amount)
              .add(productinfo)
              .add(firstname)
              .add(email)
              .add(udf1)
              .add(udf2)
              .add(udf3)
              .add("") // udf4
              .add("") // udf5
              .add("") // empty
              .add("")
              .add("")
              .add("")
              .add("")
              .add(merchantSalt);

        return hashCal("SHA-512", joiner.toString());
    }


    // 🔹 Response Hash (After Payment)
    public String generateResponseHash(Map<String, String> params) {

        String status = params.get("status");
        String txnid = params.get("txnid");
        String amount = params.get("amount");
        String productinfo = params.get("productinfo");
        String firstname = params.get("firstname");
        String email = params.get("email");

        String udf1 = params.getOrDefault("udf1", "");
        String udf2 = params.getOrDefault("udf2", "");
        String udf3 = params.getOrDefault("udf3", "");
        String udf4 = params.getOrDefault("udf4", "");
        String udf5 = params.getOrDefault("udf5", "");

        String additionalCharges = params.get("additionalCharges");

        String hashString;

        if (additionalCharges != null && !additionalCharges.isEmpty()) {

            hashString =
                    additionalCharges + "|" +
                    merchantSalt + "|" +
                    status +
                    "||||||" +   // 🔥 EXACT 6 EMPTY
                    udf5 + "|" +
                    udf4 + "|" +
                    udf3 + "|" +
                    udf2 + "|" +
                    udf1 + "|" +
                    email + "|" +
                    firstname + "|" +
                    productinfo + "|" +
                    amount + "|" +
                    txnid + "|" +
                    merchantKey;

        } else {

            hashString =
                    merchantSalt + "|" +
                    status + "||||||" +               
                    udf5 + "|" +
                    udf4 + "|" +
                    udf3 + "|" +
                    udf2 + "|" +
                    udf1 + "|" +
                    email + "|" +
                    firstname + "|" +
                    productinfo + "|" +
                    amount + "|" +
                    txnid + "|" +
                    merchantKey;
        }

        System.out.println("FINAL RAW STRING: " + hashString);

        return hashCal("SHA-512", hashString);
    }


    
    
    private String hashCal(String type, String str) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] digest = md.digest(str.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : digest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) hexString.append("0");
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
    
}
