package com.annular.filmhook.webmodel;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditionWebModel {

    private Integer auditionId;

    private String auditionTitle;

    private String auditionExperience;

    private Integer auditionCategory;

    private String auditionAddress;

    private String auditionMessage;

    private Date auditionExpireOn;

    private String auditionPostedBy;

    private Integer auditionAttendedCount;

    private Integer auditionIgnoredCount;

    private Integer auditionCreatedBy;

    private String auditionLocation;

    private Boolean flag;

    private String searchKey;

    private Integer userId;
    
    private String filmHookCode;
    
    private String name;

//	private List<AuditionRolesWebModel> auditionRoles;

    private String auditionRoles[];

    private List<AuditionRolesWebModel> auditionRolesWebModels;

    private FileInputWebModel fileInputWebModel; // for file input details
    private List<FileOutputWebModel> fileOutputWebModel; // for file output details

    private List<Integer> mediaFilesIds; // FOr Delete purpose
	private String startDate;
	private String endDate;
	private String url;
	private Boolean termsAndCondition;

}
