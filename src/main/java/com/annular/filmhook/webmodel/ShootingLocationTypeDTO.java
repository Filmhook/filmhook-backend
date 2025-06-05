package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationTypeDTO {
	
	    private Integer id;
	    private String name;
	    private String description;
//	    private List<ShootingLocationCategoryDTO> categories;
	}

