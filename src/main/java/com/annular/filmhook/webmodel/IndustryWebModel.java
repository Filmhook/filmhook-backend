package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndustryWebModel {

    private Integer id;
    private String industryName;
    private String stateCode;
    private Boolean status;
    private Integer countryId;
    private byte[] image;

}
