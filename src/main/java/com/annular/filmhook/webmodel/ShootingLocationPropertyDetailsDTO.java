package com.annular.filmhook.webmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.Convert;

import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.model.PropertyAvailabilityDate;
import com.annular.filmhook.util.StringListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;



@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShootingLocationPropertyDetailsDTO {
	// Property Information
	private Integer id;
	private String firstName;
	private String middleName;
	private String lastName;
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



	private LocalDateTime createdOn;
	private Integer createdBy;
	private Boolean status;

	private BusinessInformationDTO businessInformation;
	private BankDetailsDTO bankDetailsDTO;

	private List<String> imageUrls;
	private List<String> governmentIdUrls;
	private List<String> verificationVideo;

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
	private boolean likedByUser;
	private int likeCount;
	private Integer industryId;	
	private double averageRating;
	private List<ShootingLocationPropertyReviewDTO> reviews;
	private List<PropertyAvailabilityDTO> availabilityDates;

	private String typeLocation;
	private String locationLink;

	public Double getTotalArea() {
		return totalArea;
	}

}

