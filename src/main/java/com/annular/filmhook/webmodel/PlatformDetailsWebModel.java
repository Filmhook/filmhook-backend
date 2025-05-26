package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatformDetailsWebModel {

    private String platformName;
    private List<ProfessionDetailDTO> professionDetails;

}
