package com.annular.filmhook.model;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class BusinessAddress {
    private String location;
    private String doorFlatNumber;
    private String streetCross;
    private String area;
    private String state;
    private Long postalCode;
}