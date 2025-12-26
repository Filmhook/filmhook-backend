package com.annular.filmhook.converter;
import org.springframework.stereotype.Component;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.ShootingLocationSubcategorySelection;
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
	            .fullName(e.getFullName())
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
	           
	            .waterSupply(e.getWaterSupply())
	            .changingRooms(e.getChangingRooms())
	            .kitchen(e.getKitchen())
	           
	            .neutralLightingConditions(e.getNeutralLightingConditions())
	            .artificialLightingAvailability(e.getArtificialLightingAvailability())
	            .parkingCapacity(e.getParkingCapacity())

	            // ----- Filming Restrictions -----
	            .droneUsage(e.getDroneUsage())
	            .firearms(e.getFirearms())
	            .actionScenes(e.getActionScenes())
	         
	            .structuralModification(e.getStructuralModification())
	            .temporary(e.getTemporary())
	            .dressing(e.getDressing())
	            .insuranceRequired(e.getInsuranceRequired())
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
	            .idNumber(e.getIdNumber())
	            .ownerPermission(e.getOwnerPermission())
	            .selfOwnedPropertyDocument(e.getSelfOwnedPropertyDocument())
	            .mortgagePropertyDocument(e.getMortgagePropertyDocument())
	            .ownerPermittedDocument(e.getOwnerPermittedDocument())
	            .propertyDamageDocument(e.getPropertyDamageDocument())
	            .propertyDamageDescription(e.getPropertyDamageDescription())
	            .crewAccidentLiabilityDescription(e.getCrewAccidentLiabilityDescription())
	            .crewAccidentDocument(e.getCrewAccidentDocument())
	            .localAuthorities(e.getLocalAuthorities())
	            .governmentPermission(e.getGovernmentPermission())
	            .additionalChargesForOverTime(e.getAdditionalChargesForOverTime())
	            .adminRatedBy(e.getAdminRatedBy())
	            .adminRatedOn(e.getAdminRatedOn())
	            .adminRating(e.getAdminRating())
	            .shootingHeldDescription(e.getShootingHeldDescription())
	            .propertyCode(e.getPropertyCode())
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
	            .fullName(dto.getFullName())
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
	          
	            .waterSupply(dto.getWaterSupply())
	            .changingRooms(dto.getChangingRooms())
	            .kitchen(dto.getKitchen())
	            
	            .neutralLightingConditions(dto.getNeutralLightingConditions())
	            .artificialLightingAvailability(dto.getArtificialLightingAvailability())
	            .parkingCapacity(dto.getParkingCapacity())
	            .droneUsage(dto.getDroneUsage())
	            .firearms(dto.getFirearms())
	            .actionScenes(dto.getActionScenes())
	           
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
	            .idNumber(dto.getIdNumber())
	            .ownerPermission(dto.getOwnerPermission())
	            .localAuthorities(dto.getLocalAuthorities())
	            .governmentPermission(dto.getGovernmentPermission())
	            .publicPermission(dto.getPublicPermission())
	            .propertyDamageDescription(dto.getPropertyDamageDescription())
	            .crewAccidentLiabilityDescription(dto.getCrewAccidentLiabilityDescription())
	            .additionalChargesForOverTime(dto.getAdditionalChargesForOverTime())
	            .shootingHeldDescription(dto.getShootingHeldDescription())

	            .status(true)
	            .build();

	    return e;
	}

}
