package com.annular.filmhook.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.ShootingLocationImages;
import com.annular.filmhook.repository.ShootingLocationImagesRepository;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FileOutputWebModel;
@Service
public class ShootingLocationImageService {
	@Autowired
    private ShootingLocationImagesRepository shootingLocationImagesRepository;

    @Autowired
    private S3Util s3Util;

    public FileOutputWebModel getShootingLocationImageById(Integer shootingMediaId) {
        Optional<ShootingLocationImages> optionalImage = shootingLocationImagesRepository.findByIndustryMediaid(shootingMediaId);

        if (optionalImage.isPresent()) {
            ShootingLocationImages image = optionalImage.get();
            return FileOutputWebModel.builder()
                    .id(image.getIndustryMediaid())
                    .userId(image.getUser() != null ? image.getUser().getUserId() : null)
                    .category(image.getCategory())
                    .fileId(image.getFileId())
                    .fileName(image.getFileName())
                    .fileSize(image.getFileSize())
                    .fileType(image.getFileType())
                    .filePath(s3Util.getS3BaseURL() + "/" + image.getFilePath() + image.getFileType())
                    .createdBy(image.getCreatedBy())
                    .createdOn(image.getCreatedOn())
                    .updatedBy(image.getUpdatedBy())
                    .updatedOn(image.getUpdatedOn())
                    .build();
        } else {
            return null;
        }
    }
}
