package com.annular.filmhook.webmodel;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class SellerFileInputModel {
    private List<MultipartFile> idProofImages;
    private List<MultipartFile> shopLogos;
}