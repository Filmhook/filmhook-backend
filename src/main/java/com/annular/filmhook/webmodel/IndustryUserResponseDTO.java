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
public class IndustryUserResponseDTO {

    private String industriesName;
    private byte[] image;
    private List<PlatformDetailDTO> platformDetails;
    private Integer iupdId;
    private Integer iuIndustryId;

}
