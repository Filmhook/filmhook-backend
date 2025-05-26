package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import com.annular.filmhook.model.FilmProfessionPermanentDetail;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndustryUserPermanentDetailWebModel {

    private Integer iupdId;
    private String industriesName;
    private Integer iupdIndustryId;
    private List<PlatformDetailsWebModel> platformDetails;
    private List<FilmProfessionPermanentDetail> professionDetails;
    private List<String> subProfessionName;

    private String platformName;
    private String professionName;

    private Integer userId;
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

}
