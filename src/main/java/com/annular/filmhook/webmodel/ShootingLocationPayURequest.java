package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShootingLocationPayURequest {
	private Integer paymentId;
	private Integer bookingId;
	private String txnid;
	private String amount;
	private String productinfo;
	private String firstname;
	private String email;
	private Integer createdBy;
	private LocalDateTime createdOn;
	private Integer updatedBy;
	private Date updatedOn;
	private String status;
	private String phone;
	private String hash;
	

}
