package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatformDetailDTO {

    private String platformName;
    private byte[] image;
    private List<ProfessionDetailDTO> professionDetails;
    private Integer platformPermanentId;
    private Integer pdPlatformId;
    private List<FileOutputWebModel> outputWebModelList;

    private Integer filmCount;

    private Integer netWorth;

    private Integer dailySalary;

    public List<SubProfessionsWebModel> SubProfession;

    private String startingYear;
    private String endingYear;

}
