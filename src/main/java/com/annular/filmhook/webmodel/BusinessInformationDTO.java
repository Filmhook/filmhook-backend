package com.annular.filmhook.webmodel;

import javax.persistence.OneToOne;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessInformationDTO {
	private Long id;
	    private String businessLocation;
	    private String businessType;
	    private String businessName;
	    private String panOrGSTNumber;
	    private String location;
	    private String addressLine1;
	    private String addressLine2;
	    private String addressLine3;
	    private String state;
	    private String postalCode;
//	    @OneToOne(mappedBy = "businessInformation")
//	    private ShootingLocationPropertyDetailsDTO propertyDetails;
}
