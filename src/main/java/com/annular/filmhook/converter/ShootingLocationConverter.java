package com.annular.filmhook.converter;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.ShootingLocationSubcategory;
import com.annular.filmhook.model.ShootingLocationSubcategorySelection;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.ShootingLocationCategoryRepository;
import com.annular.filmhook.repository.ShootingLocationSubcategoryRepository;
import com.annular.filmhook.repository.ShootingLocationTypesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.webmodel.BankDetailsDTO;
import com.annular.filmhook.webmodel.BusinessInformationDTO;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategorySelectionDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;

@Component
public class ShootingLocationConverter {

	public ShootingLocationPropertyDetailsDTO entityToDto(ShootingLocationPropertyDetails e) {
	    if (e == null) return null;

	    ShootingLocationPropertyDetailsDTO dto = ShootingLocationPropertyDetailsDTO.builder()
	            .id(e.getId())

	            // ----- Owner & Identity -----
	            .firstName(e.getFirstName())
	            .middleName(e.getMiddleName())
	            .lastName(e.getLastName())
	            .citizenship(e.getCitizenship())
	            .placeOfBirth(e.getPlaceOfBirth())
	            .propertyName(e.getPropertyName())
	            .location(e.getLocation())
	            .dateOfBirth(e.getDateOfBirth())
	            .proofOfIdentity(e.getProofOfIdentity())
	            .countryOfIssued(e.getCountryOfIssued())

	            // ----- Listing Summary -----
	            .numberOfPeopleAllowed(e.getNumberOfPeopleAllowed())
	            .totalArea(e.getTotalArea())
	            .selectedUnit(e.getSelectedUnit())
	            .numberOfRooms(e.getNumberOfRooms())
	            .numberOfFloor(e.getNumberOfFloor())
	            .ceilingHeight(e.getCeilingHeight())
	            .outdoorFeatures(e.getOutdoorFeatures())
	            .architecturalStyle(e.getArchitecturalStyle())
	            .vintage(e.getVintage())
	            .industrial(e.getIndustrial())
	            .traditional(e.getTraditional())

	            // ----- Facilities -----
	            .powerSupply(e.getPowerSupply())
	            .bakupGenerators(e.getBakupGenerators())
	            .voltageCapacity(e.getVoltageCapacity())
	            .wifi(e.getWifi())
	            .airConditionAndHeating(e.getAirConditionAndHeating())
	            .numberOfWashrooms(e.getNumberOfWashrooms())
	            .restrooms(e.getRestrooms())
	            .waterSupply(e.getWaterSupply())
	            .changingRooms(e.getChangingRooms())
	            .kitchen(e.getKitchen())
	            .furnitureAndProps(e.getFurnitureAndProps())
	            .neutralLightingConditions(e.getNeutralLightingConditions())
	            .artificialLightingAvailability(e.getArtificialLightingAvailability())
	            .parkingCapacity(e.getParkingCapacity())

	            // ----- Filming Restrictions -----
	            .droneUsage(e.getDroneUsage())
	            .firearms(e.getFirearms())
	            .actionScenes(e.getActionScenes())
	            .security(e.getSecurity())
	            .structuralModification(e.getStructuralModification())
	            .temporary(e.getTemporary())
	            .dressing(e.getDressing())
//	            .permissions(e.getPermissions())
//	            .noiseRestrictions(e.getNoiseRestrictions())
//	            .shootingTiming(e.getShootingTiming())
	            .insuranceRequired(e.getInsuranceRequired())
//	            .legalAgreements(e.getLegalAgreements())

	            // ----- Accessibility -----
//	            .roadAccessAndCondition(e.getRoadAccessAndCondition())
//	            .publicTransport(e.getPublicTransport())
//	            .nearestAirportOrRailway(e.getNearestAirportOrRailway())
//	            .accommodationNearby(e.getAccommodationNearby())
//	            .foodAndCatering(e.getFoodAndCatering())
//	            .emergencyServicesNearby(e.getEmergencyServicesNearby())

	            // ----- Pricing -----
//	            .rentalCost(e.getRentalCost())
//	            .securityDeposit(e.getSecurityDeposit())
//	            .additionalCharges(e.getAdditionalCharges())
//	            .paymentModelsAccepted(e.getPaymentModelsAccepted())
//	            .cancellationPolicy(e.getCancellationPolicy())

	            // ----- Misc -----
	            .description(e.getDescription())
	            .businessOwner(e.isBusinessOwner())
//	            .govtLicenseAndPermissions(e.getGovtLicenseAndPermissions())
	            .createdBy(e.getCreatedBy())
	            .createdOn(e.getCreatedOn())
	            .status(e.getStatus())
	            .typeLocation(e.getTypeLocation())
	            .locationLink(e.getLocationLink())
	            .hygienStatus(e.getHygienStatus())
	            .genderSpecific(e.getGenderSpecific())
	            .IdNumber(e.getIdNumber())
	            .ownerPermission(e.getOwnerPermission())
	            .selfOwnedPropertyDocument(e.getSelfOwnedPropertyDocument())
	            .mortgagePropertyDocument(e.getMiddleName())
	            .ownerPermittedDocument(e.getOwnerPermittedDocument())
	            .propertyDamageDocument(e.getPropertyDamageDocument())
	            .propertyDamageDescription(e.getPropertyDamageDescription())
	            .crewAccidentLiabilityDescription(e.getCrewAccidentLiabilityDescription())
	            .crewAccidentDocument(e.getCrewAccidentDocument())
	            .localAuthorities(e.getLocalAuthorities())
	            .governmentPermission(e.getGovernmentPermission())
	            .additionalChargesForOverTime(e.getAdditionalChargesForOverTime())
	            .build();
	   
	    // ----- Category -----
	    if (e.getCategory() != null) {
	        dto.setCategory(ShootingLocationCategoryDTO.builder()
	                .id(e.getCategory().getId())
	                .name(e.getCategory().getName())
	                .build());
	        dto.setCategoryId(e.getCategory().getId());
	    }

	    // ----- SubCategory -----
	    if (e.getSubCategory() != null) {
	        dto.setSubCategory(ShootingLocationSubcategoryDTO.builder()
	                .id(e.getSubCategory().getId())
	                .name(e.getSubCategory().getName())
	                .description(e.getSubCategory().getDescription())
	                .imageUrl(e.getSubCategory().getImageUrl())
	                .build());
	        dto.setSubCategoryId(e.getSubCategory().getId());
	    }

	    // ----- Types -----
	    if (e.getTypes() != null) {
	        dto.setType(ShootingLocationTypeDTO.builder()
	                .id(e.getTypes().getId())
	                .name(e.getTypes().getName())
	                .description(e.getTypes().getDescription())
	                .build());
	        dto.setTypesId(e.getTypes().getId());
	    }

	    // ----- Industry -----
	    if (e.getIndustry() != null) {
	        dto.setIndustryId(e.getIndustry().getIndustryId());
	        dto.setIndustryName(e.getIndustry().getIndustryName());
	    }

	    // ----- Subcategory Selection -----
	    if (e.getSubcategorySelection() != null) {
	        ShootingLocationSubcategorySelection s = e.getSubcategorySelection();

	        dto.setSubcategorySelectionDTO(ShootingLocationSubcategorySelectionDTO.builder()
	                .id(s.getId())
	                .subcategoryId(s.getSubcategory().getId())
	                .entireProperty(s.getEntireProperty())
	                .singleProperty(s.getSingleProperty())
	                .entireDayPropertyPrice(s.getEntireDayPropertyPrice())
	                .entireNightPropertyPrice(s.getEntireNightPropertyPrice())
	                .entireFullDayPropertyPrice(s.getEntireFullDayPropertyPrice())
	                .singleDayPropertyPrice(s.getSingleDayPropertyPrice())
	                .singleNightPropertyPrice(s.getSingleNightPropertyPrice())
	                .singleFullDayPropertyPrice(s.getSingleFullDayPropertyPrice())
	                .entirePropertyDayDiscountPercent(s.getEntirePropertyDayDiscountPercent())
	                .entirePropertyNightDiscountPercent(s.getEntirePropertyNightDiscountPercent())
	                .entirePropertyFullDayDiscountPercent(s.getEntirePropertyFullDayDiscountPercent())
	                .singlePropertyDayDiscountPercent(s.getSinglePropertyDayDiscountPercent())
	                .singlePropertyNightDiscountPercent(s.getSinglePropertyNightDiscountPercent())
	                .singlePropertyFullDayDiscountPercent(s.getSinglePropertyFullDayDiscountPercent())
	                .build());
	    }

	    // ----- Business Information -----
	    if (e.getBusinessInformation() != null) {
	        var b = e.getBusinessInformation();
	        dto.setBusinessInformation(BusinessInformationDTO.builder()
	                .id(b.getId())
	                .businessName(b.getBusinessName())
	                .businessType(b.getBusinessType())
	                .businessLocation(b.getBusinessLocation())
	                .panOrGSTNumber(b.getPanOrGSTNumber())
	                .location(b.getLocation())
	                .addressLine1(b.getAddressLine1())
	                .addressLine2(b.getAddressLine2())
	                .addressLine3(b.getAddressLine3())
	                .state(b.getState())
	                .postalCode(b.getPostalCode())
	                .build());
	    }

	    // ----- Bank -----
	    if (e.getBankDetails() != null) {
	        var b = e.getBankDetails();
	        dto.setBankDetailsDTO(BankDetailsDTO.builder()
	                .id(b.getId())
	                .beneficiaryName(b.getBeneficiaryName())
	                .mobileNumber(b.getMobileNumber())
	                .accountNumber(b.getAccountNumber())
	                .confirmAccountNumber(b.getConfirmAccountNumber())
	                .ifscCode(b.getIfscCode())
	                .build());
	    }

	    return dto;
	}


