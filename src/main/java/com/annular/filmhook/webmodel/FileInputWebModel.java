package com.annular.filmhook.webmodel;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileInputWebModel {

	// For save purpose
	private Integer userId;
	private String category;
	private MultipartFile file;

	// For read purpose
	private String fileId;
	private String fileType;
	private String filePath;

}
