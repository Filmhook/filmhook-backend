package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

import com.annular.filmhook.model.MediaFileCategory;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class PostWebModel {

    private Integer userId;
    private String userName;

    private Integer postId;
    private MediaFileCategory category;
    private String description;
    List<MultipartFile> files;

    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Boolean promoteFlag;

    List<FileOutputWebModel> postFiles;
    Set<String> professionNames;

}
