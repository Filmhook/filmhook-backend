package com.annular.filmhook.webmodel;

import java.sql.Date;

import lombok.Data;

@Data
public class AddressListWebModel {
	

	private Integer addressListId;
	private String address;
	private boolean addressIsactive;
	private Integer addressCreatedBy;
	private Date addresssCreatedOn;
	private Integer addressUpdatedBy;
	private Date address_Updated_On;

}