    /**
     * Partial DTO -> Entity converter.
     * Note: relations (category, subCategory, types, user, industry) are NOT resolved here.
     * Service should set them after fetching.
     */
	public ShootingLocationPropertyDetails dtoToEntity(ShootingLocationPropertyDetailsDTO dto) {
	    if (dto == null) return null;

	    ShootingLocationPropertyDetails e = ShootingLocationPropertyDetails.builder()
	            .id(dto.getId())
	            .firstName(dto.getFirstName())
	            .middleName(dto.getMiddleName())
	            .lastName(dto.getLastName())
	            .citizenship(dto.getCitizenship())
	            .placeOfBirth(dto.getPlaceOfBirth())
	            .propertyName(dto.getPropertyName())
	            .location(dto.getLocation())
	            .dateOfBirth(dto.getDateOfBirth())
	            .proofOfIdentity(dto.getProofOfIdentity())
	            .countryOfIssued(dto.getCountryOfIssued())
	            .numberOfPeopleAllowed(dto.getNumberOfPeopleAllowed())
	            .totalArea(dto.getTotalArea())
	            .selectedUnit(dto.getSelectedUnit())
	            .numberOfRooms(dto.getNumberOfRooms())
	            .numberOfFloor(dto.getNumberOfFloor())
	            .ceilingHeight(dto.getCeilingHeight())
	            .outdoorFeatures(dto.getOutdoorFeatures())
	            .architecturalStyle(dto.getArchitecturalStyle())
	            .vintage(dto.getVintage())
	            .industrial(dto.getIndustrial())
	            .traditional(dto.getTraditional())
	            .powerSupply(dto.getPowerSupply())
	            .bakupGenerators(dto.getBakupGenerators())
	            .voltageCapacity(dto.getVoltageCapacity())
	            .wifi(dto.getWifi())
	            .airConditionAndHeating(dto.getAirConditionAndHeating())
	            .numberOfWashrooms(dto.getNumberOfWashrooms())
	            .restrooms(dto.getRestrooms())
	            .waterSupply(dto.getWaterSupply())
	            .changingRooms(dto.getChangingRooms())
	            .kitchen(dto.getKitchen())
	            .furnitureAndProps(dto.getFurnitureAndProps())
	            .neutralLightingConditions(dto.getNeutralLightingConditions())
	            .artificialLightingAvailability(dto.getArtificialLightingAvailability())
	            .parkingCapacity(dto.getParkingCapacity())
	            .droneUsage(dto.getDroneUsage())
	            .firearms(dto.getFirearms())
	            .actionScenes(dto.getActionScenes())
	            .security(dto.getSecurity())
	            .structuralModification(dto.getStructuralModification())
	            .temporary(dto.getTemporary())
	            .dressing(dto.getDressing())
	            .insuranceRequired(dto.getInsuranceRequired())
	            .description(dto.getDescription())
	            .businessOwner(dto.isBusinessOwner())
	            .typeLocation(dto.getTypeLocation())
	            .locationLink(dto.getLocationLink())
	            .hygienStatus(dto.getHygienStatus())
	            .genderSpecific(dto.getGenderSpecific())
	            .IdNumber(dto.getIdNumber())
	            .ownerPermission(dto.getOwnerPermission())
	            .localAuthorities(dto.getLocalAuthorities())
	            .governmentPermission(dto.getGovernmentPermission())
	            .propertyDamageDescription(dto.getPropertyDamageDescription())
	            .crewAccidentLiabilityDescription(dto.getCrewAccidentLiabilityDescription())
	            .additionalChargesForOverTime(dto.getAdditionalChargesForOverTime())
	            .status(true)
	            .build();

	    return e;
	}

}
