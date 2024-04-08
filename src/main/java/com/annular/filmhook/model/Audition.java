package com.annular.filmhook.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Audition")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Audition {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audition_id")
	private Integer auditionId;
	
	@Column(name = "audition_title")
	private String auditionTitle;
	
	@Column(name = "audition_experience")
	private String auditionExperience;
	
	@Column(name = "audition_category")
	private Integer auditionCategory;
	
	@Column(name = "audition_expireOn")
	private Date auditionExpireOn; 
	
	@Column(name = "audition_posted_by")
	private Integer auditionPostedBy;
	
	@Column(name = "audition_isactive")
	private boolean auditionIsactive;
	
	@Column(name = "audition_created_by")
	private Integer auditionCreatedBy;
	
	@Column(name = "audition_createdon")
	@CreationTimestamp
	private Date auditionCreatedOn;
	
	@Column(name = "audition_updated_by")
	private Integer auditionUpdatedBy;
	
	@Column(name = "audition_updatedon")
	@CreationTimestamp
	private Date auditionUpdatedOn;
	
	

}
