package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PromoteWebModel {

	private Integer promoteId;
	private Boolean status;
	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updatedOn;
//	private LocalDate startDate;
//	private LocalDate endDate;
	private Integer numberOfDays;
	private Integer amount;
	private Integer totalCost;
	private Integer taxFee;
	private Integer cgst;
	private Integer sgst;
	private Integer price;
    private Integer userId;
	private Integer multimediaId;
	private List<String> country;

}
