package com.annular.filmhook.webmodel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.model.PromoteAd.PromoteStatus;
import com.annular.filmhook.model.VisitePageCategory;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class PromoteWebModel {

    private Integer promoteId;
    private PromoteStatus status;             // <-- Changed from Boolean → String
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

    private Integer numberOfDays;

    private Boolean promoteStatus;
    private Boolean promoteFlag;

    private String companyType;
    private String companyName;
    private String brandName;

    private String nation;

    private Integer multimediaId;
    private List<String> country;

    private String visitPage;
    private String webSiteLink;
    private String contactNumber;
    private Integer selectOption;
    private List<Integer> selectedMediaIds;

    // New Post Create Requirements
    private Integer userId;
    private List<MultipartFile> files;
    private String description;
    private String taggedUsers;
    private String postLinkUrl;
    private String latitude;
    private String longitude;
    private String address;
    private Boolean privateOrPublic;

    // Promote Fields
    private Integer postId;

    private String headline;
    private String promoteDescription;
    private String adType;

    private String businessLocation;
    private String businessType;

    private VisitePageCategory advObject;
    private String advObjectValue;

    private MultipartFile companyLogo;
    private MultipartFile businessAddressDoc;

    private String businessName;

    private Integer visitTypeId;

    private Double budget;
    private Integer days;

    private String targetCountries;

    private Long reachMin;
    private Long reachMax;

    private List<FileOutputWebModel> postFiles;

    // ⭐ Add this — you are trying to set this in builder
    private String postDescription;

    // Payment
    private BigDecimal amount;
    private BigDecimal totalCost;
    private Integer taxFee;
    private Integer cgst;
    private Integer sgst;
    private Integer price;

    private String transactionId;
    private String paymentStatus;
}
