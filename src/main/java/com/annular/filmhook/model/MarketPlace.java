package com.annular.filmhook.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MarketPlace")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_place_id")
    private Integer marketPlaceId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "new_product")
    private String newProduct;

    @Column(name = "rental_sale")
    private Boolean rentalOrsale;

    @Column(name = "count")
    private Integer count;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "market_place_isactive")
    private boolean marketPlaceIsactive;

    @Column(name = "market_place_created_by")
    private Integer marketPlaceCreatedBy;

    @Column(name = "market_place_createdon")
    @CreationTimestamp
    private Date marketPlaceCreatedOn;

    @Column(name = "market_place_updated_by")
    private Integer marketPlaceUpdatedBy;

    @Column(name = "market_place_updatedon")
    @CreationTimestamp
    private Date marketPlaceUpdatedOn;

    @Column(name = "userId")
    private Integer userId;

}
