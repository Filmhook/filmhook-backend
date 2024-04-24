package com.annular.filmhook.webmodel;

import lombok.Data;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Data
public class FileInputWebModel {

	// For save purpose
	private Integer userId;
	private String category;
	private Integer categoryRefId;
	private MultipartFile file;
	private MultipartFile[] galleryImage;
	private MultipartFile[] galleryVideos;
	

	// For read purpose
	private String fileId;
	private String fileType;
	private String filePath;

}
