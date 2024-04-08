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
@Table(name = "audition_roles")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuditionRoles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audition_role_id")
	private Integer auditionRoleId;
	
	@Column(name = "audition_role_desc")
	private String auditionRoleDesc;
	
	@Column (name = "audition_reference_Id")
	private Integer auditionReferenceId;
	
	@Column(name = "audition_role_isactive")
	private boolean auditionRoleIsactive;
	
	@Column(name = "audition_role_created_by")
	private Integer auditionRoleCreatedBy;
	
	@Column(name = "audition_role_createdon")
	@CreationTimestamp
	private Date auditionRoleCreatedOn;
	
	@Column(name = "audition_role_updated_by")
	private Integer auditionRoleUpdatedBy;
	
	@Column(name = "audition_role_updatedon")
	@CreationTimestamp
	private Date auditionRoleUpdatedOn;
	
}
