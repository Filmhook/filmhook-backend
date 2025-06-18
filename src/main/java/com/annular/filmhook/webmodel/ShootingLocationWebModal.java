package com.annular.filmhook.webmodel;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShootingLocationWebModal {

    private Integer userId;
   
    private List<MultipartFile> shootingImages;
    
    private List<FileOutputWebModel> fileOutputWebModel; 
   
 

}