package com.annular.filmhook.webmodel;

import java.util.Date;



import lombok.Data;

@Data
public class LocationWebModel {
	
	private Integer locationId;
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer userId;
    private String locationLatitude;
    private String locationLongitude;
    private String locationAddress;
    private String locationName;
    private String locationLandMark;

}
