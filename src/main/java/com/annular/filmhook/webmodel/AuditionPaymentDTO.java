package com.annular.filmhook.webmodel;

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
	    private Double baseAmount;
	    private Integer gstPercentage;
	    private Double discountPercentage;
	    private Double finalRatePerPost;      
	    private Double discountedAmount; 
	    private Double totalAmount;
	    
	    private Map<String, Integer> roleBreakdown;
}
