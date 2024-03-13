package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer userId;

	@Column(name = "name")
	private String name;
	
	@JsonIgnore
	@Column(name = "verification_code")
	private Integer verificationCode;

	@Column(name = "dob")
	private String dob;

	@Column(name = "gender")
	private String gender;

	@Column(name = "country")
	private String country;

	@Column(name = "state")
	private String state;
	
	@Column(name = "email")
	private String email;

	@Column(name = "district")
	private String district;

	@JsonIgnore
	@Column(name = "password")
	private String password;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "status")
	private boolean status;

	@Column(name= "created_by")
	private Integer createdBy;
	
	@CreationTimestamp
	@Column(name= "created_on")
	private Date createdOn;

	@Column(name= "updated_by")
	private Integer updatedBy;
	
	@Column(name= "updated_on")
	@CreationTimestamp
	private Date updatedOn;

	@Column(name= "user_type")
	private String userType;
	
	@Column(name= "user_password")
	private String resetPassword;

}
