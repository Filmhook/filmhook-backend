package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class BusinessInfoDTO {
    private String businessLocation;
    private String businessType;
    private String businessName;
    private String panOrGstin;

    // Embedded Address fields
    private String location;
    private String doorFlatNumber;
    private String streetCross;
    private String area;
    private String state;
    private Long postalCode;
}
