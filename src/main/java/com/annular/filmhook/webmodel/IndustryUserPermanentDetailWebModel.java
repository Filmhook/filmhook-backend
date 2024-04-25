package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.ProfessionPermanentDetail;

import lombok.Data;

@Data
public class IndustryUserPermanentDetailWebModel {
	
	private Integer iupdId;
	private String industriesName;
	private Integer iupdIndustryId;
	 private List<PlatformPermanentDetail> platformDetails;
	 private List<ProfessionPermanentDetail> professionDetails;
	 private List<String> subProfessionName;

	private String platformName;
	private String professionName;
	
	private Integer userId;
	private Boolean status;
	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updatedOn;

}
