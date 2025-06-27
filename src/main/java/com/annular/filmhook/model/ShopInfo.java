package com.annular.filmhook.model;

import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class ShopInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;

    @OneToOne
    @JoinColumn(name = "shop_logo_id")
    private SellerMediaFile shopLogo;

    private Long phoneNumber;

    private String emai;

    private boolean isProductSale;

   
    @ElementCollection
    private List<String> otherWebsites;

    @ManyToOne
    @JoinColumn(name = "type_of_sale_id")
    private TypesOfSale typeOfSale;
}
