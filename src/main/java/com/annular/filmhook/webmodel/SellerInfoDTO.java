package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerInfoDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private String citizenship;
    private String placeOfBirth;
    private Long idProofNumber;
    private String doorFlatNumber;
    private String streetCross;
    private String area;
    private String state;
    private Long postalCode;
    
    private SellerMediaFileDTO idProofImage;
    private SellerMediaFileDTO shopLogo;

    private BusinessInfoDTO businessInfo;
    private ShopInfoDTO shopInfo;
    private GstVerificationDTO gstVerification;

    private Integer userId; // for created_by
    private boolean buttonStatus;
    private String activeStatus;
}
