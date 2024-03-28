package com.annular.filmhook.webmodel;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class IndustryFileInputWebModel {
	
	private Integer userId;
	private String category;
	private MultipartFile[] images;
	private MultipartFile[] videos;
	private MultipartFile panCard;
	private MultipartFile adharCard;

}
