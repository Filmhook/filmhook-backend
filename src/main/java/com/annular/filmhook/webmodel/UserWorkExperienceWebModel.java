package com.annular.filmhook.webmodel;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWorkExperienceWebModel {

    private Integer id;
    private String companyName;
    private String designation;
    private String companyLocation;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;
    private Integer userId;  // Only userId exposed to client
}

