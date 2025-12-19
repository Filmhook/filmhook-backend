package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.PaymentModule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsDTO {
	 private Integer referenceId;       // bookingId, auditionId, promotionId, etc.
	    private PaymentModule moduleType;  // AUDITION, SHOOTING, PROMOTION

	    private Integer userId;
	    private Double amount;

	    private String fullName;
	    private String email;
	    private String phoneNumber;

	    private Integer selectedDays;      
	    private String productInfo;     

	    private String txnid;      
}
