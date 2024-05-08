package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryWebModel {

    private Integer id;
    private String code;
    private String name;
    private String description;
    private byte[] logo;

}
