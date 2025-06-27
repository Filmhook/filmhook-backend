package com.annular.filmhook.webmodel;

import lombok.Data;

import java.util.List;

@Data
public class ShopInfoDTO {
    private String shopName;
    private Long phoneNumber;
    private String email;
    private boolean isProductSale;
    private List<String> otherWebsites;
    private Integer typeOfSaleId; // ID from TypesOfSale table
}
