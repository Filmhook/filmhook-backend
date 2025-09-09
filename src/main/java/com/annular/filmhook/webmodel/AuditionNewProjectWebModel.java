package com.annular.filmhook.webmodel;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditionNewProjectWebModel {
//    private Integer id;
    private String productionCompanyName;
    private String projectTitle;
    private String country;
    private List<String> industries;
    private String dubbedCountry;   
    private List<String> dubbedIndustries;
    private List<String> platforms;
    private List<String> movieTypes;
    private List<String> themeMovieTypes;
    private List<String> shootLocations;
    private LocalDate shootStartDate;
    private LocalDate shootEndDate;
    private String projectDescription;
    private String auditionProfilePicture;
    private Integer companyId; 
    private List<AuditionNewTeamNeedWebModel> teamNeeds;
    private List<MultipartFile> profilePictureFiles;
    private List<FileOutputWebModel> profilePictureFilesOutput;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<FileOutputWebModel> logoFiles;

}
