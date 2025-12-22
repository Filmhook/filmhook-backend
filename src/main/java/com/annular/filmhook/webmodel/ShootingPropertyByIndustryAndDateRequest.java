package com.annular.filmhook.webmodel;

import java.time.LocalDate;

import com.annular.filmhook.model.PropertyBookingType;
import com.annular.filmhook.model.SlotType;

import lombok.Data;

@Data
public class ShootingPropertyByIndustryAndDateRequest {

	
	  private Integer industryId;
	    private Integer userId;
	    private LocalDate startDate;   
	    private LocalDate endDate;
	    private SlotType slotType;
	    private PropertyBookingType propertyType;
}
