package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    private Integer referenceId;  
	                                 

	    @Enumerated(EnumType.STRING)
	    private PaymentModule moduleType;
	    
	    private Integer userId;
	    
	    private String fullName;
	    private String email;

	 
	    @Column(unique = true)
	    private String txnid;    
	    private String paymentHash;

	    private Double amount;
	    private String paymentStatus; 
	    private String reason;
	    private String paymentGateway;
	    private LocalDateTime expiryDate;
	    private LocalDateTime createdOn;
	    private LocalDateTime updatedOn;
	    private Integer createdBy;
	    private Integer updatedBy;
	}
