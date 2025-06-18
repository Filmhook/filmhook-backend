package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class ShopInfoDTO {
    private String shopName;
    private Long phoneNumber;
    private String email;
    private boolean isProductSale;
}
