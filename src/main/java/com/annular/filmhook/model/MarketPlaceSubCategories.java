package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marketplace_sub_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketPlaceSubCategories {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "subCategories", cascade = CascadeType.ALL)
    private List<MarketPlaceSubCategoryFields> CategoryFields;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private MarketPlaceCategories category;
}
