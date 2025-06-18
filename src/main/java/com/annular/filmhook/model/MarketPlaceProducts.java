package com.annular.filmhook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
	private String brandName;
	private String modelName;
	private BigDecimal price;
	private String availability;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	@CreatedBy
	private Integer createdBy;

	@LastModifiedBy
	private Integer updatedBy;
	@Lob
	@Column(columnDefinition = "TEXT")
	private String dynamicAttributesJson;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SellerMediaFile> mediaList;


	@ManyToOne(optional = false)
	@JoinColumn(name = "subcategory_id")
	private MarketPlaceSubCategories subCategory;

	//	    // ---------- SELLER ----------
	//	    @ManyToOne(optional = false)
	//	    @JoinColumn(name = "seller_id")
	//	    private Seller seller;

	@PrePersist
	public void onCreate() {
		this.createdDate = LocalDateTime.now();
		this.updatedDate = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedDate = LocalDateTime.now();
	}
}


