package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditionCompaniesWithProjectsDTO {

    private Integer companyId;
    private String companyName;
    private String ownerName;
    private String gstNumber;

    private int totalJobPosts;
    private int activePosts;

    private List<AuditionProjectSummaryDTO> projects;

}