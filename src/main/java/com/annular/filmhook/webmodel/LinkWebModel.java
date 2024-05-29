package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Data;

@Data
public class LinkWebModel {
	

    private Integer linkId;
	private String links;
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer userId;

}
