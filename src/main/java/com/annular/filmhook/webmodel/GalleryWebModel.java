package com.annular.filmHook.webModel;

import java.sql.Date;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalleryWebModel {
	
	
	private Integer galleryId;


	private boolean galleryIsActive;


	private Integer galleryCreatedBy;

	private Integer galleryUpdatedBy;


	private Date galleryCreatedOn;


	private Date galleryUpdatedOn;

}
