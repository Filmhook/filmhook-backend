package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiMediaOutputWebModel {

    private Integer multiMediaFileId;
    private String fileName;
    private String fileOriginalName;
    private Integer fileDomainId;
    private Integer fileDomainReferenceId;
    private Boolean fileIsActive;
    private Integer fileCreatedBy;
    private Date fileCreatedOn;
    private Integer fileUpdatedBy;
    private Date fileUpdatedOn;
    private String fileSize;
    private String fileType;

}
