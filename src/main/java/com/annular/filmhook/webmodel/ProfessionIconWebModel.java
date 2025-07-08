package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfessionIconWebModel {
    private Integer professionId;
    private String professionName;
    private String iconFilePath;
}
