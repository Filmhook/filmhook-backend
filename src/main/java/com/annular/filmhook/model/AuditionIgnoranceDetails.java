package com.annular.filmhook.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "audition_ignorance_Details")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuditionIgnoranceDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audition_ignorance_id")
	private Integer auditionIgnoranceId;
	
	@Column(name = "is_ignorance_accepted")
	private boolean isIgnoranceAccepted;
	
	@Column(name = "audition_ignorance_user")
	private Integer auditionIgnoranceUser;
	
	@Column(name = "audition_ref_id")
	private  Integer auditionRefId;//Audition Id from audition table is saved, to have this loosely coupled
	
	@Column(name = "audition_ignorance_createdby")
	private Integer auditionIgnoranceCreatedBy;
	
	@Column(name = "audition_ignorance_createdon")
	@CreationTimestamp
	private Date auditionIgnoranceCreatedOn;
	
	@Column(name = "audition_ignorance_updatedby")
	private Integer auditionIgnoranceUpdatedBy;
	
	@Column(name = "audition_ignorance_updatedon")
	@CreationTimestamp
	private Date auditionIgnoranceUpdatedOn;

}
