package com.annular.filmhook.service.impl;


import java.io.File;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.controller.ShootingLocationController;
import com.annular.filmhook.converter.ShootingLocationConverter;
import com.annular.filmhook.model.ShootingLocationOwnerBankDetails;
import com.annular.filmhook.model.Bookings;
import com.annular.filmhook.model.ShootingLocationBusinessInformation;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MultiMediaFiles;
import com.annular.filmhook.model.PropertyAvailabilityDate;
import com.annular.filmhook.model.PropertyLike;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationCategory;

import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.ShootingLocationPropertyReview;
import com.annular.filmhook.model.ShootingLocationSubcategory;
import com.annular.filmhook.model.ShootingLocationSubcategorySelection;
import com.annular.filmhook.model.ShootingLocationTypes;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.BankDetailsRepository;
import com.annular.filmhook.repository.BusinessInformationRepository;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.repository.PropertyAvailabilityDateRepository;
import com.annular.filmhook.repository.PropertyLikeRepository;
import com.annular.filmhook.repository.ShootingLocationBookingRepository;
import com.annular.filmhook.repository.ShootingLocationCategoryRepository;

import com.annular.filmhook.repository.ShootingLocationPropertyDetailsRepository;
import com.annular.filmhook.repository.ShootingLocationPropertyReviewRepository;
import com.annular.filmhook.repository.ShootingLocationSubcategoryRepository;
import com.annular.filmhook.repository.ShootingLocationSubcategorySelectionRepository;
import com.annular.filmhook.repository.ShootingLocationTypesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.ShootingLocationService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.FilmHookConstants;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.BankDetailsDTO;
import com.annular.filmhook.webmodel.BusinessInformationDTO;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PropertyAvailabilityDTO;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewResponseDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategorySelectionDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;

import software.amazon.awssdk.services.ssm.model.ResourceNotFoundException;


@Service
public class ShootingLocationServiceImpl implements ShootingLocationService {

	public static final Logger logger = LoggerFactory.getLogger(ShootingLocationController.class);

	@Autowired
	private ShootingLocationTypesRepository typesRepo;

	@Autowired
    private ShootingLocationBookingRepository bookingRepo;
	@Autowired 
	private UserDetails userDetails;
	@Autowired
	private PropertyLikeRepository likeRepository;

	@Autowired
	private ShootingLocationCategoryRepository categoryRepo;

	@Autowired
	private ShootingLocationSubcategoryRepository subcategoryRepo;

	@Autowired
	private  ShootingLocationSubcategorySelectionRepository selectionRepo;

	@Autowired
	private ShootingLocationPropertyDetailsRepository propertyDetailsRepository;

	@Autowired
	MultiMediaFileRepository multiMediaFilesRepository;

	@Autowired 
	ShootingLocationPropertyReviewRepository propertyReviewRepository;

	@Autowired
	AwsS3Service awsS3Service;

	@Autowired
	ShootingLocationBookingRepository bookingRepository;

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	S3Util s3Util;

	@Autowired
	ShootingLocationConverter shootingLocationPropertyConverter;

	@Autowired
	private FileUtil fileUtil;


	@Autowired
	private BusinessInformationRepository businessInformationRepository;

	@Autowired
	private BankDetailsRepository bankDetailsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	IndustryRepository industryRepository;


	@Autowired
	private UserService userService;

