package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformWebModel {

    private Integer id;
    private String platformName;
    private Boolean status;
    private byte[] image;

}
