package com.annular.filmhook.webmodel;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

import java.util.List;

@Data
@Builder
public class IndustryFileInputWebModel {

    private Integer userId;
    private String category;
    private List<MultipartFile> images;
    private List<MultipartFile> videos;
    private MultipartFile panCard;
    private MultipartFile adharCard;
    private List<MultipartFile> oneMinuteVideos;

}
