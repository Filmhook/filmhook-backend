package com.annular.filmhook.model;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "promote")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Promote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promote_id")
    private Integer promoteId;
    
    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;
    
	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;
	
	@Column(name = "number_of_days")
	private Integer numberOfDays;
	
	@Column(name = "amount")
	private Integer amount;
	
	@Column(name = "total_cost")
	private Integer totalCost;
	
	@Column(name = "tax_fee")
	private Integer taxFee;
	
	@Column(name = "cgst")
	private Integer cgst;
	
	@Column(name = "sgst")
	private Integer sgst;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "user_id")
	private Integer userId;
}
