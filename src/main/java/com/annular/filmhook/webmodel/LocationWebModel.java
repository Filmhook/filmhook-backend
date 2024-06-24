package com.annular.filmhook.webmodel;

import java.util.Date;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationWebModel {
	
	private Integer locationId;

    private Integer userId;
    private String latitude;
    private String longitude;
    private String address;
    private String locationName;
    private String landMark;

    private Boolean status;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
}
