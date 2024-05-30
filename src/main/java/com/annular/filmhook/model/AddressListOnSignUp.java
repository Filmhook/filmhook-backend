package com.annular.filmhook.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "AddressListOnSignUp")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddressListOnSignUp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "addresslist_onsignup_id")
	private Integer addressListOnSignUp;

	@Column(name = "address")
	private String address;

	@Column(name = "address_isactive")
	private boolean addressIsactive;

	@Column(name = "address_created_by")
	private Integer addressCreatedBy;

	@Column(name = "address_createdon")
	@CreationTimestamp
	private Date addresssCreatedOn;

	@Column(name = "address_updated_by")
	private Integer addressUpdatedBy;

	@Column(name = "address_updated_on")
	@CreationTimestamp
	private Date address_Updated_On;

}
