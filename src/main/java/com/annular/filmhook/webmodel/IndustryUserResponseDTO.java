package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;

@Data
public class IndustryUserResponseDTO {

	private String industriesName;
	private byte[] image;
    private List<PlatformDetailDTO> platformDetails;
    private Integer iupdId;
	private Integer iuIndustryId;

}
