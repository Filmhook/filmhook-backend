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

	private Date auditionExpireOn; 

	private Integer auditionPostedBy;

//	private List<AuditionRolesWebModel> auditionRoles;
	
	private String auditionRoles[];

	private FileInputWebModel fileInputWebModel; // for file input details
	private List<FileOutputWebModel> fileOutputWebModel; // for file output details

}
