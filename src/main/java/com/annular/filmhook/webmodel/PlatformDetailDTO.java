package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;

@Data
public class PlatformDetailDTO {

	private String platformName;
    private List<ProfessionDetailDTO> professionDetails;
}
