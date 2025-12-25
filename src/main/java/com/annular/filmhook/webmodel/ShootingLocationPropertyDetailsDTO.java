package com.annular.filmhook.webmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

import javax.persistence.Convert;

import com.annular.filmhook.util.StringListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShootingLocationPropertyDetailsDTO {
	private Integer id;
	private String fullName;
	private String citizenship;
	private String placeOfBirth;
	private String propertyName;
	private String location;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate dateOfBirth;
	private String proofOfIdentity;
	private String countryOfIssued;

	// 2. Listing Summary
	private String numberOfPeopleAllowed;
	private double totalArea;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String selectedUnit;
	private int numberOfRooms;
	private String numberOfFloor;
	private String ceilingHeight;

	private List<String> outdoorFeatures;
	private List<String> architecturalStyle;
	private List<String> vintage;
	private List<String> industrial;
	private List<String> traditional;

	// 3. Facilities & Amenities
	private String powerSupply;
	private String bakupGenerators;
	@Convert(converter = StringListConverter.class)
	private List<String> voltageCapacity;
	private String wifi;
	private String airConditionAndHeating;
	private int numberOfWashrooms;
	private List<String> restrooms;
	private List<String> waterSupply;
	private List<String> changingRooms;
	private List<String> kitchen;
	private List<String> furnitureAndProps;
	private List<String> neutralLightingConditions;
	private List<String> artificialLightingAvailability;
	private List<String> parkingCapacity;

	// 4. Filming Requirements & Restrictions
	private String droneUsage;
	private String firearms;
	private String actionScenes;
	private String security;
	private List<String> structuralModification;
	private String temporary;
	private String dressing;
	private List<String> permissions;
	private List<String> noiseRestrictions;
	private List<String> shootingTiming;
	private List<String> insuranceRequired;
	private List<String> legalAgreements;
	private String govtLicenseAndPermissions;

	// 5. Accessibility & Transportation
	private List<String> roadAccessAndCondition;
	private List<String> publicTransport;
	private List<String> nearestAirportOrRailway;
	private List<String> accommodationNearby;
	private List<String> foodAndCatering;
	private List<String> emergencyServicesNearby;

	// 6. Pricing & Payment Terms
	private List<String> rentalCost;
	private List<String> securityDeposit;
	private List<String> additionalCharges;
	private List<String> paymentModelsAccepted;
	private List<String> cancellationPolicy;
	private List<String> hygienStatus;
	private List<String> genderSpecific;

	private String description;
	private boolean businessOwner;
	private BusinessInformationDTO businessInformation;
	private BankDetailsDTO bankDetailsDTO;
	private Integer categoryId;
	private Integer subCategoryId;
	private Integer typesId;
	private Integer userId;
	private ShootingLocationTypeDTO type;
	private ShootingLocationCategoryDTO category;
	private ShootingLocationSubcategoryDTO subCategory;
	private ShootingLocationSubcategorySelectionDTO subcategorySelectionDTO;
	private String industryName;
	private List<Integer> industryIds;
	private Integer industryId;	
	
	private boolean likedByUser;
	private int likeCount;

	private List<PropertyAvailabilityDTO> availabilityDates;
	private String typeLocation;
	private String locationLink;
	private List<String> imageUrls;
	private String idNumber;
	private List<String> governmentIdUrls;
	private List<String> verificationVideo;
    private String ownerPermission;
    private String selfOwnedPropertyDocument;
    private String mortgagePropertyDocument; 
    private String ownerPermittedDocument; 
    private String propertyDamageDocument; 
	private String propertyDamageDescription;
    private String crewAccidentDocument; 
    private String crewAccidentLiabilityDescription;
    private String localAuthorities;
    private String governmentPermission;
    private String publicPermission;
    private Double additionalChargesForOverTime;
	private LocalDate availabilityStartDate;
	private LocalDate availabilityEndDate;
	private List<LocalDate> pausedDates; 
	private LocalDateTime createdOn;
	private Integer createdBy;
	private Boolean status;

	public Double getTotalArea() {
		return totalArea;
	}
	private long totalReviews;
	private double fiveStarPercentage;
	private double fourStarPercentage;
	private double threeStarPercentage;
	private double twoStarPercentage;
	private double oneStarPercentage;
	private double averageRating;
	private List<ShootingLocationPropertyReviewDTO> reviews;
	
	private Double adminRating;                  
	private LocalDateTime adminRatedOn;          
	private Integer adminRatedBy;
	
	private String shootingHeldDescription;
}

