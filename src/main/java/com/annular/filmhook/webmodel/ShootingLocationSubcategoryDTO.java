package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationSubcategoryDTO {
	private Integer id;
    private String name;
    private String description;
//    private List<ShootingLocationSubcategorySelectionDTO> subcategorySelections;
}
