package com.annular.filmhook.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.annular.filmhook.util.StringListConverter;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;

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
	private Long id;
    private String industryName;

	// 1. Property Information
	private String firstName;
	private String middleName;
	private String lastName;
	private String citizenship;
	private String placeOfBirth;
	private String propertyName;
	private String location;
	private LocalDate dateOfBirth;
	private String proofOfIdentity;
	private String countryOfIssued;

	// 2. Listing Summary
	private int numberOfPeopleAllowed;
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
	@Convert(converter = StringListConverter.class)
	private List<String> bakupGeneratorsAndVoltage;
	private String wifi;
	private String airConditionAndHeating;
	private int numberOfWashrooms;
	@Convert(converter = StringListConverter.class)
	private List<String> restrooms;
	@Convert(converter = StringListConverter.class)
	private List<String> waterSupply;
	@Convert(converter = StringListConverter.class)
	private List<String> changingRooms;
	@Convert(converter = StringListConverter.class)
	private List<String> kitchen;
	@Convert(converter = StringListConverter.class)
	private List<String> furnitureAndProps;
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
	private String security;
	@Convert(converter = StringListConverter.class)
	private List<String> structuralModification;
	private String temporary;
	private String dressing;
	@Convert(converter = StringListConverter.class)
	private List<String> permissions;
	@Convert(converter = StringListConverter.class)
	private List<String> noiseRestrictions;
	@Convert(converter = StringListConverter.class)
	private List<String> shootingTiming;
	@Convert(converter = StringListConverter.class)
	private List<String> insuranceRequired;
	@Convert(converter = StringListConverter.class)
	private List<String> legalAgreements;

	// 5. Accessibility & Transportation
	@Convert(converter = StringListConverter.class)
	private List<String> roadAccessAndCondition;
	@Convert(converter = StringListConverter.class)
	private List<String> publicTransport;
	@Convert(converter = StringListConverter.class)
	private List<String> nearestAirportOrRailway;
	@Convert(converter = StringListConverter.class)
	private List<String> accommodationNearby;

	@Convert(converter = StringListConverter.class)
	private List<String> foodAndCatering;
	@Convert(converter = StringListConverter.class)
	private List<String> emergencyServicesNearby;

	// 6. Pricing & Payment Terms
	@Convert(converter = StringListConverter.class)
	private List<String> rentalCost;
	@Convert(converter = StringListConverter.class)
	private List<String> securityDeposit;
	@Convert(converter = StringListConverter.class)
	private List<String> additionalCharges;
	@Convert(converter = StringListConverter.class)
	private List<String> paymentModelsAccepted;
	@Convert(converter = StringListConverter.class)
	private List<String> cancellationPolicy;


	private String description;
	private double priceCustomerPay;
	private boolean discount20Percent;
	private boolean businessOwner;
	@Convert(converter = StringListConverter.class)
	private List<String> highQualityPhotos;
	@Convert(converter = StringListConverter.class)
	private List<String> videoWalkthrough;
	

    private Integer createdBy;

    @CreationTimestamp
 
    private LocalDateTime createdOn;

    private Integer updatedBy;

    @CreationTimestamp
    private LocalDateTime updatedOn;
    
	private Boolean status;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bank_details_id")
	private BankDetails bankDetails;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "business_information_id")
	private BusinessInformation businessInformation;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "subcategory_selection_id")
	private ShootingLocationSubcategorySelection subcategorySelection;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private ShootingLocationCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_category_id")
	private ShootingLocationSubcategory subCategory;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="types_id")
	private ShootingLocationTypes types;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
	
    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShootingLocationImages> mediaFiles;
	

}
