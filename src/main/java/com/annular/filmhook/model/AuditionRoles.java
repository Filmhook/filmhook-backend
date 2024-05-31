package com.annular.filmhook.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	
	//@Column(name = "audition_role_desc")
	@Column(name = "audition_role_desc", length = 1000)
	private String auditionRoleDesc;
	
//	@Column (name = "audition_reference_Id")
//	private Integer auditionReferenceId;
	
	@ManyToOne
    @JoinColumn(name = "audition_id", nullable = false)
    private Audition audition;
	
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
