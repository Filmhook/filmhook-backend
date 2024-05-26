package com.annular.filmhook.webmodel;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
public class AuditionDetailsWebModel {
	
	private Integer auditionDetailsId;
	private String auditionDetailsName;// Audition Id from audition table is saved, to have this loosely coupled
	private Integer auditionDetailsCreatedBy;
	private Date auditionDetailsCreatedOn;
	private Integer auditionDetailsUpdatedBy;
	private Date auditionDetailsUpdatedOn;


}
