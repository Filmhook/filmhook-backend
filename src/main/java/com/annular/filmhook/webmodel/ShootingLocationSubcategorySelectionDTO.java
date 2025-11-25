package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.ShootingLocationSubcategory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationSubcategorySelectionDTO {
	private Integer id;
	 private Integer subcategoryId;
	    private Boolean entireProperty;
	    private Boolean singleProperty;
	    private Double entireDayPropertyPrice;
	    private Double entireNightPropertyPrice;
	    private Double entireFullDayPropertyPrice;
	    private Double singleDayPropertyPrice;
	    private Double singleNightPropertyPrice;
	    private Double singleFullDayPropertyPrice;
		private boolean entirePropertyDiscount20Percent;
		private boolean singlePropertyDiscount20Percent;
}
	