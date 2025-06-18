package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketPlaceSubCategoryDTO {
	private Integer id;
	private String name;
	 private Integer categoryId;
}
