package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Float adminReview;
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
    private Boolean deniedAccessRead;

    // Body Measurement Section
    private String height;
    private String weight;
    private String skinTone;
    private String hairColor;
    private String bmi;
    private String chestSize;
    private String waistSize;
    private String bicepsSize;
    private String empId;

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
    private Boolean industryUserVerified;
    private String deleteReason;
    private Boolean deactivateAccessOrdeny;
    private String rejectReason;

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
    private String countryCode;
    private String refCode;
    

    // Profile & Cover Photo
    // Input fields for save
    private FileInputWebModel profilePhoto;
    private FileInputWebModel coverPhoto;

    // Output fields for view
    private FileOutputWebModel profilePicOutput;
    private String profilePicUrl;
    private List<FileOutputWebModel> coverPhotoOutput;
    private String coverPicUrl;

    //Pagination details
    private Integer pageNo;
    private Integer pageSize;

    private String bookingAvailableDate;
    private String secondaryEmail;
    private Integer secondaryemailOtp;
    private Boolean verified;
    private Double distance;
    private String professionName;
    private String changeEmailId;
    private String bust;
    private String hip;
    private String heightUnit;
    private String weightUnit;
    
    //onlineStatus
    private Boolean onlineStatus;
    //optional set userType
    private Boolean userFlag;
    private String referralCode;
   	private String locationName;
   	private String locationAddress;
   	private String locationLandMark;
   	private String locationLatitude;
   	private String locationLongitude;
   	
   	private int followingListCount;
   	private int followersListCount;
    private Boolean pinProfileStatus;
    private boolean followingStatus;
	
	


}
