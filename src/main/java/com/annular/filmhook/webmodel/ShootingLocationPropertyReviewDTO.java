package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationPropertyReviewDTO {
	private Integer propertyId;
    private Integer userId;
    private int rating;
    private String reviewText;
    private String userName;
    private LocalDateTime createdOn;
    private Integer id;
    private List<FileOutputWebModel> files; 
    private List<MultipartFile> file; 
}
