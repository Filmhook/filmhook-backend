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
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategorySelectionDTO;

@Component
public class ShootingLocationConverter {
	  @Autowired private ShootingLocationCategoryRepository categoryRepo;
	    @Autowired private ShootingLocationSubcategoryRepository subcategoryRepo;
	    @Autowired private ShootingLocationTypesRepository typesRepo;
	    @Autowired private UserRepository userRepo;
	    @Autowired private IndustryRepository   industryRepo;

	    public ShootingLocationPropertyDetails toEntity(ShootingLocationPropertyDetailsDTO dto) {
	        return ShootingLocationPropertyDetails.builder()
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
	                .bakupGeneratorsAndVoltage(dto.getBakupGeneratorsAndVoltage())
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
	                .permissions(dto.getPermissions())
	                .noiseRestrictions(dto.getNoiseRestrictions())
	                .shootingTiming(dto.getShootingTiming())
	                .insuranceRequired(dto.getInsuranceRequired())
	                .legalAgreements(dto.getLegalAgreements())
	                .govtLicenseAndPermissions(dto.getGovtLicenseAndPermissions())
	                .roadAccessAndCondition(dto.getRoadAccessAndCondition())
	                .publicTransport(dto.getPublicTransport())
	                .nearestAirportOrRailway(dto.getNearestAirportOrRailway())
	                .accommodationNearby(dto.getAccommodationNearby())
	                .foodAndCatering(dto.getFoodAndCatering())
	                .emergencyServicesNearby(dto.getEmergencyServicesNearby())
	                .rentalCost(dto.getRentalCost())
	                .securityDeposit(dto.getSecurityDeposit())
	                .additionalCharges(dto.getAdditionalCharges())
	                .paymentModelsAccepted(dto.getPaymentModelsAccepted())
	                .cancellationPolicy(dto.getCancellationPolicy())
	                .description(dto.getDescription())
	                .priceCustomerPay(dto.getPriceCustomerPay())
	                .discount20Percent(dto.isDiscount20Percent())
	                .businessOwner(dto.isBusinessOwner())
	                .highQualityPhotos(dto.getHighQualityPhotos())
	                .videoWalkthrough(dto.getVideoWalkthrough())
	                .status(true)
	                .createdBy(dto.getUserId())
	                .createdOn(LocalDateTime.now())
	                .updatedBy(dto.getUserId())
	                .updatedOn(LocalDateTime.now())
	                .category(dto.getCategoryId() != null ? categoryRepo.findById(dto.getCategoryId()).orElse(null) : null)
	                .subCategory(dto.getSubCategoryId() != null ? subcategoryRepo.findById(dto.getSubCategoryId()).orElse(null) : null)
	                .types(dto.getTypesId() != null ? typesRepo.findById(dto.getTypesId()).orElse(null) : null)
	                .user(dto.getUserId() != null ? userRepo.findById(dto.getUserId()).orElse(null) : null)
	                .industry(dto.getIndustryId() != null ? industryRepo.findById(dto.getIndustryId()).orElse(null) : null)
	                .typeLocation(dto.getTypeLocation())
	                .locationLink(dto.getLocationLink())
	                .subcategorySelection(mapToEntity(dto.getSubcategorySelectionDTO()))
	                .build();
	    }

	    private ShootingLocationSubcategorySelection mapToEntity(ShootingLocationSubcategorySelectionDTO dto) {
	        if (dto == null) return null;
	        ShootingLocationSubcategory subcategory = subcategoryRepo.findById(dto.getSubcategoryId().intValue())
	                .orElseThrow(() -> new RuntimeException("Subcategory not found with ID: " + dto.getSubcategoryId()));

	        return ShootingLocationSubcategorySelection.builder()
	                .subcategory(subcategory)
	                .entireProperty(dto.getEntireProperty())
	                .singleProperty(dto.getSingleProperty())
	                .build();
	    }
}
