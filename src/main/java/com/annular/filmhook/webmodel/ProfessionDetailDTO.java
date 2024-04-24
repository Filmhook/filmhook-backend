package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;

@Data
public class ProfessionDetailDTO {
	
	private String professionName;
	private byte[] image;
	private List<String> subProfessionName;

}
