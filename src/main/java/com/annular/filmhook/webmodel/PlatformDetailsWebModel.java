package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlatformDetailsWebModel {

    private String platformName;
    private List<ProfessionDetailDTO> professionDetails;

}
