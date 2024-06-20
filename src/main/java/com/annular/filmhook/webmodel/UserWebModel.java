package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWebModel {

	private Integer userId;
	private String email;
	private String password;
	private String userType;
	private String verificationCode;
	private String filmHookCode;
	private String name;
	private String firstName;
	private Integer filmHookOtp;
	private String lastName;
	private String middleName;
	private String firebaseDeviceToken;
	private String adminReview;
	private String livingPlace;
	private String birthPlace;
	private Integer experience;
	private String schedule;
	



	// Biography Section
	private String dob; // UI Format dd-MM-yyyy
	private Integer age;
	private String gender;
	private String country;
	private String state;
	private String district;
	private String phoneNumber;
	private String currentAddress;
	private String homeAddress;

	// Body Measurement Section
	private String height;
	private String weight;
	private String skinTone;
	private String hairColor;
	private String bmi;
	private String chestSize;
	private String waistSize;
	private String bicepsSize;

	// Personal Info section
	private String religion;
	private String caste;
	private String maritalStatus;
	private String spouseName;
	private List<String> childrenNames;
	private String motherName;
	private String fatherName;
	private List<String> brotherNames;
	private List<String> sisterNames;

	// Education Section
	private String schoolName;
	private String collegeName;
	private String qualification;

	// Current working section
	private String workCategory;

	// User status
	private boolean status;
	private boolean flag;

	private String token;

	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updateOn;

	private String currentPassword;
	private String newPassword;
	private Integer otp;
	private Boolean mobileNumberStatus;
	private Integer emailOtp;
	private String forgotOtp;
	private Boolean adminPageStatus;

	// Profile & Cover Photo
	private FileInputWebModel profilePhoto;
	private FileInputWebModel coverPhoto;
	private FileOutputWebModel profilePicOutput;
	private FileOutputWebModel coverPhotoOutput;

	//Pagination details
	private Integer pageNo;
	private Integer pageSize;

	private String bookingAvailableDate;
}
