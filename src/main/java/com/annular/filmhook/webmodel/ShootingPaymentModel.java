package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class ShootingPaymentModel {
    private Integer bookingId;     
    private Integer userId;
    private Double amount;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String txnid;         
}
