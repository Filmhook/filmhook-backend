package com.annular.filmhook.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class ShopInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;

    // Updated to SellerMediaFile instead of Image
    @OneToOne
    @JoinColumn(name = "shop_logo_id")
    private SellerMediaFile shopLogo;

    private Long phoneNumber;

    private String emai;

    private boolean isProductSale;
}
