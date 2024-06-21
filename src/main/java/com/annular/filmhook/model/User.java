package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "user_type")
    private String userType;

    @Column(name ="tempToken")
    private String tempToken;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_Name")
    private String lastName;
    
    @Column(name = "middle_Name")
    private String middleName;
    
    @JsonIgnore
    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "dob")
    private String dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;
    
    @Column(name = "filmHookOtp")
    private Integer filmHookOtp;

    @Column(name = "district")
    private String district;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "skin_tone")
    private String skinTone;

    @Column(name = "hair_color")
    private String hairColor;

    @Column(name = "bmi")
    private String bmi;

    @Column(name = "chest_size")
    private String chestSize;

    @Column(name = "waist_size")
    private String waistSize;

    @Column(name = "biceps")
    private String biceps;

    @Column(name = "religion")
    private String religion;

    @Column(name = "caste")
    private String caste;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "spouse_name")
    private String spouseName;

    @Column(name = "children_names")
    private String childrenNames;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "fatherName")
    private String fatherName;

    @Column(name = "brother_names")
    private String brotherNames;

    @Column(name = "sister_names")
    private String sisterNames;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "college_name")
    private String collegeName;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "work_category")
    private String workCategory;

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

    @Column(name = "reset_password")
    private String resetPassword;
    
    @Column(name ="otp")
    private Integer otp;
    
    @Column(name ="emailOtp")
    private Integer emailOtp;
    
    @Column(name = "film_hook_code")
    private String filmHookCode;
    
    @Column(name = "mobile_Number_status")
    private Boolean mobileNumberStatus;
    
    @Column(name = "forgotOtp")
    private String forgotOtp;
    
    @Column(name = "admin_Page_Status")
    private Boolean adminPageStatus;
    
    @Column(name = "firebase_device_token")
    private String firebaseDeviceToken;
    
    @Column(name = "admin_review")
    private String adminReview;

    @Column(name = "living_place")
    private String livingPlace;

    @Column(name = "birth_place")
    private String birthPlace;
    
    @Column(name = "experience")
    private Integer experience;
    
    @Column(name = "schedule")
    private String schedule;
    
    @Column(name = "secondary_email")
    private String secondaryEmail;
    
    @Column(name = "secondaryemail_otp")
    private Integer secondaryemailOtp;
    
    @Column(name = "verified")
    private Boolean verified;
    
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Location location;
    

}
