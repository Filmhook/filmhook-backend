package com.annular.filmhook.webmodel;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.Date;

@Data
public class FileOutputWebModel {

	private Integer id; // MySQL Primary key field

	private String fileId;
	private String fileName;
	private long fileSize;
	private String fileType;
	private String filePath;

	private Integer userId;
	private String category;
	private Integer categoryRefId;

	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updatedOn;

}