	@Autowired
	private PropertyAvailabilityDateRepository availabilityRepository;



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
							.imageUrl(subcategory.getImageUrl())
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
	public Response savePropertyDetails(ShootingLocationPropertyDetailsDTO dto, ShootingLocationFileInputModel inputFile) {

		try {
			logger.info("Saving property: {}", dto.getPropertyName());
			// 1. VALIDATE: CATEGORY
			ShootingLocationCategory category = null;
			if (dto.getCategoryId() != null) {
				category = categoryRepo.findById(dto.getCategoryId()).orElse(null);
				if (category == null) {
					return new Response(-1,
							"Category not found: " + dto.getCategoryId(), null);
				}
			}

			// 2. VALIDATE: SUBCATEGORY
			ShootingLocationSubcategory subCategory = null;
			if (dto.getSubCategoryId() != null) {
				subCategory = subcategoryRepo.findById(dto.getSubCategoryId()).orElse(null);
				if (subCategory == null) {
					return new Response(-1,
							"Subcategory not found: " + dto.getSubCategoryId(), null);
				}
			}
			// 3. VALIDATE: TYPE
			ShootingLocationTypes type = null;
			if (dto.getTypesId() != null) {
				type = typesRepo.findById(dto.getTypesId()).orElse(null);
				if (type == null) {
					return new Response(-1,
							"Type not found: " + dto.getTypesId(), null);
				}
			}
			// 4. VALIDATE: USER
			User user = null;
			if (dto.getUserId() != null) {
				user = userRepository.findById(dto.getUserId()).orElse(null);
				if (user == null) {
					return new Response(-1,
							"User not found: " + dto.getUserId(), null);
				}
			}
			// 5. VALIDATE: INDUSTRY
			Industry industry = null;
			if (dto.getIndustryId() != null) {
				industry = industryRepository.findById(dto.getIndustryId()).orElse(null);
				if (industry == null) {
					return new Response(-1,
							"Industry not found: " + dto.getIndustryId(), null);
				}
			}
			// 6. Convert DTO to ENTITY
			ShootingLocationPropertyDetails property =
					shootingLocationPropertyConverter.dtoToEntity(dto);

			property.setCategory(category);
			property.setSubCategory(subCategory);
			property.setTypes(type);
			property.setUser(user);
			property.setIndustry(industry);

			// subcategory selection
			property.setSubcategorySelection(mapToEntity(dto.getSubcategorySelectionDTO()));

			// audit fields
			property.setStatus(true);
			property.setCreatedBy(dto.getUserId());
			property.setUpdatedBy(dto.getUserId());
			property.setCreatedOn(LocalDateTime.now());
			property.setUpdatedOn(LocalDateTime.now());

			// 7. Save Base Property

			ShootingLocationPropertyDetails savedProperty =
					propertyDetailsRepository.saveAndFlush(property);

			// 8. Save Business Info

			if (dto.getBusinessInformation() != null) {
				ShootingLocationBusinessInformation business = ShootingLocationBusinessInformation.builder()
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

			// 9. Save Bank Details
			if (dto.getBankDetailsDTO() != null) {
				ShootingLocationOwnerBankDetails bank = ShootingLocationOwnerBankDetails.builder()
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

			// 10. FILE UPLOAD
			if (inputFile != null && user != null) {

				// images
				if (inputFile.getImages() != null && !inputFile.getImages().isEmpty()) {
					FileInputWebModel imagesInput = FileInputWebModel.builder()
							.userId(user.getUserId())
							.category(MediaFileCategory.shootingLocationImage)
							.categoryRefId(savedProperty.getId())
							.files(inputFile.getImages())
							.build();

					mediaFilesService.saveMediaFiles(imagesInput, user);
				}

				// government ID
				if (inputFile.getGovermentId() != null && !inputFile.getGovermentId().isEmpty()) {
					FileInputWebModel govtInput = FileInputWebModel.builder()
							.userId(user.getUserId())
							.category(MediaFileCategory.shootingGovermentId)
							.categoryRefId(savedProperty.getId())
							.files(inputFile.getGovermentId())
							.build();

					mediaFilesService.saveMediaFiles(govtInput, user);
				}
				
				
				if (inputFile.getVideos() != null && !inputFile.getVideos().isEmpty()) {
					FileInputWebModel videoInput = FileInputWebModel.builder()
							.userId(user.getUserId())
							.category(MediaFileCategory.shootingLocationVerificationVideo)
							.categoryRefId(savedProperty.getId())
							.files(inputFile.getVideos())
							.build();

					mediaFilesService.saveMediaFiles(videoInput, user);
					
				
			}

			}
			logger.info("Property saved successfully: {}", savedProperty.getId());
			return new Response(1, "Property saved successfully", null);

		} catch (Exception e) {
			logger.error("Error in saving property: {}", e.getMessage(), e);
			return new Response(-1, "Failed to save property details", e.getMessage());
		}
	}
 

	private ShootingLocationSubcategorySelection mapToEntity(ShootingLocationSubcategorySelectionDTO dto) {
	    if (dto == null) return null;

	    ShootingLocationSubcategory subcategory = subcategoryRepo.findById(dto.getSubcategoryId().intValue())
	            .orElseThrow(() -> new RuntimeException("Subcategory not found with ID: " + dto.getSubcategoryId()));

	    ShootingLocationSubcategorySelection sel = ShootingLocationSubcategorySelection.builder()
	            .subcategory(subcategory)
	            .entireProperty(dto.getEntireProperty())
	            .singleProperty(dto.getSingleProperty())
	            .entireDayPropertyPrice(dto.getEntireDayPropertyPrice())
	            .entireNightPropertyPrice(dto.getEntireNightPropertyPrice())
	            .entireFullDayPropertyPrice(dto.getEntireFullDayPropertyPrice())
	            .singleDayPropertyPrice(dto.getSingleDayPropertyPrice())
	            .singleNightPropertyPrice(dto.getSingleNightPropertyPrice())
	            .singleFullDayPropertyPrice(dto.getSingleFullDayPropertyPrice())
	            .entirePropertyDiscount20Percent(dto.isEntirePropertyDiscount20Percent())
	            .singlePropertyDiscount20Percent(dto.isSinglePropertyDiscount20Percent())
	            .build();

	    // Save start date for Entire Property Discount
	    if (dto.isEntirePropertyDiscount20Percent()) {
	        sel.setEntirePropertyDiscountStartDate(LocalDateTime.now());
	        sel.setEntirePropertyDiscountBookingCount(0);
	    } else {
	        sel.setEntirePropertyDiscountStartDate(null);
	        sel.setEntirePropertyDiscountBookingCount(0);
	    }

	    // ⭐ IMPORTANT — Save start date for Single Property Discount
	    if (dto.isSinglePropertyDiscount20Percent()) {
	        sel.setSinglePropertyDiscountStartDate(LocalDateTime.now());
	        sel.setSinglePropertyDiscountBookingCount(0);
	    } else {
	        sel.setSinglePropertyDiscountStartDate(null);
	        sel.setSinglePropertyDiscountBookingCount(0);
	    }

	    return sel;
	}


@Override
public List<ShootingLocationPropertyDetailsDTO> getAllProperties(Integer userId) {

    logger.info("Starting getAllProperties() - fetching all properties from database");

    try {
        List<ShootingLocationPropertyDetails> properties = propertyDetailsRepository.findAll();

        List<ShootingLocationPropertyDetailsDTO> dtoList = new ArrayList<>();

        for (ShootingLocationPropertyDetails p : properties) {

            // Convert entity → DTO
            ShootingLocationPropertyDetailsDTO dto = shootingLocationPropertyConverter.entityToDto(p);

            // ----------- LIKE STATUS -----------
            Optional<PropertyLike> likeOpt =
                    likeRepository.findByPropertyIdAndLikedById(p.getId(), userId);

            dto.setLikedByUser(likeOpt.map(PropertyLike::getStatus).orElse(false));

            // ----------- MEDIA FILES USING service -----------

            List<String> imageUrls = mediaFilesService
                    .getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationImage, p.getId())
                    .stream()
                    .map(FileOutputWebModel::getFilePath)
                    .collect(Collectors.toList());

            List<String> govtIdUrls = mediaFilesService
                    .getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingGovermentId, p.getId())
                    .stream()
                    .map(FileOutputWebModel::getFilePath)
                    .collect(Collectors.toList());

            List<String> verificationVideo = mediaFilesService
                    .getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationVerificationVideo, p.getId())
                    .stream()
                    .map(FileOutputWebModel::getFilePath)
                    .collect(Collectors.toList());

            dto.setImageUrls(imageUrls);
            dto.setGovernmentIdUrls(govtIdUrls);
            dto.setVerificationVideo(verificationVideo);

            // ----------- REVIEWS -----------
            List<ShootingLocationPropertyReviewDTO> reviews =
                    propertyReviewRepository.findByPropertyId(p.getId())
                            .stream()
                            .map(r -> ShootingLocationPropertyReviewDTO.builder()
                                    .propertyId(r.getProperty().getId())
                                    .userId(r.getUser().getUserId())
                                    .rating(r.getRating())
                                    .reviewText(r.getReviewText())
                                    .userName(r.getUser().getName())
                                    .build())
                            .collect(Collectors.toList());

            dto.setReviews(reviews);

            // Average Rating
            double avgRating = reviews.stream()
                    .mapToInt(ShootingLocationPropertyReviewDTO::getRating)
                    .average()
                    .orElse(0.0);

            dto.setAverageRating(avgRating);

            // Add DTO to list
            dtoList.add(dto);
        }

        logger.info("Completed getAllProperties() - total properties fetched: {}", dtoList.size());
        return dtoList;

    } catch (Exception e) {
        logger.error("Exception occurred in getAllProperties(): ", e);
        return Collections.emptyList();
    }
}



	// Helper method to avoid null lists in DTOs
	private <T> List<T> defaultList(List<T> list) {
		return list == null ? Collections.emptyList() : list;
	}


	@Override
	public Response getPropertiesByUserId(Integer userId) {

		logger.info("Fetching properties for userId: {}", userId);

		try {
			List<ShootingLocationPropertyDetails> properties =
					propertyDetailsRepository.findAllByUserId(userId);

			// ⛔ No properties
			if (properties.isEmpty()) {
				return new Response(0, "No properties found for this user", Collections.emptyList());
			}

			// 1️⃣ PRELOAD LIKES
			List<PropertyLike> likes = likeRepository.findByLikedById(userId);
			Set<Integer> likedPropertyIds = likes.stream()
					.filter(PropertyLike::getStatus)
					.map(l -> l.getProperty().getId())
					.collect(Collectors.toSet());

			List<ShootingLocationPropertyDetailsDTO> result = new ArrayList<>();

			for (ShootingLocationPropertyDetails p : properties) {

				// 2️⃣ DTO using converter
				ShootingLocationPropertyDetailsDTO dto = shootingLocationPropertyConverter.entityToDto(p);

				// 3️⃣ liked info
				dto.setLikedByUser(likedPropertyIds.contains(p.getId()));
				dto.setLikeCount(likeRepository.countLikesByPropertyId(p.getId()));
				// 8️⃣ MEDIA FILES
				List<String> imageUrls = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationImage, p.getId())
						.stream().map(FileOutputWebModel::getFilePath)
						.collect(Collectors.toList());

				List<String> govtIdUrls = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingGovermentId, p.getId())
						.stream().map(FileOutputWebModel::getFilePath)
						.collect(Collectors.toList());

				dto.setImageUrls(imageUrls);
				dto.setGovernmentIdUrls(govtIdUrls);

				// 9️⃣ REVIEWS
				List<ShootingLocationPropertyReviewDTO> reviews =
						propertyReviewRepository.findByPropertyIdAndUser_UserId(p.getId(), userId)
						.stream()
						.map(r -> ShootingLocationPropertyReviewDTO.builder()
								.propertyId(r.getProperty().getId())
								.userId(r.getUser().getUserId())
								.rating(r.getRating())
								.reviewText(r.getReviewText())
								.userName(r.getUser().getName())
								.build())
						.collect(Collectors.toList());

				dto.setReviews(reviews);

				double avgRating =
						reviews.stream().mapToInt(ShootingLocationPropertyReviewDTO::getRating)
						.average().orElse(0);

				dto.setAverageRating(avgRating);

				result.add(dto);
			}

			return new Response(1, "Properties fetched successfully", result);

		} catch (Exception e) {
			logger.error("Error fetching user properties", e);
			return new Response(-1, "Error while fetching properties", null);
		}
	}


	//===========================================
	@Override
	public List<ShootingLocationPropertyDetailsDTO> getPropertiesByIndustryIds(List<Integer> industryIds, Integer userId) {

		logger.info("Fetching properties for industries: {}", industryIds);

		try {
			if (industryIds == null || industryIds.isEmpty()) {
				return Collections.emptyList();
			}

			// 1️⃣ Fetch properties
			List<ShootingLocationPropertyDetails> properties =
					propertyDetailsRepository.findAllActiveByIndustryIndustryId(industryIds);

			if (properties.isEmpty()) {
				return Collections.emptyList();
			}

			// 2️⃣ Preload user likes
			Set<Integer> likedPropertyIds = likeRepository.findByLikedById(userId)
					.stream()
					.filter(PropertyLike::getStatus)
					.map(l -> l.getProperty().getId())
					.collect(Collectors.toSet());

			// 3️⃣ Load availability for all properties
			List<Integer> propertyIds = properties.stream().map(ShootingLocationPropertyDetails::getId).toList();

			Map<Integer, List<PropertyAvailabilityDTO>> availabilityMap =
					availabilityRepository.findByPropertyIdIn(propertyIds).stream()
					.collect(Collectors.groupingBy(
							a -> a.getProperty().getId(),
							Collectors.mapping(a -> PropertyAvailabilityDTO.builder()
									.propertyId(a.getProperty().getId())
									.startDate(a.getStartDate())
									.endDate(a.getEndDate())
									.build(), Collectors.toList())
							));

			// 4️⃣ Load industry names
			Map<Integer, String> industryNameMap = industryRepository.findAllById(industryIds).stream()
					.collect(Collectors.toMap(Industry::getIndustryId, Industry::getIndustryName));

			List<ShootingLocationPropertyDetailsDTO> dtoList = new ArrayList<>();

			// 5️⃣ Convert each property
			for (ShootingLocationPropertyDetails p : properties) {

				// Base DTO from converter
				ShootingLocationPropertyDetailsDTO dto = shootingLocationPropertyConverter.entityToDto(p);

				// 6️⃣ Industry name
				if (p.getIndustry() != null) {
					dto.setIndustryName(industryNameMap.get(p.getIndustry().getIndustryId()));
				}

				// 7️⃣ Likes
				dto.setLikedByUser(likedPropertyIds.contains(p.getId()));
				dto.setLikeCount(likeRepository.countLikesByPropertyId(p.getId()));

				// 8️⃣ MEDIA FILES (your required style)

				List<String> imageUrls = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationImage, p.getId())
						.stream()
						.map(FileOutputWebModel::getFilePath)
						.collect(Collectors.toList());

				List<String> govtIdUrls = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingGovermentId, p.getId())
						.stream()
						.map(FileOutputWebModel::getFilePath)
						.collect(Collectors.toList());
				
				List<String> verificationVedio = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationVerificationVideo, p.getId())
						.stream()
						.map(FileOutputWebModel::getFilePath)
						.collect(Collectors.toList());

				dto.setImageUrls(imageUrls);
				dto.setGovernmentIdUrls(govtIdUrls);
				dto.setVerificationVideo(verificationVedio);

				// Add to final list
				dtoList.add(dto);
			}

			return dtoList;

		} catch (Exception e) {
			logger.error("Exception while fetching properties by industry IDs", e);
			return Collections.emptyList();
		}
	}



	@Override
	@Transactional
	public Response deletePropertyById(Integer id) {
		try {
			Integer userId = userDetails.userInfo().getId();

			ShootingLocationPropertyDetails property = propertyDetailsRepository.findById(id)
					.orElse(null);

			if (property == null) {
				logger.warn("Property with ID {} not found", id);
				return new Response(0, "Property not found with ID: " + id, null);
			}

			// ✅ Verify ownership
			if (property.getUser() == null || !property.getUser().getUserId().equals(userId)) {
				logger.warn("User {} is not authorized to delete property ID {}", userId, id);
				return new Response(0, "You are not authorized to delete this property", null);
			}

			// ✅ Soft delete
			property.setStatus(false);
			property.setUpdatedBy(userId);
			property.setUpdatedOn(LocalDateTime.now());

			propertyDetailsRepository.save(property);

			logger.info("Property ID {} soft deleted (status=false) by user ID {}", id, userId);
			return new Response(1, "Property deleted successfully", null);

		} catch (Exception e) {
			logger.error("Error deleting property with ID {}: {}", id, e.getMessage(), e);
			return new Response(0, "Something went wrong while deleting the property", null);
		}
	}


	@Transactional
	@Override
	public ShootingLocationPropertyDetailsDTO updatePropertyDetails(Integer id, ShootingLocationPropertyDetailsDTO dto, ShootingLocationFileInputModel inputFile) {

		try {
			if (id == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Property ID must not be null");
			}
			ShootingLocationPropertyDetails property = propertyDetailsRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Property not found with ID: " + id));


			logger.info("Updating property: {}", dto.getPropertyName());

			// --- Update main entity fields ---
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
			property.setNumberOfPeopleAllowed(dto.getNumberOfPeopleAllowed());
			property.setTotalArea(dto.getTotalArea());
			property.setSelectedUnit(dto.getSelectedUnit());
			property.setNumberOfRooms(dto.getNumberOfRooms());
			property.setNumberOfFloor(dto.getNumberOfFloor());
			property.setCeilingHeight(dto.getCeilingHeight());
			property.setGovtLicenseAndPermissions(dto.getGovtLicenseAndPermissions());

			property.setOutdoorFeatures(dto.getOutdoorFeatures());
			property.setArchitecturalStyle(dto.getArchitecturalStyle());
			property.setVintage(dto.getVintage());
			property.setIndustrial(dto.getIndustrial());
			property.setTraditional(dto.getTraditional());

			property.setPowerSupply(dto.getPowerSupply());
			property.setBakupGenerators(dto.getBakupGenerators());
			property.setVoltageCapacity(dto.getVoltageCapacity());
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

			property.setRoadAccessAndCondition(dto.getRoadAccessAndCondition());
			property.setPublicTransport(dto.getPublicTransport());
			property.setNearestAirportOrRailway(dto.getNearestAirportOrRailway());
			property.setAccommodationNearby(dto.getAccommodationNearby());
			property.setFoodAndCatering(dto.getFoodAndCatering());
			property.setEmergencyServicesNearby(dto.getEmergencyServicesNearby());

			property.setRentalCost(dto.getRentalCost());
			property.setSecurityDeposit(dto.getSecurityDeposit());
			property.setAdditionalCharges(dto.getAdditionalCharges());
			property.setPaymentModelsAccepted(dto.getPaymentModelsAccepted());
			property.setCancellationPolicy(dto.getCancellationPolicy());

			property.setDescription(dto.getDescription());

			property.setBusinessOwner(dto.isBusinessOwner());
			property.setUpdatedOn(LocalDateTime.now());
			property.setUpdatedBy(dto.getUserId());
			property.setTypeLocation(dto.getTypeLocation());
			property.setLocationLink(dto.getLocationLink());

			// --- Update Category/SubCategory/Type/User/Industry ---
			if (dto.getCategoryId() != null) {
				ShootingLocationCategory category = new ShootingLocationCategory();
				category.setId(dto.getCategoryId());

			}

			if (dto.getSubCategoryId() != null) {
				ShootingLocationSubcategory subCategory = new ShootingLocationSubcategory();
				subCategory.setId(dto.getSubCategoryId());

			}

			if (dto.getTypesId() != null) {
				ShootingLocationTypes type = new ShootingLocationTypes();
				type.setId(dto.getTypesId());

			}

			if (dto.getUserId() != null) {
				User user = new User();
				user.setUserId(dto.getUserId());

			}

			if (dto.getIndustryId() != null) {
				Industry industry = industryRepository.findById(dto.getIndustryId())
						.orElseThrow(() -> new RuntimeException("Industry not found"));
				property.setIndustry(industry);
			}

			// --- Update Subcategory Selection ---
			if (dto.getSubcategorySelectionDTO() != null) {
				property.setSubcategorySelection(mapToEntity(dto.getSubcategorySelectionDTO()));
			}

			ShootingLocationPropertyDetails updatedProperty = propertyDetailsRepository.save(property);

			// --- Business Info ---
			if (dto.getBusinessInformation() != null) {
				ShootingLocationBusinessInformation business = businessInformationRepository.findByPropertyDetails(updatedProperty)
						.orElse(new ShootingLocationBusinessInformation());
				business.setPropertyDetails(updatedProperty);
				business.setBusinessName(dto.getBusinessInformation().getBusinessName());
				business.setBusinessType(dto.getBusinessInformation().getBusinessType());
				business.setBusinessLocation(dto.getBusinessInformation().getBusinessLocation());
				business.setPanOrGSTNumber(dto.getBusinessInformation().getPanOrGSTNumber());
				business.setLocation(dto.getBusinessInformation().getLocation());
				business.setAddressLine1(dto.getBusinessInformation().getAddressLine1());
				business.setAddressLine2(dto.getBusinessInformation().getAddressLine2());
				business.setAddressLine3(dto.getBusinessInformation().getAddressLine3());
				business.setState(dto.getBusinessInformation().getState());
				business.setPostalCode(dto.getBusinessInformation().getPostalCode());
				businessInformationRepository.save(business);
			}

			// --- Bank Info ---
			if (dto.getBankDetailsDTO() != null) {
				ShootingLocationOwnerBankDetails bank = bankDetailsRepository.findByPropertyDetails(updatedProperty)
						.orElse(new ShootingLocationOwnerBankDetails());
				bank.setPropertyDetails(updatedProperty);
				bank.setBeneficiaryName(dto.getBankDetailsDTO().getBeneficiaryName());
				bank.setMobileNumber(dto.getBankDetailsDTO().getMobileNumber());
				bank.setAccountNumber(dto.getBankDetailsDTO().getAccountNumber());
				bank.setConfirmAccountNumber(dto.getBankDetailsDTO().getConfirmAccountNumber());
				bank.setIfscCode(dto.getBankDetailsDTO().getIfscCode());
				bankDetailsRepository.save(bank);
			}

			if (inputFile != null) {
				String updateMode = inputFile.getUpdateMode();
				boolean isReplace = "REPLACE".equalsIgnoreCase(updateMode);
				boolean isAppend = "APPEND".equalsIgnoreCase(updateMode);

				if (!isReplace && !isAppend) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid update mode. Use APPEND or REPLACE.");
				}

//				if (isReplace) {
//					List<ShootingLocationImages> oldFiles = shootingLocationImagesRepository.findByProperty(property);
//					for (ShootingLocationImages media : oldFiles) {
//						s3Util.deleteFileFromS3(media.getFilePath());
//					}
//					shootingLocationImagesRepository.deleteAllByProperty(property);
//				}

				//				// Upload new media
				//				Map<ShootingLocationImages, MultipartFile> mediaFilesMap = prepareMediaFileData(dto, inputFile, property.getUser(), property);
				//				for (Map.Entry<ShootingLocationImages, MultipartFile> entry : mediaFilesMap.entrySet()) {
				//					ShootingLocationImages media = entry.getKey();
				//					MultipartFile file = entry.getValue();
				//					shootingLocationImagesRepository.save(media);
				//					FileOutputWebModel uploaded = uploadToS3(file, media);
				//					if (uploaded != null) {
				//						logger.info("Uploaded file: {}", uploaded.getFilePath());
				//					}
				//				}
			}

			propertyDetailsRepository.save(property);
			return dto;

		} catch (Exception e) {
			logger.error("Error updating property details", e);
			throw new RuntimeException("Failed to update property", e);
		}
	}

	@Override
	public String toggleLike(Integer propertyId, Integer userId) {
		ShootingLocationPropertyDetails property = propertyDetailsRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found"));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Optional<PropertyLike> existingLike = likeRepository.findByPropertyAndLikedBy(property, user);

		if (existingLike.isPresent()) {
			likeRepository.delete(existingLike.get());
			return "Unliked successfully";
		} else {
			PropertyLike like = PropertyLike.builder()
					.property(property)
					.likedBy(user)
					.status(true)
					.createdBy(userId)
					.build();
			likeRepository.save(like);
			return "Liked successfully";
		}
	}

	
	@Override
