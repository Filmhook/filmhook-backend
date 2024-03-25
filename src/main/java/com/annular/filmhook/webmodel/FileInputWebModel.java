package com.annular.filmhook.webmodel;

import lombok.Data;

import java.util.List;

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

	// For save purpose industry User
	private List<MultipartFile> images;
	private List<MultipartFile> videos;
	private MultipartFile panCard;
	private MultipartFile aadhaarCard;
}
