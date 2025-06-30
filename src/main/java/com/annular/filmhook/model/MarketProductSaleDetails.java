package com.annular.filmhook.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_product_sale_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketProductSaleDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal salePrice;
    private String saleAvailability;
    private String saleWarranty;

    @OneToOne
    @JoinColumn(name = "product_id")
    private MarketPlaceProducts product;
}