package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class ShootingPaymentModel {

    private Integer bookingId;     
    private Integer userId;

    private Double amount;

    private String firstname;
    private String email;
    private String phone;

    private String txnid;         
}
