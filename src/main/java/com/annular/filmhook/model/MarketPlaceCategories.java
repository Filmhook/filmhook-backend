package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "marketplace_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPlaceCategories {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    private String name;

	    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	    private List<MarketPlaceSubCategories> subCategories;
}
