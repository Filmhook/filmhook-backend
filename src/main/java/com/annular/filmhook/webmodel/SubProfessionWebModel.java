package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubProfessionWebModel {

    private Integer id;
    private String subProfessionName;
    private Boolean status;

}
