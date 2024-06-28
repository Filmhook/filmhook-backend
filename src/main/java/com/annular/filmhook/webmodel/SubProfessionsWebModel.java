package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubProfessionsWebModel {

    private Integer subProfessionId;
    private Integer startingYear;
    private Integer endingYear;

}
