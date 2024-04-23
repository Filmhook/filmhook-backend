package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;

@Data
public class IndustryUserResponseDTO {

	private String industriesName;
    private List<PlatformDetailDTO> platformDetails;
}
