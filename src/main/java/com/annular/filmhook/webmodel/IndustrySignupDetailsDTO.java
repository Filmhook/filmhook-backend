package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class IndustrySignupDetailsDTO {

	  private Integer userId;
	    private String fullName;

	    private Integer countryId;
	    private Integer industryId;
	    private Integer professionId;
	    private Integer subProfessionId;
	    private Integer yearsOfExperience;
	    private String verificationCode; 
}
