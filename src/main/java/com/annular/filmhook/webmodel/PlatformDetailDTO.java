package com.annular.filmhook.webmodel;

import java.util.List;

import javax.persistence.Column;

import lombok.Data;

@Data
public class PlatformDetailDTO {

	private String platformName;
	private byte[] image;
	private List<ProfessionDetailDTO> professionDetails;
	private Integer platformPermanentId;
	private Integer pdPlatformId;

	private Integer filmCount;

	private Integer netWorth;

	private Integer dailySalary;
}
