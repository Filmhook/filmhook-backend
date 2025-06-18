package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.SellerMediaFile;
import lombok.*;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerMediaFileDTO {

    private Integer mediaId;
    private String category;
    private String fileId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String filePath;
    private String expiryTime;
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer notificationCount;
    private Boolean unverifiedList;

    public static SellerMediaFileDTO from(SellerMediaFile file) {
        if (file == null) return null;

        return SellerMediaFileDTO.builder()
                .mediaId(file.getMediaId())
                .category(file.getCategory())
                .fileId(file.getFileId())
                .fileName(file.getFileName())
                .fileSize(file.getFileSize())
                .fileType(file.getFileType())
                .filePath(file.getFilePath())
                .expiryTime(file.getExpiryTime())
                .status(file.getStatus())
                .createdBy(file.getCreatedBy())
                .createdOn(file.getCreatedOn())
                .updatedBy(file.getUpdatedBy())
                .updatedOn(file.getUpdatedOn())
                .notificationCount(file.getNotificationCount())
                .unverifiedList(file.getUnverifiedList())
                .build();
    }
}
