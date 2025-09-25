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

import com.fasterxml.jackson.annotation.JsonBackReference;

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


    @Column(name = "audition_role_desc", length = 1000)
    private String auditionRoleDesc;

    //	@Column (name = "audition_reference_Id")	
	//	private Integer auditionReferenceId;

	@JsonBackReference
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
    
    // New fields 
    @Column(name = "character_name")
    private String characterName;

    @Column(name = "age_range")
    private String ageRange;

    @Column(name = "ethnicity")
    private String ethnicity;

    @Column(name = "height_range")
    private String heightRange;

    @Column(name = "weight")
    private String weight;

    @Column(name = "profile_face")
    private String profileFace;

    @Column(name = "opportunity")
    private String opportunity;

    @Column(name = "experience")
    private String experience;

    @Column(name = "pay")
    private String pay;

    @Column(name = "days_of_shoot")
    private Integer daysOfShoot;

    @Column(name = "date_of_shoot")
    private String dateOfShoot;

    @Column(name = "compensation", length = 500)
    private String compensation;

}