public List<ShootingLocationPropertyDetailsDTO> getPropertiesLikedByUser(Integer userId) {

    logger.info("Fetching liked properties for userId: {}", userId);

    try {

        // 1. Fetch liked properties (only where status = true)
        List<PropertyLike> likedList = likeRepository.findByLikedById(userId)
                .stream()
                .filter(PropertyLike::getStatus)
                .collect(Collectors.toList());

        if (likedList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ShootingLocationPropertyDetails> properties =
                likedList.stream()
                        .map(PropertyLike::getProperty)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        if (properties.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Load availability in BATCH
        List<Integer> propertyIds = properties.stream()
                .map(ShootingLocationPropertyDetails::getId)
                .toList();

        Map<Integer, List<PropertyAvailabilityDTO>> availabilityMap =
                availabilityRepository.findByPropertyIdIn(propertyIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                a -> a.getProperty().getId(),
                                Collectors.mapping(a -> PropertyAvailabilityDTO.builder()
                                        .propertyId(a.getProperty().getId())
                                        .startDate(a.getStartDate())
                                        .endDate(a.getEndDate())
                                        .build(), Collectors.toList())
                        ));

        List<ShootingLocationPropertyDetailsDTO> result = new ArrayList<>();

        // 3. Loop each liked property
        for (ShootingLocationPropertyDetails property : properties) {

            Integer pid = property.getId();

            // A) Convert using converter (main mapping)
            ShootingLocationPropertyDetailsDTO dto =
                    shootingLocationPropertyConverter.entityToDto(property);

            // B) Add LIKE details
            dto.setLikedByUser(true);
            dto.setLikeCount(likeRepository.countLikesByPropertyId(pid));

            // C) Add industry name
            dto.setIndustryName(
                    property.getIndustry() != null ? property.getIndustry().getIndustryName() : null
            );

            // D) Add MEDIA FILES
            dto.setImageUrls(
                    mediaFilesService.getMediaFilesByCategoryAndRefId(
                                    MediaFileCategory.shootingLocationImage, pid)
                            .stream().map(FileOutputWebModel::getFilePath).toList()
            );

            dto.setGovernmentIdUrls(
                    mediaFilesService.getMediaFilesByCategoryAndRefId(
                                    MediaFileCategory.shootingGovermentId, pid)
                            .stream().map(FileOutputWebModel::getFilePath).toList()
            );

            dto.setVerificationVideo(
                    mediaFilesService.getMediaFilesByCategoryAndRefId(
                                    MediaFileCategory.shootingLocationVerificationVideo, pid)
                            .stream().map(FileOutputWebModel::getFilePath).toList()
            );

            // E) Add REVIEWS
            List<ShootingLocationPropertyReviewDTO> reviews =
                    propertyReviewRepository.findByPropertyId(pid)
                            .stream()
                            .map(r -> ShootingLocationPropertyReviewDTO.builder()
                                    .propertyId(r.getProperty().getId())
                                    .userId(r.getUser().getUserId())
                                    .rating(r.getRating())
                                    .reviewText(r.getReviewText())
                                    .userName(r.getUser().getName())
                                    .build())
                            .toList();

            dto.setReviews(reviews);
            dto.setAverageRating(
                    reviews.stream().mapToInt(ShootingLocationPropertyReviewDTO::getRating)
                            .average().orElse(0.0)
            );

            // F) Add AVAILABILITY
            dto.setAvailabilityDates(
                    availabilityMap.getOrDefault(pid, Collections.emptyList())
            );

            // Add final DTO to list
                    result.add(dto);
        }

        logger.info("Total liked properties returned: {}", result.size());
        return result;

    } catch (Exception ex) {
        logger.error("Error in getPropertiesLikedByUser: {}", ex.getMessage(), ex);
        return Collections.emptyList();
    }
}



	public Long countLikes(Integer propertyId) {
		ShootingLocationPropertyDetails property = propertyDetailsRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found"));
		return likeRepository.countByProperty(property);
	}

	@Override
	public ShootingLocationPropertyReviewDTO saveReview(
			Integer propertyId,
			Integer userId,
			int rating,
			String reviewText,
			List<MultipartFile> files) {

		// 1️⃣ Fetch property and user
		ShootingLocationPropertyDetails property = propertyDetailsRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found"));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// 2️⃣ Create and save the review
		ShootingLocationPropertyReview review = ShootingLocationPropertyReview.builder()
				.property(property)
				.user(user)
				.rating(rating)
				.reviewText(reviewText)
				.build();

		ShootingLocationPropertyReview savedReview = propertyReviewRepository.save(review);

		// Save optional photos using your existing media service
		if (!Utility.isNullOrEmptyList(files)) {
			FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.ShootingLocationReview) 
					.categoryRefId(savedReview.getId())  
					.files(files)
					.build();

			mediaFilesService.saveMediaFiles(fileInputWebModel, user);
		}

		// 4️⃣ Convert to DTO
		return ShootingLocationPropertyReviewDTO.builder()
				.id(savedReview.getId())
				.propertyId(propertyId)
				.userId(userId)
				.rating(rating)
				.reviewText(reviewText)
				.userName(user.getName())
				.createdOn(savedReview.getCreatedOn())
				.build();
	}

	public double getAverageRating(Integer propertyId) {
		List<ShootingLocationPropertyReview> reviews = propertyReviewRepository.findByPropertyId(propertyId);
		return reviews.stream()
				.mapToInt(ShootingLocationPropertyReview::getRating)
				.average()
				.orElse(0.0);
	}

	@Override
	public ShootingLocationPropertyReviewResponseDTO getReviewsByPropertyId(Integer propertyId) {
		List<ShootingLocationPropertyReview> reviews = propertyReviewRepository.findByPropertyId(propertyId)
				.stream()
				.sorted(Comparator.comparing(ShootingLocationPropertyReview::getCreatedOn).reversed())
				.collect(Collectors.toList());

		if (reviews.isEmpty()) {
			return ShootingLocationPropertyReviewResponseDTO.builder()
					.reviews(Collections.emptyList())
					.averageRating(0.0)
					.totalReviews(0)
					.fiveStarPercentage(0.0)
					.fourStarPercentage(0.0)
					.threeStarPercentage(0.0)
					.twoStarPercentage(0.0)
					.oneStarPercentage(0.0)
					.build();
		}

		// ✅ Calculate rating status
		long totalReviews = reviews.size();
		double averageRating = reviews.stream()
				.mapToInt(ShootingLocationPropertyReview::getRating)
				.average()
				.orElse(0.0);

		long fiveStar = reviews.stream().filter(r -> r.getRating() == 5).count();
		long fourStar = reviews.stream().filter(r -> r.getRating() == 4).count();
		long threeStar = reviews.stream().filter(r -> r.getRating() == 3).count();
		long twoStar = reviews.stream().filter(r -> r.getRating() == 2).count();
		long oneStar = reviews.stream().filter(r -> r.getRating() == 1).count();

		// ✅ Convert counts to percentages
		double fiveStarPercentage = (fiveStar * 100.0) / totalReviews;
		double fourStarPercentage = (fourStar * 100.0) / totalReviews;
		double threeStarPercentage = (threeStar * 100.0) / totalReviews;
		double twoStarPercentage = (twoStar * 100.0) / totalReviews;
		double oneStarPercentage = (oneStar * 100.0) / totalReviews;

		// ✅ Map reviews to DTO
		List<ShootingLocationPropertyReviewDTO> reviewDTOs = reviews.stream()
				.map(review -> {
					List<FileOutputWebModel> files = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.ShootingLocationReview, review.getId())
							.stream()
							.sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
							.collect(Collectors.toList());

					return ShootingLocationPropertyReviewDTO.builder()
							.id(review.getId())
							.propertyId(review.getProperty().getId())
							.userId(review.getUser().getUserId())
							.rating(review.getRating())
							.reviewText(review.getReviewText())
							.profilePicUrl(userService.getProfilePicUrl(review.getUser().getUserId()))
							.userName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
							.createdOn(review.getCreatedOn())
							.files(files)
							.build();
				})
				.collect(Collectors.toList());

		// ✅ Return combined response
		return ShootingLocationPropertyReviewResponseDTO.builder()
				.reviews(reviewDTOs)
				.averageRating(Math.round(averageRating * 10.0) / 10.0)
				.totalReviews(totalReviews) // total number of reviews for the property
				.fiveStarPercentage(Math.round(fiveStarPercentage * 10.0) / 10.0)
				.fourStarPercentage(Math.round(fourStarPercentage * 10.0) / 10.0)
				.threeStarPercentage(Math.round(threeStarPercentage * 10.0) / 10.0)
				.twoStarPercentage(Math.round(twoStarPercentage * 10.0) / 10.0)
				.oneStarPercentage(Math.round(oneStarPercentage * 10.0) / 10.0)
				.build();
	}


	@Override
	public PropertyAvailabilityDTO saveAvailability(PropertyAvailabilityDTO dto) {
		ShootingLocationPropertyDetails property = propertyDetailsRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new RuntimeException("Property not found"));

		// Fetch existing availability (you expect only one per property)
		List<PropertyAvailabilityDate> existingList = availabilityRepository.findByProperty_Id(dto.getPropertyId());

		PropertyAvailabilityDate availability;
		if (!existingList.isEmpty()) {
			// Update the first one (you may choose latest if multiple exist)
			availability = existingList.get(0);
			availability.setStartDate(dto.getStartDate());
			availability.setEndDate(dto.getEndDate());
		} else {
			// Create new
			availability = PropertyAvailabilityDate.builder()
					.property(property)
					.startDate(dto.getStartDate())
					.endDate(dto.getEndDate())
					.build();
		}

		PropertyAvailabilityDate saved = availabilityRepository.save(availability);

		return PropertyAvailabilityDTO.builder()
				.propertyId(saved.getProperty().getId())
				.startDate(saved.getStartDate())
				.endDate(saved.getEndDate())
				.build();
	}

	@Override
	public List<PropertyAvailabilityDTO> getAvailabilityByPropertyId(Integer propertyId) {
		return availabilityRepository.findByPropertyId(propertyId).stream()
				.map(a -> PropertyAvailabilityDTO.builder()
						.propertyId(a.getProperty().getId())
						.startDate(a.getStartDate())
						.endDate(a.getEndDate())
						.build())
				.collect(Collectors.toList());
	}
	@Override
	public void updateAvailabilityDates(Integer propertyId, List<PropertyAvailabilityDTO> availabilityList) {
		// Step 1: Fetch property
		ShootingLocationPropertyDetails property = propertyDetailsRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("❌ Property not found with id: " + propertyId));

		// Step 2: Delete old dates
		availabilityRepository.deleteByPropertyId(propertyId);

		// Step 3: Save new availability
		List<PropertyAvailabilityDate> newDates = availabilityList.stream()
				.map(dto -> PropertyAvailabilityDate.builder()
						.startDate(dto.getStartDate())
						.endDate(dto.getEndDate())
						.property(property)
						.build())
				.collect(Collectors.toList());

		availabilityRepository.saveAll(newDates);
	}

	@Override
	public ShootingLocationPropertyDetailsDTO getPropertyByBookingId(Integer bookingId) {
		logger.info("Starting getPropertyByBookingId() - fetching property for bookingId: {}", bookingId);

		try {
			// 1. Get booking record
			ShootingLocationBooking booking = bookingRepository.findById(bookingId)
					.orElseThrow(() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND, 
							"Booking not found with ID: " + bookingId
							));

			// 2. Get property linked to booking
			ShootingLocationPropertyDetails property = Optional.ofNullable(booking.getProperty())
					.flatMap(prop -> propertyDetailsRepository.findById(prop.getId()))
					.orElseThrow(() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND,
							"Property not found for bookingId: " + bookingId
							));

			Integer propertyId = property.getId();
			Integer userId = property.getUser() != null ? property.getUser().getUserId() : null;

			// 3. Fetch related IDs safely
			Set<Integer> categoryIds = property.getCategory() != null
					? Collections.singleton(property.getCategory().getId()) : Collections.emptySet();
			Set<Integer> subcategoryIds = property.getSubCategory() != null
					? Collections.singleton(property.getSubCategory().getId()) : Collections.emptySet();
			Set<Integer> typeIds = property.getTypes() != null
					? Collections.singleton(property.getTypes().getId()) : Collections.emptySet();

			// 4. Fetch related entities
			Map<Integer, ShootingLocationCategory> categoryMap = categoryRepo.findAllById(categoryIds)
					.stream().collect(Collectors.toMap(ShootingLocationCategory::getId, c -> c));
			Map<Integer, ShootingLocationSubcategory> subcategoryMap = subcategoryRepo.findAllById(subcategoryIds)
					.stream().collect(Collectors.toMap(ShootingLocationSubcategory::getId, c -> c));
			Map<Integer, ShootingLocationTypes> typesMap = typesRepo.findAllById(typeIds)
					.stream().collect(Collectors.toMap(ShootingLocationTypes::getId, c -> c));

			// 5. Likes info
			Set<Integer> likedPropertyIds = (userId != null)
					? likeRepository.findByLikedById(userId).stream()
							.filter(PropertyLike::getStatus)
							.map(like -> like.getProperty().getId())
							.collect(Collectors.toSet())
							: Collections.emptySet();

			boolean likeStatus = likedPropertyIds.contains(propertyId);
			int likeCount = likeRepository.countLikesByPropertyId(propertyId);

			// 6. Business info
			BusinessInformationDTO businessInfoDTO = null;
			if (property.getBusinessInformation() != null) {
				ShootingLocationBusinessInformation b = property.getBusinessInformation();
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

			// 7. Bank details
			BankDetailsDTO bankDetailsDTO = null;
			if (property.getBankDetails() != null) {
				ShootingLocationOwnerBankDetails bank = property.getBankDetails();
				bankDetailsDTO = BankDetailsDTO.builder()
						.beneficiaryName(bank.getBeneficiaryName())
						.mobileNumber(bank.getMobileNumber())
						.accountNumber(bank.getAccountNumber())
						.confirmAccountNumber(bank.getConfirmAccountNumber())
						.ifscCode(bank.getIfscCode())
						.build();
			}

			// 8. Category DTOs
			ShootingLocationCategoryDTO categoryDTO = (property.getCategory() != null && categoryMap.containsKey(property.getCategory().getId()))
					? ShootingLocationCategoryDTO.builder()
							.id(property.getCategory().getId())
							.name(categoryMap.get(property.getCategory().getId()).getName())
							.build()
							: null;

			ShootingLocationSubcategoryDTO subcategoryDTO = (property.getSubCategory() != null && subcategoryMap.containsKey(property.getSubCategory().getId()))
					? ShootingLocationSubcategoryDTO.builder()
							.id(property.getSubCategory().getId())
							.name(subcategoryMap.get(property.getSubCategory().getId()).getName())
							.description(subcategoryMap.get(property.getSubCategory().getId()).getDescription())
							.imageUrl(subcategoryMap.get(property.getSubCategory().getId()).getImageUrl())
							.build()
							: null;

			ShootingLocationTypeDTO typeDTO = (property.getTypes() != null && typesMap.containsKey(property.getTypes().getId()))
					? ShootingLocationTypeDTO.builder()
							.id(property.getTypes().getId())
							.name(typesMap.get(property.getTypes().getId()).getName())
							.description(typesMap.get(property.getTypes().getId()).getDescription())
							.build()
							: null;

			ShootingLocationSubcategorySelectionDTO subcategorySelectionDTO = property.getSubcategorySelection() != null
					? ShootingLocationSubcategorySelectionDTO.builder()
							.subcategoryId(property.getSubcategorySelection().getId())
							.entireProperty(property.getSubcategorySelection().getEntireProperty())
							.singleProperty(property.getSubcategorySelection().getSingleProperty())
							.build()
							: null;

			// 9. Media files
//			List<String> imageUrls = new ArrayList<>();
//			List<String> videoUrls = new ArrayList<>();
//			List<String> governmentIdUrls = new ArrayList<>();
//			if (property.getMediaFiles() != null) {
//				for (ShootingLocationImages file : property.getMediaFiles()) {
//					String cat = file.getCategory();
//					if ("shootingLocationImage".equalsIgnoreCase(cat)) {
//						imageUrls.add(file.getFilePath());
//					} else if ("Video".equalsIgnoreCase(cat)) {
//						videoUrls.add(file.getFilePath());
//					} else if ("govermentId".equalsIgnoreCase(cat)) {
//						governmentIdUrls.add(file.getFilePath());
//					}
//				}
//			}

			List<PropertyAvailabilityDTO> availabilityDates = availabilityRepository.findByPropertyId(propertyId)
					.stream()
					.map(avail -> PropertyAvailabilityDTO.builder()
							.propertyId(propertyId)
							.startDate(avail.getStartDate())
							.endDate(avail.getEndDate())
							.build())
					.collect(Collectors.toList());

			// 10. Reviews
			List<ShootingLocationPropertyReviewDTO> reviews = propertyReviewRepository.findByPropertyId(propertyId)
					.stream()
					.map(review -> ShootingLocationPropertyReviewDTO.builder()
							.propertyId(propertyId)
							.userId(review.getUser() != null ? review.getUser().getUserId() : null)
							.rating(review.getRating())
							.reviewText(review.getReviewText())
							.userName(review.getUser() != null ? review.getUser().getName() : null)
							.build())
					.collect(Collectors.toList());

			double avgRating = reviews.stream()
					.mapToInt(ShootingLocationPropertyReviewDTO::getRating)
					.average()
					.orElse(0.0);

			// 11. Build final DTO
			ShootingLocationPropertyDetailsDTO dto = ShootingLocationPropertyDetailsDTO.builder()
					.id(propertyId)
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
					.numberOfPeopleAllowed(property.getNumberOfPeopleAllowed())
					.totalArea(property.getTotalArea())
					.selectedUnit(property.getSelectedUnit())
					.numberOfRooms(property.getNumberOfRooms())
					.numberOfFloor(property.getNumberOfFloor())
					.ceilingHeight(property.getCeilingHeight())
					.outdoorFeatures(property.getOutdoorFeatures())
					.architecturalStyle(property.getArchitecturalStyle())
					.vintage(property.getVintage())
					.industrial(property.getIndustrial())
					.traditional(property.getTraditional())
					.powerSupply(property.getPowerSupply())
					.bakupGenerators(property.getBakupGenerators())
					.voltageCapacity(property.getVoltageCapacity())
					.wifi(property.getWifi())
					.airConditionAndHeating(property.getAirConditionAndHeating())
					.numberOfWashrooms(property.getNumberOfWashrooms())
					.restrooms(property.getRestrooms())
					.waterSupply(property.getWaterSupply())
					.changingRooms(property.getChangingRooms())
					.kitchen(property.getKitchen())
					.furnitureAndProps(property.getFurnitureAndProps())
					.neutralLightingConditions(property.getNeutralLightingConditions())
					.artificialLightingAvailability(property.getArtificialLightingAvailability())
					.parkingCapacity(property.getParkingCapacity())
					.droneUsage(property.getDroneUsage())
					.firearms(property.getFirearms())
					.actionScenes(property.getActionScenes())
					.security(property.getSecurity())
					.structuralModification(property.getStructuralModification())
					.temporary(property.getTemporary())
					.dressing(property.getDressing())
					.permissions(property.getPermissions())
					.noiseRestrictions(property.getNoiseRestrictions())
					.shootingTiming(property.getShootingTiming())
					.insuranceRequired(property.getInsuranceRequired())
					.legalAgreements(property.getLegalAgreements())
					.govtLicenseAndPermissions(property.getGovtLicenseAndPermissions())
					.roadAccessAndCondition(property.getRoadAccessAndCondition())
					.publicTransport(property.getPublicTransport())
					.nearestAirportOrRailway(property.getNearestAirportOrRailway())
					.accommodationNearby(property.getAccommodationNearby())
					.foodAndCatering(property.getFoodAndCatering())
					.emergencyServicesNearby(property.getEmergencyServicesNearby())
					.rentalCost(property.getRentalCost())
					.securityDeposit(property.getSecurityDeposit())
					.additionalCharges(property.getAdditionalCharges())
					.paymentModelsAccepted(property.getPaymentModelsAccepted())
					.cancellationPolicy(property.getCancellationPolicy())
					.description(property.getDescription())
					.businessOwner(property.isBusinessOwner())
					.businessInformation(businessInfoDTO)
					.bankDetailsDTO(bankDetailsDTO)
					.subcategorySelectionDTO(subcategorySelectionDTO)
					.category(categoryDTO)
					.subCategory(subcategoryDTO)
					.type(typeDTO)
//					.imageUrls(imageUrls)
//					.verificationVideo(videoUrls)
//					.governmentIdUrls(governmentIdUrls)
					.likedByUser(likeStatus)
					.reviews(reviews)
					.averageRating(avgRating)
					.typeLocation(property.getTypeLocation())
					.locationLink(property.getLocationLink())
					.likeCount(likeCount)
					.industryName(property.getIndustry() != null ? property.getIndustry().getIndustryName() : null)
					.industryId(property.getIndustry() != null ? property.getIndustry().getIndustryId() : null)
					.categoryId(property.getCategory() != null ? property.getCategory().getId() : null)
					.subCategoryId(property.getSubCategory() != null ? property.getSubCategory().getId() : null)
					.typesId(property.getTypes() != null ? property.getTypes().getId() : null)
					.availabilityDates(availabilityDates)
					.userId(userId)
					.build();

			logger.info("Completed getPropertyByBookingId() - property fetched for bookingId: {}", bookingId);
			return dto;

		} catch (Exception e) {
			logger.error("Exception occurred in getPropertyByBookingId(): ", e);
			throw e;
		}
	}
	@Override
	@Transactional
	public ShootingLocationPropertyReviewDTO updateReview(
			Integer reviewId,
			Integer propertyId,
			Integer userId,
			int rating,
			String reviewText,
			List<MultipartFile> files,
			@Nullable List<Integer> deletedFileIds
			) {
		// 1️⃣ Fetch and validate review
		ShootingLocationPropertyReview review = propertyReviewRepository.findById(reviewId)
				.orElseThrow(() -> new RuntimeException("Review not found"));

		if (!review.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("You can only edit your own review");
		}

		if (propertyId != null && review.getProperty() != null
				&& !Objects.equals(review.getProperty().getId(), propertyId)) {
			throw new IllegalArgumentException("Review does not belong to the given propertyId");
		}

		// 2️⃣ Update review text/rating
		review.setRating(rating);
		review.setReviewText(reviewText);
		review.setUpdatedOn(LocalDateTime.now());
		propertyReviewRepository.save(review);

		// 3️⃣ Get user
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// 🔹 1️⃣ Delete specific old files if user removed any
		if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
			logger.info("Deleting review files for review {}: {}", review.getId(), deletedFileIds);
			mediaFilesService.deleteMediaFilesByCategoryAndIds(
					MediaFileCategory.ShootingLocationReview,
					deletedFileIds
					);
		}

		// 🔹 2️⃣ Upload new files if provided
		if (files != null && !files.isEmpty()) {
			FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.ShootingLocationReview)
					.categoryRefId(review.getId())
					.files(files)
					.build();

			mediaFilesService.saveMediaFiles(fileInputWebModel, user);
			logger.info("Uploaded {} new files for review {}", files.size(), review.getId());
		}

		// 🔹 3️⃣ Build response using mediaFilesService helper (not repository)
		List<FileOutputWebModel> mediaDTOs = mediaFilesService.getMediaFilesByCategoryAndRefId(
				MediaFileCategory.ShootingLocationReview,
				review.getId()
				);

		// 🔹 4️⃣ Return DTO
		return ShootingLocationPropertyReviewDTO.builder()
				.id(review.getId())
				.propertyId(propertyId)
				.userId(userId)
				.rating(review.getRating())
				.reviewText(review.getReviewText())
				.createdOn(review.getCreatedOn())

				.files(mediaDTOs)
				.build();
	}



	@Override
	public String deleteReview(Integer reviewId, Integer userId) {
		ShootingLocationPropertyReview review = propertyReviewRepository.findById(reviewId)
				.orElseThrow(() -> new RuntimeException("Review not found"));

		if (!review.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("You are not authorized to delete this review");
		}

		mediaFilesService.deleteMediaFilesByCategoryAndRefIds(
				MediaFileCategory.ShootingLocationReview,
				List.of(review.getId())
				);

		propertyReviewRepository.delete(review);

		return "Your review has been deleted successfully";
	}

	@Scheduled(cron = "0 25 10 * * *")
 @Transactional
	    public void expireDiscountsDaily() {
	        LocalDateTime now = LocalDateTime.now();

	        List<ShootingLocationSubcategorySelection> selections = selectionRepo.findAllWithAnyDiscountEnabled();
	        for (ShootingLocationSubcategorySelection sel : selections) {
	            boolean changed = false;

	            if (Boolean.TRUE.equals(sel.isEntirePropertyDiscount20Percent()) && sel.getEntirePropertyDiscountStartDate() != null) {
	                LocalDateTime start = sel.getEntirePropertyDiscountStartDate();
	                long bookings = bookingRepo.countConfirmedBookingsByPropertySince(sel.getPropertyDetails().getId(), start);
	                sel.setEntirePropertyDiscountBookingCount((int) bookings);

	                boolean expiredByBookings = bookings >= 2;
	                boolean expiredByDate = start.plusDays(1).isBefore(now) || start.plusDays(1).isEqual(now);
	                if (expiredByBookings || expiredByDate) {
	                    sel.setEntirePropertyDiscount20Percent(false);
	                    sel.setEntirePropertyDiscountStartDate(null);
	                    sel.setEntirePropertyDiscountBookingCount(0);
	                    changed = true;
	                }
	            }

	            if (Boolean.TRUE.equals(sel.isSinglePropertyDiscount20Percent()) && sel.getSinglePropertyDiscountStartDate() != null) {
	                LocalDateTime start = sel.getSinglePropertyDiscountStartDate();
	                long bookings = bookingRepo.countConfirmedBookingsByPropertySince(sel.getPropertyDetails().getId(), start);
	                sel.setSinglePropertyDiscountBookingCount((int) bookings);

	                boolean expiredByBookings = bookings >= 2;
	                boolean expiredByDate = start.plusDays(1).isBefore(now) || start.plusDays(1).isEqual(now);
	                if (expiredByBookings || expiredByDate) {
	                    sel.setSinglePropertyDiscount20Percent(false);
	                    sel.setSinglePropertyDiscountStartDate(null);
	                    sel.setSinglePropertyDiscountBookingCount(0);
	                    changed = true;
	                }
	            }

	            if (changed) {
	                selectionRepo.save(sel);
	            }
	        }
	    }
	
	

}