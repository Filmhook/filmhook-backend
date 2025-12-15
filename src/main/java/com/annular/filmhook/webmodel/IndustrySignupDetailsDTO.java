package com.annular.filmhook.webmodel;

import lombok.Data;
import lombok.Builder;

@Builder
@Data
public class IndustrySignupDetailsDTO {

	 private Integer userId;
	    private String fullName;

	    private Integer countryId;
	    private String countryName;

	    private Integer industryId;
	    private String industryName;

	    private Integer professionId;
	    private String professionName;

	    private Integer subProfessionId;
	    private String subProfessionName;

	    private Integer yearsOfExperience;
	    private String verificationCode;
	    private Boolean verified;
}
