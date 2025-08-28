package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoteWebModel {

    private Integer promoteId;
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer postId;
    //	private LocalDate startDate;
	//	private LocalDate endDate;
    private Integer numberOfDays;
    private Integer amount;
    private Integer totalCost;
    private Integer taxFee;
    private Integer cgst;
    private Boolean promoteStatus;
    private Boolean promoteFlag;
 
    private String companyType; 
    private String companyName;
    private String brandName;

    private Integer sgst;
    private Integer price;
    private Integer userId;
    private Integer multimediaId;
    private List<String> country;
    List<MultipartFile> files;
    private String visitPage;    
    private String webSiteLink;
    private String whatsAppNumber;
    private Integer selectOption;

}
