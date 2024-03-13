package com.annular.filmhook.webmodel;

import java.sql.Date;


import lombok.Data;

@Data
public class GalleryWebModel {

	private Integer id;
	private boolean status;
	private Integer createdBy;
	private Integer updatedBy;
	private Date createdOn;
	private Date updatedOn;
}
