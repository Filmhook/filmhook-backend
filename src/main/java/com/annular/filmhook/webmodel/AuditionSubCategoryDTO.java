package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuditionSubCategoryDTO {
	 private Integer subId;
	    private String subName;
	    private int auditionCount;
}
