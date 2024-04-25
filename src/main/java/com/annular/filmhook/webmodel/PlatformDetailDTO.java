package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;

@Data
public class PlatformDetailDTO {

	private String platformName;
	private byte[] image;
    private List<ProfessionDetailDTO> professionDetails;
    private Integer platformPermanentId;
    private Integer pdPlatformId;

}
