package com.annular.filmhook.webmodel;
import lombok.Data;

@Data
public class AuditionPaymentWebModel {
    private Integer auditionPaymentId;
    private Integer projectId;
    private Integer userId;
    private Integer selectedDays; 
    private String txnid;
    private String paymentStatus;
    private String reason;
    private String paymentHash;
   private Double totalAmount;
   private Integer totalTeamNeed;
	private String productinfo;
	private String firstname;
	private String email;
	private String key;
	private String PhoneNumber;
}