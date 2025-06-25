package com.annular.filmhook.webmodel;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationFileInputModel {


	private List<MultipartFile> images;
	private List<MultipartFile> videos;
	//    private MultipartFile panCard;
	//    private MultipartFile adharCard;
	//	private Integer propertyID;
	//  private String category;

	private List<MultipartFile> govermentId;
	   private String updateMode;
}