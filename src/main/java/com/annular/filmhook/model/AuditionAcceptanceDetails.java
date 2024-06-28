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
@Table(name = "audition_acceptance_Details")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuditionAcceptanceDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audition_acceptance_id")
	private Integer auditionAcceptanceId;
	
	@Column(name = "is_audition_accepted")
	private boolean isAuditionAccepted;
	
	@Column(name = "audition_acceptance_user")
	private Integer auditionAcceptanceUser;
	
	@Column(name = "audition_ref_id")
	private  Integer auditionRefId; // Audition Id from audition table is saved, to have this loosely coupled
	
	@Column(name = "audition_acceptance_createdby")
	private Integer auditionAcceptanceCreatedBy;
	
	@Column(name = "audition_acceptance_createdon")
	@CreationTimestamp
	private Date auditionAcceptanceCreatedOn;
	
	@Column(name = "audition_acceptance_updatedby")
	private Integer auditionAcceptanceUpdatedBy;
	
	@Column(name = "audition_acceptance_updatedon")
	@CreationTimestamp
	private Date auditionAcceptanceUpdatedOn;
	
}
