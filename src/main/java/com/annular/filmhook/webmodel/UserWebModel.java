package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserWebModel {

    private Integer userId;
    private String email;
    private String password;
    private String userType;
    private String verificationCode;
    private String filmHookCode;
    private String name;
    private String firstName;
    private String lastName;
    private String middleName;


    // Biography Section
    private String dob;
    private String age;
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
}
