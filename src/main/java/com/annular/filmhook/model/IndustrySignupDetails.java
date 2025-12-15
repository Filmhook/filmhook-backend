package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "industry_signup_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndustrySignupDetails {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;


	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;


	    @Column(name = "full_name", nullable = false)
	    private String fullName;

	    @Column(name = "years_of_experience")
	    private Integer yearsOfExperience;


	    @ManyToOne
	    @JoinColumn(name = "country_id")
	    private Country country;


	    @ManyToOne
	    @JoinColumn(name = "industry_id")
	    private Industry industry;

	    @ManyToOne
	    @JoinColumn(name = "profession_id")
	    private FilmProfession profession;

	    @ManyToOne
	    @JoinColumn(name = "sub_profession_id")
	    private FilmSubProfession subProfession;

	    @Column(name = "verification_code", length = 4)
	    private String verificationCode;

	    @Builder.Default
	    @Column(name = "verified")
	    private Boolean verified = false;

	    @CreationTimestamp
	    @Column(name = "created_on")
	    private Date createdOn;
	

}
