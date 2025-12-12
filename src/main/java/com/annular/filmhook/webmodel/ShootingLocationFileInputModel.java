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
	private List<MultipartFile> govermentId;
    private MultipartFile selfOwnedPropertyDocument;
    private MultipartFile mortgagePropertyDocument;
    private MultipartFile ownerPermittedDocument;
    private MultipartFile propertyDamageDocument;
    private MultipartFile crewAccidentDocument;
    private String updateMode;
}