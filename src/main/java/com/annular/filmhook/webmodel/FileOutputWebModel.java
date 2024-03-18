package com.annular.filmhook.webmodel;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.Date;

@Data
public class FileOutputWebModel {

	private Integer userId;
	private String category;

	private String fileId;
	private String fileName;
	private long fileSize;
	private String fileType;
	private String filePath;

	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updatedOn;

}
