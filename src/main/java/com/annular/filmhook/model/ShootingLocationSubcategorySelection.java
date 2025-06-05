package com.annular.filmhook.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="shooting_location_selection")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class ShootingLocationSubcategorySelection {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "subcategory_id")
	    private ShootingLocationSubcategory subcategory;
	    
	    @OneToOne(mappedBy = "subcategorySelection", cascade = CascadeType.ALL)
	    private ShootingLocationPropertyDetails propertyDetails;

	    private Boolean entireProperty;
	    private Boolean singleProperty;
	    
	    
}
