package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FilmWebModel {

	private Integer filmProfesssionId;

	private String professionName;

	private List<String> subProfessionName;

	private Boolean status;

	private Integer createdBy;

	private Date createdOn;

	private Integer updatedBy;

	private List<String> subProfessionsName;
	
	
}
