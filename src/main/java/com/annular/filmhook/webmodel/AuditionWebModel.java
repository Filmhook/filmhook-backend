package com.annular.filmhook.webmodel;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class AuditionWebModel {

	private Integer auditionId;

	private String auditionTitle;

	private String auditionExperience;

	private Integer auditionCategory;
	
	private String auditionAddress;
	
	private String auditionMessage;

	private Date auditionExpireOn; 

	private String auditionPostedBy;
	
	private Integer auditionAttendedCount;
	
	private Integer auditionIgnoredCount;
	
	private Integer auditionCreatedBy;
	
	private String auditionLocation;
	
	private Boolean flag;
	
	private String searchKey;

//	private List<AuditionRolesWebModel> auditionRoles;
	
	private String auditionRoles[];

	private List<AuditionRolesWebModel> auditionRolesWebModels;
	
	private FileInputWebModel fileInputWebModel; // for file input details
	private List<FileOutputWebModel> fileOutputWebModel; // for file output details

}
