package com.annular.filmHook.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Gallery")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gallery {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer galleryId;

	@Column
	private boolean galleryIsActive;

	@Column
	private Integer galleryCreatedBy;

	@Column
	private Integer galleryUpdatedBy;

	@Column
	@CreationTimestamp
	private Date galleryCreatedOn;

	@Column
	@CreationTimestamp
	private Date galleryUpdatedOn;

}
