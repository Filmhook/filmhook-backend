package com.annular.filmhook.webmodel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
public class MultiMediaOutputWebModel {

	private Integer multiMediaFileId;
	private String fileName;
	private String fileOriginalName;
	private Integer fileDomainId;
	private Integer fileDomainReferenceId;
	private Boolean fileIsActive;
	private Integer fileCreatedBy;
	private Date fileCreatedOn;
	private Integer fileUpdatedBy;
	private Date fileUpdatedOn;
	private String fileSize;
	private String fileType;

}
