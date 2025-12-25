package com.annular.filmhook.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.annular.filmhook.util.LocalDateListConverter;
import com.annular.filmhook.util.StringListConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="shooting_location_property_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class ShootingLocationPropertyDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// 1. Property Information
	private String fullName;
		
	private String citizenship;
	
	private String placeOfBirth;
	private String propertyName;
	private String location;
	private LocalDate dateOfBirth;
	private String proofOfIdentity;
	private String countryOfIssued;

	// 2. Listing Summary
	private String numberOfPeopleAllowed;
	private double totalArea;
	private String selectedUnit;
	private int numberOfRooms;
	private String numberOfFloor;
	private String ceilingHeight;

	@Convert(converter = StringListConverter.class)
	private List<String> outdoorFeatures;
	@Convert(converter = StringListConverter.class)
	private List<String> architecturalStyle;
	@Convert(converter = StringListConverter.class)
	private List<String> vintage;
	@Convert(converter = StringListConverter.class)
	private List<String> industrial;
	@Convert(converter = StringListConverter.class)
	private List<String> traditional;

	// 3. Facilities & Amenities
	private String powerSupply;
	private String bakupGenerators;
	
	@Convert(converter = StringListConverter.class)
	private List<String> voltageCapacity;
	
	private String wifi;
	private String airConditionAndHeating;
	private int numberOfWashrooms;
	@Convert(converter = StringListConverter.class)
	private List<String> waterSupply;
	@Convert(converter = StringListConverter.class)
	private List<String> changingRooms;
	@Convert(converter = StringListConverter.class)
	private List<String> kitchen;

	@Convert(converter = StringListConverter.class)
	private List<String> neutralLightingConditions;
	@Convert(converter = StringListConverter.class)
	private List<String> artificialLightingAvailability;
	@Convert(converter = StringListConverter.class)
	private List<String> parkingCapacity;

	// 4. Filming Requirements & Restrictions
	private String droneUsage;
	private String firearms;
	private String actionScenes;
	@Convert(converter = StringListConverter.class)
	private List<String> structuralModification;
	private String temporary;
	private String dressing;

	private String description;
	private boolean businessOwner;
    private Integer createdBy;
    @CreationTimestamp
     private LocalDateTime createdOn;

    private Integer updatedBy;

    @CreationTimestamp
    private LocalDateTime updatedOn;
    
	private Boolean status;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bank_details_id")
	private ShootingLocationOwnerBankDetails bankDetails;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "business_information_id")
	private ShootingLocationBusinessInformation businessInformation;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "subcategory_selection_id")
	private ShootingLocationSubcategorySelection subcategorySelection;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private ShootingLocationCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_category_id", nullable = true)
	private ShootingLocationSubcategory subCategory;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="types_id")
	private ShootingLocationTypes types;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
	
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PropertyLike> likes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private Industry industry;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShootingLocationPropertyReview> reviews;
 
    private String typeLocation;
    private String locationLink;
    
    @Convert(converter = StringListConverter.class)
    private List<String> hygienStatus;
    
    @Convert(converter = StringListConverter.class)
    private List<String> genderSpecific;
    
    private String idNumber;
    private String ownerPermission;
    private String selfOwnedPropertyDocument;
    private String mortgagePropertyDocument; 
    private String ownerPermittedDocument; 
    private String propertyDamageDocument; 
    private String crewAccidentDocument; 
    
    private String localAuthorities;
    private String governmentPermission;
    @Column(columnDefinition = "TEXT")
    private String publicPermission;
    private Double additionalChargesForOverTime;
	private String propertyDamageDescription;
	private String crewAccidentLiabilityDescription;
	@Convert(converter = StringListConverter.class)
	private List<String> insuranceRequired;
	
	private LocalDate availabilityStartDate;
	private LocalDate availabilityEndDate;

	@Convert(converter = LocalDateListConverter.class)
	@Column(columnDefinition = "TEXT")
	private List<LocalDate> pausedDates; 
	@Column(name = "admin_rating")
	private Double adminRating; 	
	private LocalDateTime adminRatedOn;
	// Admin user who rated
	private Integer adminRatedBy;
	 
	@Column(columnDefinition = "TEXT")
	private String shootingHeldDescription;

}