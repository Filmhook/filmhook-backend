package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfessionWebModel {

    private Integer id;
    private String professionName;
    private Boolean status;
    private byte[] image;

}
