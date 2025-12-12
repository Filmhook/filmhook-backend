package com.annular.filmhook.webmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ShootingPropertyByIndustryAndDateRequest {

	
	  private Integer industryId;
	    private Integer userId;
	    private LocalDate startDate;   
	    private LocalDate endDate;
}
