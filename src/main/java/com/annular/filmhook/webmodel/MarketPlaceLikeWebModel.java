package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MarketPlaceLikeWebModel {
	

    private Integer marketPlaceLikeId;
    private Integer marketPlaceId; // primary key for post table
    private Integer marketPlacelikedBy; // primary key for user table
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;



}
