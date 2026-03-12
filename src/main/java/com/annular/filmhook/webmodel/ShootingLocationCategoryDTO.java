package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationCategoryDTO {
	   private Integer id;
	    private String name;
	    private String image;
//	    private List <ShootingLocationSubcategoryDTO> subcategories;
}
