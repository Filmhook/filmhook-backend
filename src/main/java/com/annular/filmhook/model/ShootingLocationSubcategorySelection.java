package com.annular.filmhook.model;

import java.time.LocalDateTime;

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

@Builder
@Getter
@Setter
public class ShootingLocationSubcategorySelection {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "subcategory_id")
	    private ShootingLocationSubcategory subcategory;
	    
	    @OneToOne(mappedBy = "subcategorySelection", cascade = CascadeType.ALL)
	    private ShootingLocationPropertyDetails propertyDetails;

	    private Boolean entireProperty;
	    private Boolean singleProperty;
	    private Double entireDayPropertyPrice;
	    private Double entireNightPropertyPrice;
	    private Double entireFullDayPropertyPrice;
	    private Double singleDayPropertyPrice;
	    private Double singleNightPropertyPrice;
	    private Double singleFullDayPropertyPrice;
		private boolean entirePropertyDiscount20Percent;
		private boolean singlePropertyDiscount20Percent;
	    private LocalDateTime entirePropertyDiscountStartDate;
	    private LocalDateTime singlePropertyDiscountStartDate;
	    
	    private Integer entirePropertyDiscountBookingCount = 0;
	    private Integer singlePropertyDiscountBookingCount = 0;
	    
}
