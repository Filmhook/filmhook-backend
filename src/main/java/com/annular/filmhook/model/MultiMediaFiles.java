package com.annular.filmhook.model;

import java.util.Date;

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
@Table(name = "Multi_Media_Files")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MultiMediaFiles {	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "multi_media_file_id")
	private Integer multiMediaFileId;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_original_name")
	private String fileOriginalName;

	@Column(name = "file_domain_id")
	private Integer fileDomainId;

	@Column(name = "file_domain_reference_id")
	private Integer fileDomainReferenceId;

	@Column(name = "file_is_active")
	private Boolean fileIsActive;

	@Column(name = "file_created_by")
	private Integer fileCreatedBy;

	@Column(name = "file_created_on")
	@CreationTimestamp
	private Date fileCreatedOn;

	@Column(name = "file_updated_by")
	private Integer fileUpdatedBy;

	@Column(name = "file_updated_on")
	@CreationTimestamp
	private Date fileUpdatedOn;

	@Column(name = "file_size")
	private Long fileSize;

	@Column(name = "file_type")
	private String fileType;
}
