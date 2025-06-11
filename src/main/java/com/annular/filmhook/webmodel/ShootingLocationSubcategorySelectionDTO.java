package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.ShootingLocationSubcategory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationSubcategorySelectionDTO {
	 private Long subcategoryId;
	    private Boolean entireProperty;
	    private Boolean singleProperty;
}
	