  package com.annular.filmhook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_Place_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPlaceProducts {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String productType;//Sale, Rent, Both
	 
	private String brandName;
	private String modelName;
	private BigDecimal price;
	private String availability;
	private String additionalDetails;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	private String status;

	@CreatedBy
	private Long createdBy;

	@LastModifiedBy
	private Long updatedBy;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SellerMediaFile> mediaList;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<MarketPlaceProductDynamicAttribute> dynamicAttributes;

	@ManyToOne(optional = false)
	@JoinColumn(name = "subcategory_id")
	private MarketPlaceSubCategories subCategory;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	@JsonIgnore
	private SellerInfo seller;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MarketPlaceProductReview> reviews;
	
	  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	    private List<MarketPlaceLikes> likes;
	
	@PrePersist
	public void onCreate() {
		this.createdDate = LocalDateTime.now();
		this.updatedDate = LocalDateTime.now();
		if (this.status == null) {
			this.status = "ACTIVE"; 
		}
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedDate = LocalDateTime.now();
	}
}


