 package com.annular.filmhook.model;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "audition_Details")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuditionDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audition_details_id")
	private Integer auditionDetailsId;

	@Column(name = "audition_details_Name")
	private String auditionDetailsName; // Audition Id from audition table is saved, to have this loosely coupled

	@Column(name = "audition_details_createdby")
	private Integer auditionDetailsCreatedBy;

	@Column(name = "audition_details_createdon")
	@CreationTimestamp
	private Date auditionDetailsCreatedOn;

	@Column(name = "audition_details_updatedby")
	private Integer auditionDetailsUpdatedBy;

	@Column(name = "audition_details_updatedon")
	@CreationTimestamp
	private Date auditionDetailsUpdatedOn;
	
	@OneToMany(mappedBy = "auditionDetails", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<AuditionSubDetails> subDetailsList;


}
