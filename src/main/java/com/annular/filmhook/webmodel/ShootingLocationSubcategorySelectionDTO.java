package com.annular.filmhook.webmodel;


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
	    private Integer entirePropertyDayDiscountPercent;
	    private Integer entirePropertyNightDiscountPercent;
	    private Integer entirePropertyFullDayDiscountPercent;

	    private Integer singlePropertyDayDiscountPercent;
	    private Integer singlePropertyNightDiscountPercent;
	    private Integer singlePropertyFullDayDiscountPercent;

}
