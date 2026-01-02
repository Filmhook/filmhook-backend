package com.annular.filmhook.webmodel;

import java.util.Date;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsWebModel {
    
	
    private Integer paymentId;
    private String txnid;
    private String amount;
    private String productinfo;
    private String fullName;
    private String email;
    private Integer userId;
    private Integer postId;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Boolean status;
    private Integer promoteId;
    private String reason;
    private String promotionStatus;

}
