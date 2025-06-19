package com.annular.filmhook.webmodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPlaceProductDTO {
	private Integer id;
	 private Integer subCategoryId;
	    private String brandName;
	    private String modelName;
	    private BigDecimal price;
	    private String availability;
	    private Integer createdBy;
	    private Integer updatedBy; 
	    private Long sellerId;
	    private Map<String, Object> dynamicAttributesJson;
	    private List<String> imageUrls;
	    private List<String> videoUrls; 
	    private String sellerFullName;
	    private String sellerEmail;
	    private String subCategoryName;
}
