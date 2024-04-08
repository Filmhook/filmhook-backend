package com.annular.filmhook.model;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

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

	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "category")
	private String category;

	@Column(name = "file_id")
	private String fileId;

	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "file_size")
	private Long fileSize;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "file_path")
	private String filePath;

	@Column(name = "expiry_time")
	private String expiryTime;

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

