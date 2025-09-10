package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;
import java.util.List;

import com.annular.filmhook.model.FilmSubProfession;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditionNewTeamNeedWebModel {
//    private Integer id;
    private String role;
    private Integer count;
    private String characterName;
    private String gender;
    private Integer ageFrom;
    private Integer ageTo;
    private String ethnicity;
    private Double heightMin;
    private Double heightMax;
    private String bodyType;
    private List<String> regionalDemonyms;
    private String opportunity;
    private Integer experienceYears;
    private List<String> rolesResponsibilities;
    private Double salary;
    private String salaryType;
    private String paymentMode;
    private Integer workDays;
    private List<String> facilitiesProvided;
    private Integer subProfessionId;
    private Boolean status;
    private Integer createdBy;
    private Integer updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
