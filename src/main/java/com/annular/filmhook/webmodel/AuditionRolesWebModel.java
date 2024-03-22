package com.annular.filmhook.webmodel;

import java.sql.Date;

import lombok.Data;

@Data
public class AuditionRolesWebModel {

	private Integer auditionRoleId;
	
	private String auditionRoleDesc;
	
	private Integer auditionReferenceId;

	private boolean auditionRoleIsactive;
	
	private Integer auditionRoleCreatedBy;
	
	private Date auditionRoleCreatedOn;

	private Integer auditionRoleUpdatedBy;
	
	private Date auditionRoleUpdatedOn;
	
}
