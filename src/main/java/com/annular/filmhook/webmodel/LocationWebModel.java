package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.Set;

import com.annular.filmhook.model.LocationVisibility;

import lombok.Builder;
import lombok.Data;

@Data

public class LocationWebModel {

    private Integer locationId;

    private Integer userId;
    private Double latitude;
    private Double longitude;
    private String address;
    private String locationName;
    private String landMark;
    
    private LocationVisibility visibility;
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

    // ===== Nearby Extra Fields =====
    private String userName;
    private Double distance;
    private String distanceUnit;
    private String profilePic;
    private Set<String> professionNames;
    private String userType;
    private Float review;
    
    public LocationWebModel(Integer userId,
            String userName,
            Double latitude,
            Double longitude,
            Double distance,
            String userType) {
this.userId = userId;
this.userName = userName;
this.latitude = latitude;
this.longitude = longitude;
this.distance = distance;
this.userType = userType;
}
}
