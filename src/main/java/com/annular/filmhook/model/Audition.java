package com.annular.filmhook.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	
	@Column(name = "audition_address")
	private String auditionAddress;
	
	@Column(name = "audition_message")
	private String auditionMessage;
	
	@Column(name = "audition_expireOn")
	private Date auditionExpireOn; 
	
	@Column(name = "audition_posted_by")
	private String auditionPostedBy;
	
	@OneToMany(mappedBy = "audition")
    private List<AuditionRoles> auditionRoles;
	
	@Column(name = "audition_isactive")
	private boolean auditionIsactive;
	
	@Column(name = "auditionLocation")
	private String auditionLocation;
	
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

	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "startDate")
	private String startDate;
	
	@Column(name = "endDate")
	private String endDate;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "termsAndCondition")
	private Boolean termsAndCondition;

}
