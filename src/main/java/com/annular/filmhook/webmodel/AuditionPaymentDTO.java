package com.annular.filmhook.webmodel;

import java.math.BigDecimal;
import java.util.Map;

import com.annular.filmhook.model.ServiceType;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuditionPaymentDTO {
	 private Integer projectId;
	    private Integer selectedDays;
	    private Integer totalTeamNeed;
	    private double amountPerPost;
	    private BigDecimal baseAmount;
	    private Integer gstPercentage;
	    private Double discountPercentage;
	    private BigDecimal finalRatePerPost;      
	    private BigDecimal discountedAmount; 
	    private BigDecimal totalAmount;
	    
	    private Map<String, Integer> roleBreakdown;
}
