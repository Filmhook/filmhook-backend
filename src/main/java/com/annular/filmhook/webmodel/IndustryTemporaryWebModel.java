package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndustryTemporaryWebModel {

    private Integer itId;
    private List<String> industriesName;
    private List<String> platformName;
    private List<String> professionName;
    private List<String> subProfessionName;
    private Integer userId;

}
