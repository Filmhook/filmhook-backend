package com.annular.filmhook.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.controller.ShootingLocationController;
import com.annular.filmhook.controller.StoriesController;
import com.annular.filmhook.model.BankDetails;
import com.annular.filmhook.model.BusinessInformation;
import com.annular.filmhook.model.ShootingLocationCategory;
import com.annular.filmhook.model.ShootingLocationImages;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.ShootingLocationSubcategory;
import com.annular.filmhook.model.ShootingLocationSubcategorySelection;
import com.annular.filmhook.model.ShootingLocationTypes;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.BankDetailsRepository;
import com.annular.filmhook.repository.BusinessInformationRepository;
import com.annular.filmhook.repository.ShootingLocationCategoryRepository;
import com.annular.filmhook.repository.ShootingLocationImageRepository;
import com.annular.filmhook.repository.ShootingLocationPropertyDetailsRepository;
import com.annular.filmhook.repository.ShootingLocationSubcategoryRepository;
import com.annular.filmhook.repository.ShootingLocationSubcategorySelectionRepository;
import com.annular.filmhook.repository.ShootingLocationTypesRepository;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.ShootingLocationService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.BankDetailsDTO;
import com.annular.filmhook.webmodel.BusinessInformationDTO;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFullDataDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategorySelectionDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShootingLocationServiceImpl implements ShootingLocationService {

	private final ShootingLocationTypesRepository typesRepo;
	private final ShootingLocationCategoryRepository categoryRepo;
	private final ShootingLocationSubcategoryRepository subcategoryRepo;
	private final ShootingLocationSubcategorySelectionRepository selectionRepo;
	@Autowired
	private ShootingLocationPropertyDetailsRepository propertyDetailsRepository;

	@Autowired
	private UserService userService;
	@Autowired
	private BusinessInformationRepository businessInformationRepository;

	@Autowired
	private BankDetailsRepository bankDetailsRepository;
	  

	    @Autowired
	    private ShootingLocationImageRepository shootingLocationImagesRepository;

	  

	    @Autowired
	    private AwsS3Service s3Service;

	public static final Logger logger = LoggerFactory.getLogger(ShootingLocationController.class);


	@Override
	public List<ShootingLocationTypeDTO> getAllTypes() {
		try {
			logger.info("Fetching all shooting location types");

			List<ShootingLocationTypes> types = typesRepo.findAll();

			List<ShootingLocationTypeDTO> typeDTOs = types.stream()
					.map(type -> ShootingLocationTypeDTO.builder()
							.id(type.getId())
							.name(type.getName())
							.description(type.getDescription())
							.build())
					.collect(Collectors.toList());

			logger.info("Successfully fetched {} types", typeDTOs.size());
			return typeDTOs;

		} catch (Exception e) {
			logger.error("Service error while fetching types: {}", e.getMessage(), e);
			throw new RuntimeException("Unable to fetch types from DB");
		}
	}


	@Override
	public List<ShootingLocationCategoryDTO> getCategoriesByTypeId(Integer typeId) {
		try {
			logger.info("Fetching categories for typeId: {}", typeId);

			List<ShootingLocationCategory> categories = categoryRepo.getCategoriesByTypeId(typeId); 

			List<ShootingLocationCategoryDTO> categoryDTOs = categories.stream()
					.map(category -> ShootingLocationCategoryDTO.builder()
							.id(category.getId())
							.name(category.getName())
							.build())
					.collect(Collectors.toList());

			logger.info("Successfully fetched {} categories for typeId: {}", categoryDTOs.size(), typeId);
			return categoryDTOs;

		} catch (Exception e) {
			logger.error("Error while fetching categories for typeId {}: {}", typeId, e.getMessage(), e);
			throw new RuntimeException("Unable to fetch categories from the database");
		}
	}


	@Override
	public List<ShootingLocationSubcategoryDTO> getSubcategoriesByCategoryId(Integer categoryId) {
		try {
			logger.info("Fetching subcategories for categoryId: {}", categoryId);

			List<ShootingLocationSubcategory> subcategories = subcategoryRepo.findByCategoryId(categoryId);

			List<ShootingLocationSubcategoryDTO> subcategoryDTOs = subcategories.stream()
					.map(subcategory -> ShootingLocationSubcategoryDTO.builder()
							.id(subcategory.getId())
							.name(subcategory.getName())
							.description(subcategory.getDescription())
							.build())
					.collect(Collectors.toList());

			logger.info("Successfully fetched {} subcategories for categoryId: {}", subcategoryDTOs.size(), categoryId);
			return subcategoryDTOs;

		} catch (Exception e) {
			logger.error("Error while fetching subcategories for categoryId {}: {}", categoryId, e.getMessage(), e);
			throw new RuntimeException("Unable to fetch subcategories from the database");
		}
	}





	@Override
	public void saveSelection(Long subcategoryId, Boolean entire, Boolean single) {
		try {
			logger.info("Saving selection for subcategoryId: {}, entire: {}, single: {}", subcategoryId, entire, single);

			ShootingLocationSubcategory subcategory = subcategoryRepo.findById(subcategoryId.intValue())
					.orElseThrow(() -> {
						logger.error("Subcategory not found with id: {}", subcategoryId);
						return new RuntimeException("Subcategory not found");
					});

			ShootingLocationSubcategorySelection selection = ShootingLocationSubcategorySelection.builder()
					.subcategory(subcategory)
					.entireProperty(entire)
					.singleProperty(single)
					.build();

			selectionRepo.save(selection);

			logger.info("Selection saved successfully for subcategoryId: {}", subcategoryId);

		} catch (Exception e) {
			logger.error("Error while saving selection for subcategoryId {}: {}", subcategoryId, e.getMessage(), e);
			throw new RuntimeException("Unable to save subcategory selection");
		}
	}



	@Override
	public ShootingLocationPropertyDetailsDTO savePropertyDetails(ShootingLocationPropertyDetailsDTO dto) {
		try {
			logger.info("Saving property", dto.getPropertyName());

			ShootingLocationCategory category = null;
			if (dto.getCategoryId() != null) {
				category = new ShootingLocationCategory();
				category.setId(dto.getCategoryId().intValue());  
			}

			ShootingLocationSubcategory subCategory = null;
			if (dto.getSubCategoryId() != null) {
				subCategory = new ShootingLocationSubcategory();
				subCategory.setId(dto.getSubCategoryId().intValue());
			}
			ShootingLocationTypes type=null;
			if(dto.getTypesId()!=null) {
				type= new ShootingLocationTypes();
				type.setId(dto.getTypesId().intValue());
			}
			User user=null;
			if(dto.getUserId()!=null) {
				user= new User();
				user.setUserId(dto.getUserId().intValue());
			}


			ShootingLocationPropertyDetails property = ShootingLocationPropertyDetails.builder()
					// 1. Owner & Property Identity
					.industryName(dto.getIndustryName())
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

					// 2. Listing Summary
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

					// 3. Facilities & Amenities
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

					// 4. Filming Requirements & Restrictions
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

					// 5. Accessibility & Transportation
					.roadAccessAndCondition(dto.getRoadAccessAndCondition())
					.publicTransport(dto.getPublicTransport())
					.nearestAirportOrRailway(dto.getNearestAirportOrRailway())
					.accommodationNearby(dto.getAccommodationNearby())
					.foodAndCatering(dto.getFoodAndCatering())
					.emergencyServicesNearby(dto.getEmergencyServicesNearby())

					// 6. Pricing & Payment Terms
					.rentalCost(dto.getRentalCost())
					.securityDeposit(dto.getSecurityDeposit())
					.additionalCharges(dto.getAdditionalCharges())
					.paymentModelsAccepted(dto.getPaymentModelsAccepted())
					.cancellationPolicy(dto.getCancellationPolicy())

					// Optional fields if you’ve added:
					.description(dto.getDescription())
					.priceCustomerPay(dto.getPriceCustomerPay())
					.discount20Percent(dto.isDiscount20Percent())
					.businessOwner(dto.isBusinessOwner())
					.highQualityPhotos(dto.getHighQualityPhotos())
					.videoWalkthrough(dto.getVideoWalkthrough())
					.category(category)
					.subCategory(subCategory)
					.types(type)
					.user(user)
					.subcategorySelection(mapToEntity(dto.getSubcategorySelectionDTO()))

					.build();

			ShootingLocationPropertyDetails savedProperty = propertyDetailsRepository.saveAndFlush(property);

			if (dto.getBusinessInformation() != null) {

				BusinessInformation business = BusinessInformation.builder()
						.propertyDetails(savedProperty)
						.businessName(dto.getBusinessInformation().getBusinessName())
						.businessType(dto.getBusinessInformation().getBusinessType())
						.businessLocation(dto.getBusinessInformation().getBusinessLocation())
						.panOrGSTNumber(dto.getBusinessInformation().getPanOrGSTNumber())
						.location(dto.getBusinessInformation().getLocation())
						.addressLine1(dto.getBusinessInformation().getAddressLine1())
						.addressLine2(dto.getBusinessInformation().getAddressLine2())
						.addressLine3(dto.getBusinessInformation().getAddressLine3())
						.state(dto.getBusinessInformation().getState())
						.postalCode(dto.getBusinessInformation().getPostalCode())
						.build();

				businessInformationRepository.save(business);
				savedProperty.setBusinessInformation(business);
			}

			if (dto.getBankDetailsDTO() != null) {

				BankDetailsDTO bankDto = dto.getBankDetailsDTO();

				BankDetails bank = BankDetails.builder()
						.propertyDetails(savedProperty)
						.beneficiaryName(dto.getBankDetailsDTO().getBeneficiaryName())
						.mobileNumber(dto.getBankDetailsDTO().getMobileNumber())
						.accountNumber(dto.getBankDetailsDTO().getAccountNumber())
						.confirmAccountNumber(dto.getBankDetailsDTO().getConfirmAccountNumber())
						.ifscCode(dto.getBankDetailsDTO().getIfscCode())
						.build();
				bankDetailsRepository.save(bank);
				savedProperty.setBankDetails(bank);

			}

			logger.info("Property saved with ID: {}", savedProperty.getId());
			propertyDetailsRepository.save(savedProperty);
			// Optionally return the full object (you can also map back to DTO)
			return dto;

		} catch (Exception e) {
			logger.error("Error at savePropertyDetails() -> {}", e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to save property details");
		}

	}
	private ShootingLocationSubcategorySelection mapToEntity(ShootingLocationSubcategorySelectionDTO dto) {
		if (dto == null) {
			return null;
		}

		ShootingLocationSubcategory subcategory = subcategoryRepo.findById(dto.getSubcategoryId().intValue())
				.orElseThrow(() -> new RuntimeException("Subcategory not found with ID: " + dto.getSubcategoryId()));


		return ShootingLocationSubcategorySelection.builder()
				.subcategory(subcategory)
				.entireProperty(dto.getEntireProperty())
				.singleProperty(dto.getSingleProperty())
				.build();
	}

	@Override
	public List<ShootingLocationPropertyDetailsDTO> getAllProperties() {
		logger.info("Starting getAllProperties() - fetching all properties from database");

		try {
			List<ShootingLocationPropertyDetails> properties = propertyDetailsRepository.findAll();
			List<ShootingLocationPropertyDetailsDTO> propertyDTOs = new ArrayList <>();
			logger.info("Starting getAllProperties() - fetching all properties from database", properties);
			for (ShootingLocationPropertyDetails property : properties) {


				BusinessInformationDTO businessInfoDTO = null;
				if (property.getBusinessInformation() != null) {
					var b = property.getBusinessInformation();
					businessInfoDTO = BusinessInformationDTO.builder()
							.id(property.getBusinessInformation().getId())
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
							.build();
				}

				BankDetailsDTO bankDetailsDTO = null;
				if (property.getBankDetails() != null) {
					var bank = property.getBankDetails();
					bankDetailsDTO = BankDetailsDTO.builder()

							.beneficiaryName(bank.getBeneficiaryName())
							.mobileNumber(bank.getMobileNumber())
							.accountNumber(bank.getAccountNumber())
							.confirmAccountNumber(bank.getConfirmAccountNumber())
							.ifscCode(bank.getIfscCode()) 
							.build();
				}

				ShootingLocationCategoryDTO categoryDTO = null;
				if (property.getCategory() != null) {
					ShootingLocationCategory category = categoryRepo.findById(property.getCategory().getId())
							.orElse(null);
					if (category != null) {
						categoryDTO = ShootingLocationCategoryDTO.builder()
								.id(category.getId())
								.name(category.getName())
								.build();
					}
				}


				ShootingLocationSubcategoryDTO subcategoryDTO = null;
				if (property.getSubCategory() != null) {
					ShootingLocationSubcategory subCategory = subcategoryRepo.findById(property.getSubCategory().getId())
							.orElse(null);
					if (subCategory != null) {
						subcategoryDTO = ShootingLocationSubcategoryDTO.builder()
								.id(subCategory.getId())
								.name(subCategory.getName())
								.description(subCategory.getDescription())
								.build();
					}
				}
				ShootingLocationTypeDTO typeDTO=null;
				if(property.getTypes()!=null) {
					ShootingLocationTypes types= typesRepo.findById(property.getTypes().getId())
							.orElse(null);
					if(types!=null) {
						typeDTO=ShootingLocationTypeDTO.builder()
								.id(types.getId())
								.name(types.getName())
								.description(types.getDescription())
								.build();
					}
				}

				ShootingLocationSubcategorySelectionDTO shootingLocationSubcategorySelectionDTO = null;
				if (property.getSubcategorySelection() != null) {
					var shooting = property.getSubcategorySelection();
					shootingLocationSubcategorySelectionDTO = ShootingLocationSubcategorySelectionDTO.builder()

							.entireProperty(shooting.getEntireProperty())
							.singleProperty(shooting.getSingleProperty())
							.build();
				}


				ShootingLocationPropertyDetailsDTO dto = ShootingLocationPropertyDetailsDTO.builder()
						// 1. Owner & Property Identity
						.id(property.getId())
						.firstName(property.getFirstName())
						.middleName(property.getMiddleName())
						.lastName(property.getLastName())
						.citizenship(property.getCitizenship())
						.placeOfBirth(property.getPlaceOfBirth())
						.propertyName(property.getPropertyName())
						.location(property.getLocation())
						.dateOfBirth(property.getDateOfBirth())
						.proofOfIdentity(property.getProofOfIdentity())
						.countryOfIssued(property.getCountryOfIssued())

						// 2. Listing Summary
						.numberOfPeopleAllowed(property.getNumberOfPeopleAllowed())
						.totalArea(property.getTotalArea())
						.selectedUnit(property.getSelectedUnit())
						.numberOfRooms(property.getNumberOfRooms())
						.numberOfFloor(property.getNumberOfFloor())
						.ceilingHeight(property.getCeilingHeight())
						.outdoorFeatures(property.getOutdoorFeatures() != null ? property.getOutdoorFeatures() : Collections.emptyList())
						.architecturalStyle(property.getArchitecturalStyle() != null ? property.getArchitecturalStyle() : Collections.emptyList())
						.vintage(property.getVintage() != null ? property.getVintage() : Collections.emptyList())
						.industrial(property.getIndustrial() != null ? property.getIndustrial() : Collections.emptyList())
						.traditional(property.getTraditional() != null ? property.getTraditional() : Collections.emptyList())

						// 3. Facilities & Amenities
						.powerSupply(property.getPowerSupply())
						.bakupGeneratorsAndVoltage(property.getBakupGeneratorsAndVoltage() != null ? property.getBakupGeneratorsAndVoltage() : Collections.emptyList())
						.wifi(property.getWifi())
						.airConditionAndHeating(property.getAirConditionAndHeating())
						.numberOfWashrooms(property.getNumberOfWashrooms())
						.restrooms(property.getRestrooms() != null ? property.getRestrooms() : Collections.emptyList())
						.waterSupply(property.getWaterSupply() != null ? property.getWaterSupply() : Collections.emptyList())
						.changingRooms(property.getChangingRooms() != null ? property.getChangingRooms() : Collections.emptyList())
						.kitchen(property.getKitchen() != null ? property.getKitchen() : Collections.emptyList())
						.furnitureAndProps(property.getFurnitureAndProps() != null ? property.getFurnitureAndProps() : Collections.emptyList())
						.neutralLightingConditions(property.getNeutralLightingConditions() != null ? property.getNeutralLightingConditions() : Collections.emptyList())
						.artificialLightingAvailability(property.getArtificialLightingAvailability() != null ? property.getArtificialLightingAvailability() : Collections.emptyList())
						.parkingCapacity(property.getParkingCapacity() != null ? property.getParkingCapacity() : Collections.emptyList())

						// 4. Filming Requirements & Restrictions
						.droneUsage(property.getDroneUsage())
						.firearms(property.getFirearms())
						.actionScenes(property.getActionScenes())
						.security(property.getSecurity())
						.structuralModification(property.getStructuralModification() != null ? property.getStructuralModification() : Collections.emptyList())
						.temporary(property.getTemporary())
						.dressing(property.getDressing())
						.permissions(property.getPermissions() != null ? property.getPermissions() : Collections.emptyList())
						.noiseRestrictions(property.getNoiseRestrictions() != null ? property.getNoiseRestrictions() : Collections.emptyList())
						.shootingTiming(property.getShootingTiming() != null ? property.getShootingTiming() : Collections.emptyList())
						.insuranceRequired(property.getInsuranceRequired() != null ? property.getInsuranceRequired() : Collections.emptyList())
						.legalAgreements(property.getLegalAgreements() != null ? property.getLegalAgreements() : Collections.emptyList())

						// 5. Accessibility & Transportation
						.roadAccessAndCondition(property.getRoadAccessAndCondition() != null ? property.getRoadAccessAndCondition() : Collections.emptyList())
						.publicTransport(property.getPublicTransport() != null ? property.getPublicTransport() : Collections.emptyList())
						.nearestAirportOrRailway(property.getNearestAirportOrRailway() != null ? property.getNearestAirportOrRailway() : Collections.emptyList())
						.accommodationNearby(property.getAccommodationNearby() != null ? property.getAccommodationNearby() : Collections.emptyList())
						.foodAndCatering(property.getFoodAndCatering() != null ? property.getFoodAndCatering() : Collections.emptyList())
						.emergencyServicesNearby(property.getEmergencyServicesNearby() != null ? property.getEmergencyServicesNearby() : Collections.emptyList())

						// 6. Pricing & Payment Terms
						.rentalCost(property.getRentalCost() != null ? property.getRentalCost() : Collections.emptyList())
						.securityDeposit(property.getSecurityDeposit() != null ? property.getSecurityDeposit() : Collections.emptyList())
						.additionalCharges(property.getAdditionalCharges() != null ? property.getAdditionalCharges() : Collections.emptyList())
						.paymentModelsAccepted(property.getPaymentModelsAccepted() != null ? property.getPaymentModelsAccepted() : Collections.emptyList())
						.cancellationPolicy(property.getCancellationPolicy() != null ? property.getCancellationPolicy() : Collections.emptyList())


						// Optional fields
						.description(property.getDescription())
						.priceCustomerPay(property.getPriceCustomerPay())
						.discount20Percent(property.isDiscount20Percent())
						.BusinessOwner(property.isBusinessOwner())
						.highQualityPhotos(property.getHighQualityPhotos())
						.videoWalkthrough(property.getVideoWalkthrough())

						// Nested DTOs
						.businessInformation(businessInfoDTO)
						.bankDetailsDTO(bankDetailsDTO)
						.subcategorySelectionDTO(shootingLocationSubcategorySelectionDTO)
						.category(categoryDTO)
						.subCategory(subcategoryDTO)
						.type(typeDTO)
						.build();

				propertyDTOs.add(dto);
			}

			logger.info("Completed getAllProperties() - total properties fetched: {}", propertyDTOs.size());
			return propertyDTOs;

		} catch (Exception e) {
			logger.error("Exception occurred in getAllProperties(): ", e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ShootingLocationPropertyDetailsDTO> getPropertiesByUserId(Integer userId) {
		logger.info("Starting getPropertiesByUserId() - fetching properties for userId: {}", userId);

		try {

			List<ShootingLocationPropertyDetails> properties = propertyDetailsRepository.findAllByUserId(userId);
			if (properties.isEmpty()) {
				logger.info("No properties found for userId: {}", userId);
				return Collections.emptyList();
			}


			Set<Integer> categoryIdInts = properties.stream()
					.map(p -> p.getCategory() != null ? p.getCategory().getId() : null)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());

			Set<Integer> subcategoryIds = properties.stream()
					.map(p -> p.getSubCategory() != null ? p.getSubCategory().getId() : null)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());

			Set<Integer> typeIds = properties.stream()
					.map(p -> p.getTypes() != null ? p.getTypes().getId() : null)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());

			
			Map<Integer, ShootingLocationCategory> categoryMap = categoryRepo.findAllById(categoryIdInts)
					.stream()
					.collect(Collectors.toMap(
							ShootingLocationCategory::getId,
							c -> c
							));
			Map<Integer, ShootingLocationSubcategory> subcategoryMap = subcategoryRepo.findAllById(subcategoryIds)
					.stream()
					.collect(Collectors.toMap(
							ShootingLocationSubcategory::getId,
							c -> c
							));

			Map<Integer, ShootingLocationTypes> typesMap = typesRepo.findAllById(typeIds)
					.stream()
					.collect(Collectors.toMap(
							ShootingLocationTypes::getId,
							c -> c
							));

			List<ShootingLocationPropertyDetailsDTO> propertyDTOs = new ArrayList<>();

			for (ShootingLocationPropertyDetails property : properties) {

				
				BusinessInformationDTO businessInfoDTO = null;
				if (property.getBusinessInformation() != null) {
					var b = property.getBusinessInformation();
					businessInfoDTO = BusinessInformationDTO.builder()
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
							.build();
				}

				// Map nested BankDetailsDTO
				BankDetailsDTO bankDetailsDTO = null;
				if (property.getBankDetails() != null) {
					var bank = property.getBankDetails();
					bankDetailsDTO = BankDetailsDTO.builder()
							.beneficiaryName(bank.getBeneficiaryName())
							.mobileNumber(bank.getMobileNumber())
							.accountNumber(bank.getAccountNumber())
							.confirmAccountNumber(bank.getConfirmAccountNumber())
							.ifscCode(bank.getIfscCode())
							.build();
				}

				// Map Category DTO (from batch map)
				ShootingLocationCategoryDTO categoryDTO = null;
				if (property.getCategory() != null) {
					ShootingLocationCategory category = categoryMap.get(property.getCategory().getId());
					if (category != null) {
						categoryDTO = ShootingLocationCategoryDTO.builder()
								.id(category.getId())
								.name(category.getName())
								.build();
					}
				}

				// Map SubCategory DTO (from batch map)
				ShootingLocationSubcategoryDTO subcategoryDTO = null;
				if (property.getSubCategory() != null) {
					ShootingLocationSubcategory subCategory = subcategoryMap.get(property.getSubCategory().getId());
					if (subCategory != null) {
						subcategoryDTO = ShootingLocationSubcategoryDTO.builder()
								.id(subCategory.getId())
								.name(subCategory.getName())
								.description(subCategory.getDescription())
								.build();
					}
				}

				// Map Type DTO (from batch map)
				ShootingLocationTypeDTO typeDTO = null;
				if (property.getTypes() != null) {
					ShootingLocationTypes types = typesMap.get(property.getTypes().getId());
					if (types != null) {
						typeDTO = ShootingLocationTypeDTO.builder()
								.id(types.getId())
								.name(types.getName())
								.description(types.getDescription())
								.build();
					}
				}

				// Map SubcategorySelection DTO
				ShootingLocationSubcategorySelectionDTO shootingLocationSubcategorySelectionDTO = null;
				if (property.getSubcategorySelection() != null) {
					var shooting = property.getSubcategorySelection();
					shootingLocationSubcategorySelectionDTO = ShootingLocationSubcategorySelectionDTO.builder()
							.entireProperty(shooting.getEntireProperty())
							.singleProperty(shooting.getSingleProperty())
							.build();
				}

				ShootingLocationPropertyDetailsDTO dto = ShootingLocationPropertyDetailsDTO.builder()
						// 1. Owner & Property Identity
						.id(property.getId())
						.firstName(property.getFirstName())
						.middleName(property.getMiddleName())
						.lastName(property.getLastName())
						.citizenship(property.getCitizenship())
						.placeOfBirth(property.getPlaceOfBirth())
						.propertyName(property.getPropertyName())
						.location(property.getLocation())
						.dateOfBirth(property.getDateOfBirth())
						.proofOfIdentity(property.getProofOfIdentity())
						.countryOfIssued(property.getCountryOfIssued())

						// 2. Listing Summary
						.numberOfPeopleAllowed(property.getNumberOfPeopleAllowed())
						.totalArea(property.getTotalArea())
						.selectedUnit(property.getSelectedUnit())
						.numberOfRooms(property.getNumberOfRooms())
						.numberOfFloor(property.getNumberOfFloor())
						.ceilingHeight(property.getCeilingHeight())
						.outdoorFeatures(property.getOutdoorFeatures() != null ? property.getOutdoorFeatures() : Collections.emptyList())
						.architecturalStyle(property.getArchitecturalStyle() != null ? property.getArchitecturalStyle() : Collections.emptyList())
						.vintage(property.getVintage() != null ? property.getVintage() : Collections.emptyList())
						.industrial(property.getIndustrial() != null ? property.getIndustrial() : Collections.emptyList())
						.traditional(property.getTraditional() != null ? property.getTraditional() : Collections.emptyList())

						// 3. Facilities & Amenities
						.powerSupply(property.getPowerSupply())
						.bakupGeneratorsAndVoltage(property.getBakupGeneratorsAndVoltage() != null ? property.getBakupGeneratorsAndVoltage() : Collections.emptyList())
						.wifi(property.getWifi())
						.airConditionAndHeating(property.getAirConditionAndHeating())
						.numberOfWashrooms(property.getNumberOfWashrooms())
						.restrooms(property.getRestrooms() != null ? property.getRestrooms() : Collections.emptyList())
						.waterSupply(property.getWaterSupply() != null ? property.getWaterSupply() : Collections.emptyList())
						.changingRooms(property.getChangingRooms() != null ? property.getChangingRooms() : Collections.emptyList())
						.kitchen(property.getKitchen() != null ? property.getKitchen() : Collections.emptyList())
						.furnitureAndProps(property.getFurnitureAndProps() != null ? property.getFurnitureAndProps() : Collections.emptyList())
						.neutralLightingConditions(property.getNeutralLightingConditions() != null ? property.getNeutralLightingConditions() : Collections.emptyList())
						.artificialLightingAvailability(property.getArtificialLightingAvailability() != null ? property.getArtificialLightingAvailability() : Collections.emptyList())
						.parkingCapacity(property.getParkingCapacity() != null ? property.getParkingCapacity() : Collections.emptyList())

						// 4. Filming Requirements & Restrictions
						.droneUsage(property.getDroneUsage())
						.firearms(property.getFirearms())
						.actionScenes(property.getActionScenes())
						.security(property.getSecurity())
						.structuralModification(property.getStructuralModification() != null ? property.getStructuralModification() : Collections.emptyList())
						.temporary(property.getTemporary())
						.dressing(property.getDressing())
						.permissions(property.getPermissions() != null ? property.getPermissions() : Collections.emptyList())
						.noiseRestrictions(property.getNoiseRestrictions() != null ? property.getNoiseRestrictions() : Collections.emptyList())
						.shootingTiming(property.getShootingTiming() != null ? property.getShootingTiming() : Collections.emptyList())
						.insuranceRequired(property.getInsuranceRequired() != null ? property.getInsuranceRequired() : Collections.emptyList())
						.legalAgreements(property.getLegalAgreements() != null ? property.getLegalAgreements() : Collections.emptyList())

						// 5. Accessibility & Transportation
						.roadAccessAndCondition(property.getRoadAccessAndCondition() != null ? property.getRoadAccessAndCondition() : Collections.emptyList())
						.publicTransport(property.getPublicTransport() != null ? property.getPublicTransport() : Collections.emptyList())
						.nearestAirportOrRailway(property.getNearestAirportOrRailway() != null ? property.getNearestAirportOrRailway() : Collections.emptyList())
						.accommodationNearby(property.getAccommodationNearby() != null ? property.getAccommodationNearby() : Collections.emptyList())
						.foodAndCatering(property.getFoodAndCatering() != null ? property.getFoodAndCatering() : Collections.emptyList())
						.emergencyServicesNearby(property.getEmergencyServicesNearby() != null ? property.getEmergencyServicesNearby() : Collections.emptyList())

						// 6. Pricing & Payment Terms
						.rentalCost(property.getRentalCost() != null ? property.getRentalCost() : Collections.emptyList())
						.securityDeposit(property.getSecurityDeposit() != null ? property.getSecurityDeposit() : Collections.emptyList())
						.additionalCharges(property.getAdditionalCharges() != null ? property.getAdditionalCharges() : Collections.emptyList())
						.paymentModelsAccepted(property.getPaymentModelsAccepted() != null ? property.getPaymentModelsAccepted() : Collections.emptyList())
						.cancellationPolicy(property.getCancellationPolicy() != null ? property.getCancellationPolicy() : Collections.emptyList())

						// Optional fields
						.description(property.getDescription())
						.priceCustomerPay(property.getPriceCustomerPay())
						.discount20Percent(property.isDiscount20Percent())
						.BusinessOwner(property.isBusinessOwner())
						.highQualityPhotos(property.getHighQualityPhotos())
						.videoWalkthrough(property.getVideoWalkthrough())

						// Nested DTOs
						.businessInformation(businessInfoDTO)
						.bankDetailsDTO(bankDetailsDTO)
						.subcategorySelectionDTO(shootingLocationSubcategorySelectionDTO)
						.category(categoryDTO)
						.subCategory(subcategoryDTO)
						.type(typeDTO)
						.build();

				propertyDTOs.add(dto);
			}

			logger.info("Completed getPropertiesByUserId() - total properties fetched: {}", propertyDTOs.size());
			return propertyDTOs;

		} catch (Exception e) {
			logger.error("Exception occurred in getPropertiesByUserId(): ", e);
			return Collections.emptyList();
		}
	}
	@Override
	public void deletePropertyById(Long id) {
		try {
			Optional<ShootingLocationPropertyDetails> optionalProperty = propertyDetailsRepository.findById(id);

			if (optionalProperty.isPresent()) {
				propertyDetailsRepository.deleteById(id);
				logger.info("Deleted property with ID: {}", id);
			} else {
				logger.warn("Property with ID {} not found for deletion", id);
				throw new RuntimeException("Property with ID " + id + " not found");
			}
		} catch (Exception e) {
			logger.error("Error deleting property with ID {}: {}", id, e.getMessage(), e);
			throw new RuntimeException("An error occurred while deleting the property with ID " + id);
		}
	}

	@Override
	public ShootingLocationPropertyDetailsDTO updateProperty(Long id, ShootingLocationPropertyDetailsDTO dto) {
		logger.info("Attempting to update property with ID: {}", id);

		try {
			Optional<ShootingLocationPropertyDetails> optionalProperty = propertyDetailsRepository.findById(id);
			if (optionalProperty.isEmpty()) {
				throw new RuntimeException("Property not found with ID: " + id);
			}

			ShootingLocationPropertyDetails property = optionalProperty.get();

			// 1. Property Info
			property.getId();

			property.setFirstName(dto.getFirstName());
			property.setMiddleName(dto.getMiddleName());
			property.setLastName(dto.getLastName());
			property.setCitizenship(dto.getCitizenship());
			property.setPlaceOfBirth(dto.getPlaceOfBirth());
			property.setPropertyName(dto.getPropertyName());
			property.setLocation(dto.getLocation());
			property.setDateOfBirth(dto.getDateOfBirth());
			property.setProofOfIdentity(dto.getProofOfIdentity());
			property.setCountryOfIssued(dto.getCountryOfIssued());

			// 2. Listing Summary
			property.setNumberOfPeopleAllowed(dto.getNumberOfPeopleAllowed());
			property.setTotalArea(dto.getTotalArea());
			property.setSelectedUnit(dto.getSelectedUnit());
			property.setNumberOfRooms(dto.getNumberOfRooms());
			property.setNumberOfFloor(dto.getNumberOfFloor());
			property.setCeilingHeight(dto.getCeilingHeight());
			property.setOutdoorFeatures(dto.getOutdoorFeatures());
			property.setArchitecturalStyle(dto.getArchitecturalStyle());
			property.setVintage(dto.getVintage());
			property.setIndustrial(dto.getIndustrial());
			property.setTraditional(dto.getTraditional());

			// 3. Facilities & Amenities
			property.setPowerSupply(dto.getPowerSupply());
			property.setBakupGeneratorsAndVoltage(dto.getBakupGeneratorsAndVoltage());
			property.setWifi(dto.getWifi());
			property.setAirConditionAndHeating(dto.getAirConditionAndHeating());
			property.setNumberOfWashrooms(dto.getNumberOfWashrooms());
			property.setRestrooms(dto.getRestrooms());
			property.setWaterSupply(dto.getWaterSupply());
			property.setChangingRooms(dto.getChangingRooms());
			property.setKitchen(dto.getKitchen());
			property.setFurnitureAndProps(dto.getFurnitureAndProps());
			property.setNeutralLightingConditions(dto.getNeutralLightingConditions());
			property.setArtificialLightingAvailability(dto.getArtificialLightingAvailability());
			property.setParkingCapacity(dto.getParkingCapacity());

			// 4. Filming Requirements & Restrictions
			property.setDroneUsage(dto.getDroneUsage());
			property.setFirearms(dto.getFirearms());
			property.setActionScenes(dto.getActionScenes());
			property.setSecurity(dto.getSecurity());
			property.setStructuralModification(dto.getStructuralModification());
			property.setTemporary(dto.getTemporary());
			property.setDressing(dto.getDressing());
			property.setPermissions(dto.getPermissions());
			property.setNoiseRestrictions(dto.getNoiseRestrictions());
			property.setShootingTiming(dto.getShootingTiming());
			property.setInsuranceRequired(dto.getInsuranceRequired());
			property.setLegalAgreements(dto.getLegalAgreements());

			// 5. Accessibility & Transportation
			property.setRoadAccessAndCondition(dto.getRoadAccessAndCondition());
			property.setPublicTransport(dto.getPublicTransport());
			property.setNearestAirportOrRailway(dto.getNearestAirportOrRailway());
			property.setAccommodationNearby(dto.getAccommodationNearby());
			property.setFoodAndCatering(dto.getFoodAndCatering());
			property.setEmergencyServicesNearby(dto.getEmergencyServicesNearby());

			// 6. Pricing & Payment Terms
			property.setRentalCost(dto.getRentalCost());
			property.setSecurityDeposit(dto.getSecurityDeposit());
			property.setAdditionalCharges(dto.getAdditionalCharges());
			property.setPaymentModelsAccepted(dto.getPaymentModelsAccepted());
			property.setCancellationPolicy(dto.getCancellationPolicy());

			// 7. Media References & Business Info
			property.setDescription(dto.getDescription());
			property.setPriceCustomerPay(dto.getPriceCustomerPay());
			property.setDiscount20Percent(dto.isDiscount20Percent());
			property.setBusinessOwner(dto.isBusinessOwner());
			property.setHighQualityPhotos(dto.getHighQualityPhotos());
			property.setVideoWalkthrough(dto.getVideoWalkthrough());

			// 8. Bank Details
			if (dto.getBankDetailsDTO() != null) {
				BankDetails bank = new BankDetails();
				bank.setId(dto.getBankDetailsDTO().getId());
				bank.setBeneficiaryName(dto.getBankDetailsDTO().getBeneficiaryName());
				bank.setMobileNumber(dto.getBankDetailsDTO().getMobileNumber());
				bank.setAccountNumber(dto.getBankDetailsDTO().getAccountNumber());
				bank.setConfirmAccountNumber(dto.getBankDetailsDTO().getConfirmAccountNumber());
				bank.setIfscCode(dto.getBankDetailsDTO().getIfscCode());
				property.setBankDetails(bank);
			}

			// 9. Business Info
			if (dto.getBusinessInformation() != null) {
				BusinessInformation info = new BusinessInformation();
				info.setBusinessName(dto.getBusinessInformation().getBusinessName());
				info.setBusinessType(dto.getBusinessInformation().getBusinessType());
				info.setBusinessLocation(dto.getBusinessInformation().getBusinessLocation());
				info.setPanOrGSTNumber(dto.getBusinessInformation().getPanOrGSTNumber());
				info.setLocation(dto.getBusinessInformation().getLocation());
				info.setAddressLine1(dto.getBusinessInformation().getAddressLine1());
				info.setAddressLine2(dto.getBusinessInformation().getAddressLine2());
				info.setAddressLine3(dto.getBusinessInformation().getAddressLine3());
				info.setState(dto.getBusinessInformation().getState());
				info.setPostalCode(dto.getBusinessInformation().getPostalCode());
				property.setBusinessInformation(info);
			}


			ShootingLocationPropertyDetails saved = propertyDetailsRepository.save(property);
			logger.info("Successfully updated property ID: {}", saved.getId());


			return dto; 

		} catch (Exception e) {
			logger.error("Error updating property ID {}: {}", id, e.getMessage(), e);
			throw new RuntimeException("Error updating property with ID: " + id + " - " + e.getMessage());
		}
	}
	@Override
	public List<FileOutputWebModel> saveShootingLocation(ShootingLocationPropertyDetailsDTO dto, MultipartFile[] files) {
	    List<FileOutputWebModel> fileOutputList = new ArrayList<>();
	    try {
			logger.info("Saving property", dto.getPropertyName());

			ShootingLocationCategory category = null;
			if (dto.getCategoryId() != null) {
				category = new ShootingLocationCategory();
				category.setId(dto.getCategoryId().intValue());  
			}

			ShootingLocationSubcategory subCategory = null;
			if (dto.getSubCategoryId() != null) {
				subCategory = new ShootingLocationSubcategory();
				subCategory.setId(dto.getSubCategoryId().intValue());
			}
			ShootingLocationTypes type=null;
			if(dto.getTypesId()!=null) {
				type= new ShootingLocationTypes();
				type.setId(dto.getTypesId().intValue());
			}
			User user=null;
			if(dto.getUserId()!=null) {
				user= new User();
				user.setUserId(dto.getUserId().intValue());
			}


			ShootingLocationPropertyDetails property = ShootingLocationPropertyDetails.builder()
					// 1. Owner & Property Identity
					.industryName(dto.getIndustryName())
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

					// 2. Listing Summary
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

					// 3. Facilities & Amenities
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

					// 4. Filming Requirements & Restrictions
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

					// 5. Accessibility & Transportation
					.roadAccessAndCondition(dto.getRoadAccessAndCondition())
					.publicTransport(dto.getPublicTransport())
					.nearestAirportOrRailway(dto.getNearestAirportOrRailway())
					.accommodationNearby(dto.getAccommodationNearby())
					.foodAndCatering(dto.getFoodAndCatering())
					.emergencyServicesNearby(dto.getEmergencyServicesNearby())

					// 6. Pricing & Payment Terms
					.rentalCost(dto.getRentalCost())
					.securityDeposit(dto.getSecurityDeposit())
					.additionalCharges(dto.getAdditionalCharges())
					.paymentModelsAccepted(dto.getPaymentModelsAccepted())
					.cancellationPolicy(dto.getCancellationPolicy())

					// Optional fields if you’ve added:
					.description(dto.getDescription())
					.priceCustomerPay(dto.getPriceCustomerPay())
					.discount20Percent(dto.isDiscount20Percent())
					.businessOwner(dto.isBusinessOwner())
					.highQualityPhotos(dto.getHighQualityPhotos())
					.videoWalkthrough(dto.getVideoWalkthrough())
					.category(category)
					.subCategory(subCategory)
					.types(type)
					.user(user)
					.subcategorySelection(mapToEntity(dto.getSubcategorySelectionDTO()))

					.build();

			ShootingLocationPropertyDetails savedProperty = propertyDetailsRepository.saveAndFlush(property);

			if (dto.getBusinessInformation() != null) {

				BusinessInformation business = BusinessInformation.builder()
						.propertyDetails(savedProperty)
						.businessName(dto.getBusinessInformation().getBusinessName())
						.businessType(dto.getBusinessInformation().getBusinessType())
						.businessLocation(dto.getBusinessInformation().getBusinessLocation())
						.panOrGSTNumber(dto.getBusinessInformation().getPanOrGSTNumber())
						.location(dto.getBusinessInformation().getLocation())
						.addressLine1(dto.getBusinessInformation().getAddressLine1())
						.addressLine2(dto.getBusinessInformation().getAddressLine2())
						.addressLine3(dto.getBusinessInformation().getAddressLine3())
						.state(dto.getBusinessInformation().getState())
						.postalCode(dto.getBusinessInformation().getPostalCode())
						.build();

				businessInformationRepository.save(business);
				savedProperty.setBusinessInformation(business);
			}

			if (dto.getBankDetailsDTO() != null) {

				BankDetailsDTO bankDto = dto.getBankDetailsDTO();

				BankDetails bank = BankDetails.builder()
						.propertyDetails(savedProperty)
						.beneficiaryName(dto.getBankDetailsDTO().getBeneficiaryName())
						.mobileNumber(dto.getBankDetailsDTO().getMobileNumber())
						.accountNumber(dto.getBankDetailsDTO().getAccountNumber())
						.confirmAccountNumber(dto.getBankDetailsDTO().getConfirmAccountNumber())
						.ifscCode(dto.getBankDetailsDTO().getIfscCode())
						.build();
				bankDetailsRepository.save(bank);
				savedProperty.setBankDetails(bank);
			}

			for (MultipartFile file : files) {
			    File convertedFile = convertMultiPartToFile(file);

			    // Assuming bucket name is passed via config or hardcoded
			    String s3Url = s3Service.putObjectIntoS3("your-bucket-name", "shooting-location-images/", convertedFile);

			    ShootingLocationImages image = ShootingLocationImages.builder()
			            .user(user)
			            .category("shooting")
			            .fileName(file.getOriginalFilename())
			            .fileSize(file.getSize())
			            .fileType(file.getContentType())
			            .filePath(s3Url)
			            .status(true)
			            .createdBy(user.getUserId().intValue())
			            .build();

			    shootingLocationImagesRepository.save(image);

			    fileOutputList.add(FileOutputWebModel.builder()
			            .id(image.getShootingmediaId())
			            .fileName(image.getFileName())
			            .filePath(image.getFilePath())
			            .fileType(image.getFileType())
			            .build());
			}

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to save shooting location and files", e);
	    }

	    return fileOutputList;
	}
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
	    File convFile = File.createTempFile("temp", file.getOriginalFilename());
	    file.transferTo(convFile);
	    return convFile;
	}
}
