package com.annular.filmhook.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shooting_location_owner_bank_details")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShootingLocationOwnerBankDetails {
	@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 
	 private String beneficiaryName;
	 private String mobileNumber;
	 private String accountNumber;
	 private String confirmAccountNumber;
	 private String ifscCode;
	 @OneToOne(mappedBy = "bankDetails")
	 private ShootingLocationPropertyDetails propertyDetails;
}
