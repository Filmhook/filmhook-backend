package com.annular.filmhook.webmodel;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
public class ShootingLocationWebModel {

	private Integer shootingLocationId;
	private String shootingLocationName;
	private String shootingLocationDescription;
	private String shootingTermsAndCondition;

	private Boolean indoorOrOutdoorLocation;
	private String locationUrl;
	private float cost;
	private String hourMonthDay;
	private boolean shootingLocationIsactive;
	private Integer shootingLocationCreatedBy;
	private Date shootingLocationCreatedOn;
	private Integer shootingLocationUpdatedBy;
	private Date shootingLocationUpdatedOn;
	private Integer userId;

	private FileInputWebModel fileInputWebModel; // for file input details
	private List<FileOutputWebModel> fileOutputWebModel; // for file output details

}
