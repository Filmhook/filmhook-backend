package com.annular.filmhook.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MediaFiles")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MediaFiles {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_original_name")
	private String fileOriginalName;

	@Column(name = "file_domain_id")
	private Integer fileDomainId;

	@Column(name = "file_domain_reference_id")
	private Integer fileDomainReferenceId;
	
	@Column(name = "file_size")
	private String fileSize;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "updated_on")
	@CreationTimestamp
	private Date updatedOn;

}

