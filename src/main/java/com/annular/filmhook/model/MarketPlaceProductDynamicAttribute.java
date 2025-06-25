package com.annular.filmhook.model;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "market_place_product_dynamic_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPlaceProductDynamicAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldKey;
    private String value;
    private String section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private MarketPlaceProducts product;
}
