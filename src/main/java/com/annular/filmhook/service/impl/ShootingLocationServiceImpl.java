package com.annular.filmhook.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.converter.ShootingLocationBookingConverter;
import com.annular.filmhook.converter.ShootingLocationConverter;
import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.FileStatus;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.PaymentModule;
import com.annular.filmhook.model.Payments;
import com.annular.filmhook.model.PropertyBookingType;
import com.annular.filmhook.model.PropertyLike;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationBusinessInformation;
import com.annular.filmhook.model.ShootingLocationCategory;
import com.annular.filmhook.model.ShootingLocationOwnerBankDetails;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.ShootingLocationPropertyReview;
import com.annular.filmhook.model.ShootingLocationSubcategory;
import com.annular.filmhook.model.ShootingLocationSubcategorySelection;
import com.annular.filmhook.model.ShootingLocationTypes;
import com.annular.filmhook.model.ShootingPropertyStatus;
import com.annular.filmhook.model.SlotType;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.BankDetailsRepository;
import com.annular.filmhook.repository.BusinessInformationRepository;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.repository.PaymentsRepository;
import com.annular.filmhook.repository.PropertyLikeRepository;
import com.annular.filmhook.repository.ShootingLocationBookingRepository;
import com.annular.filmhook.repository.ShootingLocationCategoryRepository;
import com.annular.filmhook.repository.ShootingLocationPropertyDetailsRepository;
import com.annular.filmhook.repository.ShootingLocationPropertyReviewRepository;
import com.annular.filmhook.repository.ShootingLocationSubcategoryRepository;
import com.annular.filmhook.repository.ShootingLocationSubcategorySelectionRepository;
import com.annular.filmhook.repository.ShootingLocationTypesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.security.PropertyCodeGenerator;
import com.annular.filmhook.service.AdminService;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.ShootingLocationService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.BankDetailsDTO;
import com.annular.filmhook.webmodel.BookingWithPropertyDTO;
import com.annular.filmhook.webmodel.BusinessInformationDTO;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PaymentsDTO;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewResponseDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertySummaryDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategorySelectionDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;
import com.annular.filmhook.webmodel.ShootingPaymentModel;
import com.annular.filmhook.webmodel.ShootingPropertyMediaRequest;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


@Service
public class ShootingLocationServiceImpl implements ShootingLocationService {

	public static final Logger logger = LoggerFactory.getLogger(ShootingLocationServiceImpl.class);
	private static final DeviceRgb BRAND_BLUE = new DeviceRgb(3, 169, 244);
	@Autowired
	private ShootingLocationTypesRepository typesRepo;

	@Autowired
	ShootingLocationService shootingLocationService;
	@Autowired
	private ShootingLocationBookingRepository bookingRepo;

	@Autowired
	private PropertyLikeRepository likeRepository;
	@Autowired
	private LikeRepository likesRepository;

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
	PaymentsServiceImpl paymentsServiceImpl;
	@Autowired
	PaymentsRepository paymentsRepository;
	@Autowired
	InAppNotificationRepository inAppNotificationRepo;
	@Autowired
	S3Util s3Util;
	@Autowired
	private  UserDetails userDetails;
	@Autowired
	ShootingLocationConverter shootingLocationPropertyConverter;
	@Autowired
	private BusinessInformationRepository businessInformationRepository;

	@Autowired
	private BankDetailsRepository bankDetailsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	IndustryRepository industryRepository;


	@Autowired
	private MailNotification mailNotification;
	@Autowired
	private UserService userService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private MediaFilesRepository mediaFilesRepository;

	@Autowired
	InAppNotificationRepository inAppNotificationRepository;

	@Autowired
	private ShootingLocationBookingConverter shootingLocationBookingConverter;

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

			if (dto.getId() == null) {

				String propertyCode;
				int attempts = 0;

				do {
					propertyCode = PropertyCodeGenerator.generate();
					attempts++;
				} while (propertyDetailsRepository.existsByPropertyCode(propertyCode) && attempts < 5);

				if (attempts == 5) {
					throw new RuntimeException("Unable to generate unique property code");
				}

				property.setPropertyCode(propertyCode);
			}
			property.setCategory(category);
			property.setSubCategory(subCategory);
			property.setTypes(type);

			if (type != null) {
				if (type.getId() == 1) {
					property.setStatus(ShootingPropertyStatus.PENDING);
				} else if (type.getId() == 2) {
					property.setStatus(ShootingPropertyStatus.ACTIVE);
				}
			}
			property.setUser(user);
			property.setIndustry(industry);

			property.setSubcategorySelection(mapToEntity(dto.getSubcategorySelectionDTO()));

			// ---------- SAVE AVAILABILITY & PAUSED DATES ----------
			if (dto.getAvailabilityStartDate() != null && dto.getAvailabilityEndDate() != null) {
				property.setAvailabilityStartDate(dto.getAvailabilityStartDate());
				property.setAvailabilityEndDate(dto.getAvailabilityEndDate());
			}

			// ---------- SAVE PAUSED DATES  ----------
			if (dto.getPausedDates() != null && !dto.getPausedDates().isEmpty()) {
				property.setPausedDates(dto.getPausedDates());
			} else {
				property.setPausedDates(null); 
			}


			// audit fields
			property.setCreatedBy(dto.getUserId());
			property.setCreatedOn(LocalDateTime.now());;

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
				if ( inputFile.getImages() != null 

						&& !inputFile.getImages().isEmpty()) {

					FileInputWebModel imagesInput = FileInputWebModel.builder()
							.userId(user.getUserId())
							.category(MediaFileCategory.shootingLocationImage)
							.categoryRefId(savedProperty.getId())
							.files(inputFile.getImages())
							.build();

					if (type.getId() == 1) {
						mediaFilesService.saveMediaFilesShootingProperty(imagesInput, user);
					} else {
						mediaFilesService.saveMediaFiles(imagesInput, user);
					}
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
				savedProperty.setSelfOwnedPropertyDocument(
						uploadDocument(inputFile.getSelfOwnedPropertyDocument(),
								MediaFileCategory.shootingPropertyDocuments,
								savedProperty.getId(), user));

				savedProperty.setMortgagePropertyDocument(
						uploadDocument(inputFile.getMortgagePropertyDocument(),
								MediaFileCategory.shootingPropertyDocuments,
								savedProperty.getId(), user));

				savedProperty.setOwnerPermittedDocument(
						uploadDocument(inputFile.getOwnerPermittedDocument(),
								MediaFileCategory.shootingPropertyDocuments,
								savedProperty.getId(), user));

				savedProperty.setPropertyDamageDocument(
						uploadDocument(inputFile.getPropertyDamageDocument(),
								MediaFileCategory.shootingPropertyDocuments,
								savedProperty.getId(), user));

				savedProperty.setCrewAccidentDocument(
						uploadDocument(inputFile.getCrewAccidentDocument(),
								MediaFileCategory.shootingPropertyDocuments,
								savedProperty.getId(), user));


				propertyDetailsRepository.saveAndFlush(savedProperty);
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
				.entirePropertyDayDiscountPercent(dto.getEntirePropertyDayDiscountPercent())
				.entirePropertyNightDiscountPercent(dto.getEntirePropertyNightDiscountPercent())
				.entirePropertyFullDayDiscountPercent(dto.getEntirePropertyFullDayDiscountPercent())
				.singlePropertyDayDiscountPercent(dto.getSinglePropertyDayDiscountPercent())
				.singlePropertyNightDiscountPercent(dto.getSinglePropertyNightDiscountPercent())
				.singlePropertyFullDayDiscountPercent(dto.getSinglePropertyFullDayDiscountPercent())
				.build();
		return sel;
	}

	private String uploadDocument(MultipartFile file, MediaFileCategory category, Integer propertyId, User user) {

		if (file == null || file.isEmpty()) {
			return null;
		}

		FileInputWebModel input = FileInputWebModel.builder()
				.userId(user.getUserId())
				.category(category)
				.categoryRefId(propertyId)
				.files(List.of(file))
				.build();

		List<FileOutputWebModel> result = mediaFilesService.saveMediaFiles(input, user);

		if (result == null || result.isEmpty()) {
			return null;  // Or log warning: upload failed
		}

		return result.get(0).getFilePath();  // Safe now
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

				//				dto.setImageUrls(imageUrls);
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


	@Override
	public Response getPropertiesByUserId(Integer userId) {

		logger.info("Fetching properties for userId: {}", userId);

		try {
			List<ShootingPropertyStatus> allowedStatuses =
					Arrays.asList(
							ShootingPropertyStatus.APPROVED,
							ShootingPropertyStatus.PENDING,
							ShootingPropertyStatus.REJECTED
							);

			List<ShootingLocationPropertyDetails> properties =
					propertyDetailsRepository.findAllByUser_UserIdAndStatusIn(userId, allowedStatuses);

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

				Integer propertyId = p.getId();

				// 2️⃣ Base DTO mapping
				ShootingLocationPropertyDetailsDTO dto =
						shootingLocationPropertyConverter.entityToDto(p);

				// 3️⃣ Like info
				dto.setLikedByUser(likedPropertyIds.contains(propertyId));
				dto.setLikeCount(likeRepository.countLikesByPropertyId(propertyId));

				// 4️⃣ Media files
				List<FileOutputWebModel> imageUrls = mediaFilesService
						.getAllMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationImage, p.getId());
				dto.setImageUrls(imageUrls);

				dto.setGovernmentIdUrls(
						mediaFilesService
						.getMediaFilesByCategoryAndRefId(
								MediaFileCategory.shootingGovermentId, propertyId)
						.stream()
						.map(FileOutputWebModel::getFilePath)
						.collect(Collectors.toList())
						);

				// 5️⃣ REVIEWS + RATINGS (✅ USE YOUR METHOD)
				ShootingLocationPropertyReviewResponseDTO reviewResponse =
						getReviewsByPropertyId(propertyId, userId);

				dto.setReviews(reviewResponse.getReviews());
				dto.setAverageRating(reviewResponse.getAverageRating());
				dto.setTotalReviews(reviewResponse.getTotalReviews());
				dto.setFiveStarPercentage(reviewResponse.getFiveStarPercentage());
				dto.setFourStarPercentage(reviewResponse.getFourStarPercentage());
				dto.setThreeStarPercentage(reviewResponse.getThreeStarPercentage());
				dto.setTwoStarPercentage(reviewResponse.getTwoStarPercentage());
				dto.setOneStarPercentage(reviewResponse.getOneStarPercentage());

				// 6️⃣ Admin rating (if added)
				dto.setAdminRating(p.getAdminRating());
				dto.setAdminRatedOn(p.getAdminRatedOn());
				dto.setAdminRatedBy(p.getAdminRatedBy());

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

				//				dto.setImageUrls(imageUrls);
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
	public List<ShootingLocationPropertyDetailsDTO> getPropertiesByIndustryIdsAndDates(Integer industryId,Integer userId,
			LocalDate startDate,
			LocalDate endDate,
			PropertyBookingType propertyType) {

		try {

			// VALIDATION
			if (industryId == null) {
				throw new RuntimeException("Industry ID is required");
			}
			if (userId == null) {
				throw new RuntimeException("User ID is required");
			}
			if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
				throw new RuntimeException("Start Date cannot be after End Date");
			}


			// 1️⃣ Fetch all active properties for the single industry
			List<ShootingLocationPropertyDetails> properties =
					propertyDetailsRepository.findAllActiveByIndustryIndustryId(
							Collections.singletonList(industryId)
							);

			if (properties == null || properties.isEmpty()) {
				return Collections.emptyList();
			}

			// 2️⃣ Filter ONLY by availability window
			if (startDate != null && endDate != null) {

				properties = properties.stream()
						.filter(p -> {

							LocalDate availabilityStart = p.getAvailabilityStartDate();
							LocalDate availabilityEnd = p.getAvailabilityEndDate();

							// availability dates must exist
							if (availabilityStart == null || availabilityEnd == null) {
								logger.debug("Property {} excluded: availability dates missing", p.getId());
								return false;
							}

							//🔥 ONLY CHECK — search dates inside availability
							boolean valid =
									!startDate.isBefore(availabilityStart) &&
									!endDate.isAfter(availabilityEnd);

							if (!valid) {
								logger.debug(
										"Property {} excluded: search {} - {} outside availability {} - {}",
										p.getId(),
										startDate, endDate,
										availabilityStart, availabilityEnd
										);
							}

							return valid;
						})
						.collect(Collectors.toList());
			}
			// 2️⃣.5 Filter by property booking type (ONLY if provided)
			if (propertyType != null) {

				properties = properties.stream()
						.filter(p -> {

							ShootingLocationSubcategorySelection s = p.getSubcategorySelection();
							if (s == null) return false;

							return propertyType == PropertyBookingType.ENTIRE_PROPERTY
									? Boolean.TRUE.equals(s.getEntireProperty())
											: Boolean.TRUE.equals(s.getSingleProperty());
						})
						.collect(Collectors.toList());
			}


			if (properties.isEmpty()) {
				return Collections.emptyList();
			}

			// 3️⃣ Preload user likes
			Set<Integer> likedPropertyIds = likeRepository.findByLikedById(userId)
					.stream()
					.filter(PropertyLike::getStatus)
					.map(l -> l.getProperty().getId())
					.collect(Collectors.toSet());

			// 4️⃣ Convert to DTOs
			List<ShootingLocationPropertyDetailsDTO> dtoList = new ArrayList<>();

			for (ShootingLocationPropertyDetails p : properties) {

				ShootingLocationPropertyDetailsDTO dto = shootingLocationPropertyConverter.entityToDto(p);

				// ---- CLEAN PRICES BASED ON PROPERTY TYPE ----
				applyPropertyTypeCleanup(dto, propertyType);

				// Industry info (entity might be lazy)
				if (p.getIndustry() != null) {
					dto.setIndustryId(p.getIndustry().getIndustryId());
					dto.setIndustryName(p.getIndustry().getIndustryName());
				}

				// Likes
				dto.setLikedByUser(likedPropertyIds.contains(p.getId()));
				dto.setLikeCount(likeRepository.countLikesByPropertyId(p.getId()));

				// Media files
				List<FileOutputWebModel> imageUrls = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationImage, p.getId());


				List<String> govtIdUrls = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingGovermentId, p.getId())
						.stream().map(FileOutputWebModel::getFilePath).collect(Collectors.toList());

				List<String> verificationVideo = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationVerificationVideo, p.getId())
						.stream().map(FileOutputWebModel::getFilePath).collect(Collectors.toList());

				//				dto.setImageUrls(imageUrls);
				dto.setGovernmentIdUrls(govtIdUrls);
				dto.setVerificationVideo(verificationVideo);
				dto.setImageUrls(imageUrls);


				ShootingLocationPropertyReviewResponseDTO reviewData = getReviewsByPropertyId(p.getId(), userId);

				dto.setReviews(reviewData.getReviews());
				dto.setAverageRating(reviewData.getAverageRating());
				dto.setTotalReviews(reviewData.getTotalReviews());
				dto.setFiveStarPercentage(reviewData.getFiveStarPercentage());
				dto.setFourStarPercentage(reviewData.getFourStarPercentage());
				dto.setThreeStarPercentage(reviewData.getThreeStarPercentage());
				dto.setTwoStarPercentage(reviewData.getTwoStarPercentage());
				dto.setOneStarPercentage(reviewData.getOneStarPercentage());


				dtoList.add(dto);
			}

			return dtoList;

		} catch (RuntimeException e) {
			throw e;
		} 
	}

	private void applyPropertyTypeCleanup(
			ShootingLocationPropertyDetailsDTO dto,
			PropertyBookingType propertyType) {

		if (propertyType == null || dto.getSubcategorySelectionDTO() == null) return;

		ShootingLocationSubcategorySelectionDTO s =
				dto.getSubcategorySelectionDTO();

		if (propertyType == PropertyBookingType.ENTIRE_PROPERTY) {

			// keep ENTIRE, remove SINGLE
			s.setSingleProperty(false);
			s.setSingleDayPropertyPrice(null);
			s.setSingleNightPropertyPrice(null);
			s.setSingleFullDayPropertyPrice(null);
			s.setSinglePropertyDayDiscountPercent(null);
			s.setSinglePropertyNightDiscountPercent(null);
			s.setSinglePropertyFullDayDiscountPercent(null);

		} else if (propertyType == PropertyBookingType.SINGLE_PROPERTY) {

			// keep SINGLE, remove ENTIRE
			s.setEntireProperty(false);
			s.setEntireDayPropertyPrice(null);
			s.setEntireNightPropertyPrice(null);
			s.setEntireFullDayPropertyPrice(null);
			s.setEntirePropertyDayDiscountPercent(null);
			s.setEntirePropertyNightDiscountPercent(null);
			s.setEntirePropertyFullDayDiscountPercent(null);
		}
	}


	@Override
	public ShootingLocationPropertyReviewResponseDTO getReviewsByPropertyId(
			Integer propertyId,
			Integer userId) {

		// ----------------------------------
		// 1️⃣ FETCH REVIEWS
		// ----------------------------------
		List<ShootingLocationPropertyReview> reviews =
				propertyReviewRepository.findByPropertyId(propertyId)
				.stream()
				.sorted(Comparator.comparing(
						ShootingLocationPropertyReview::getCreatedOn).reversed())
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

		// ----------------------------------
		// 2️⃣ RATING CALCULATION
		// ----------------------------------
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

		double fiveStarPercentage = (fiveStar * 100.0) / totalReviews;
		double fourStarPercentage = (fourStar * 100.0) / totalReviews;
		double threeStarPercentage = (threeStar * 100.0) / totalReviews;
		double twoStarPercentage = (twoStar * 100.0) / totalReviews;
		double oneStarPercentage = (oneStar * 100.0) / totalReviews;

		// ----------------------------------
		// 3️⃣ PRELOAD USER REVIEW LIKES (ONCE)
		// ----------------------------------
		Map<Integer, Likes> userReviewLikeMap = new HashMap<>();

		if (userId != null) {
			likesRepository.findAllByUserIdForReviews(userId)
			.stream()
			.filter(l -> l.getReviewId() != null)
			.forEach(l -> userReviewLikeMap.put(l.getReviewId(), l));
		}

		// ----------------------------------
		// 4️⃣ MAP REVIEWS → DTO (WITH LIKES)
		// ----------------------------------
		List<ShootingLocationPropertyReviewDTO> reviewDTOs =
				reviews.stream()
				.map((ShootingLocationPropertyReview review) -> {

					Boolean likeStatus = false;
					Boolean unlikeStatus = false;
					Integer latestLikeId = null;

					Likes userLike = userReviewLikeMap.get(review.getId());

					if (userLike != null) {
						latestLikeId = userLike.getLikeId();

						if ("LIKE".equalsIgnoreCase(userLike.getReactionType())) {
							likeStatus = true;
						} else if ("UNLIKE".equalsIgnoreCase(userLike.getReactionType())) {
							unlikeStatus = true;
						}
					}

					Long totalLikes =
							likesRepository.countByReviewIdAndReactionTypeAndCategory(
									review.getId(), "LIKE", "REVIEW");

					Long totalUnlikes =
							likesRepository.countByReviewIdAndReactionTypeAndCategory(
									review.getId(), "UNLIKE", "REVIEW");

					List<FileOutputWebModel> files =
							mediaFilesService
							.getMediaFilesByCategoryAndRefId(
									MediaFileCategory.ShootingLocationReview,
									review.getId())
							.stream()
							.sorted(Comparator.comparing(
									FileOutputWebModel::getId).reversed())
							.collect(Collectors.toList());

					return ShootingLocationPropertyReviewDTO.builder()
							.id(review.getId())
							.propertyId(review.getProperty().getId())
							.userId(review.getUser().getUserId())
							.userName(review.getUser().getFirstName() + " "
									+ review.getUser().getLastName())
							.profilePicUrl(
									userService.getProfilePicUrl(
											review.getUser().getUserId()))
							.rating(review.getRating())
							.reviewText(review.getReviewText())
							.createdOn(review.getCreatedOn())
							.files(files)
							.likeStatus(likeStatus)
							.unlikeStatus(unlikeStatus)
							.latestLikeId(latestLikeId)
							.totalLikes(totalLikes)
							.totalUnlikes(totalUnlikes)
							.ownerReplyOn(review.getOwnerReplyOn())
							.ownerReplyText(review.getOwnerReplyText())
							.ownerReplyBy(
									review.getOwnerReplyBy() != null
									? review.getOwnerReplyBy().getUserId()
											: null
									)
							.ownerReplyByName(
									review.getOwnerReplyBy() != null
									? review.getOwnerReplyBy().getFirstName()
											+ " "
											+ review.getOwnerReplyBy().getLastName()
											: null
									)
							.build();
				})
				.collect(Collectors.toList());

		// ----------------------------------
		// 5️⃣ FINAL RESPONSE
		// ----------------------------------
		return ShootingLocationPropertyReviewResponseDTO.builder()
				.reviews(reviewDTOs)
				.averageRating(Math.round(averageRating * 10.0) / 10.0)
				.totalReviews(totalReviews)
				.fiveStarPercentage(Math.round(fiveStarPercentage * 10.0) / 10.0)
				.fourStarPercentage(Math.round(fourStarPercentage * 10.0) / 10.0)
				.threeStarPercentage(Math.round(threeStarPercentage * 10.0) / 10.0)
				.twoStarPercentage(Math.round(twoStarPercentage * 10.0) / 10.0)
				.oneStarPercentage(Math.round(oneStarPercentage * 10.0) / 10.0)
				.build();
	}


	@Override
	@Transactional
	public Response deletePropertyById(Integer id) {
		try {
			Integer userId = userDetails.userInfo().getId();

			ShootingLocationPropertyDetails property =
					propertyDetailsRepository.findById(id).orElse(null);

			if (property == null) {
				return new Response(0, "Property not found", null);
			}

			// Owner check
			if (property.getUser() == null ||
					!property.getUser().getUserId().equals(userId)) {

				return new Response(0, "You are not authorized to delete this property", null);
			}

			// Block delete if ACTIVE bookings exist
			boolean hasActiveBookings =
					bookingRepository.existsByProperty_IdAndStatusIn(
							id,
							List.of(
									BookingStatus.IN_PROGRESS,
									BookingStatus.CONFIRMED
									)
							);

			if (hasActiveBookings) {
				return new Response(
						0,
						"Property cannot be deleted because it has active bookings",
						null
						);
			}

			//  SOFT DELETE
			property.setStatus(ShootingPropertyStatus.DELETED);
			property.setUpdatedBy(userId);
			property.setUpdatedOn(LocalDateTime.now());
			propertyDetailsRepository.save(property);

			return new Response(1, "Property deleted successfully", null);

		} catch (Exception e) {
			logger.error("Delete property failed for ID {}: {}", id, e.getMessage(), e);
			return new Response(0, "Something went wrong while deleting property", null);
		}
	}


	@Transactional
	@Override
	public ShootingLocationPropertyDetailsDTO updatePropertyDetails(
			Integer id,
			ShootingLocationPropertyDetailsDTO dto,
			ShootingLocationFileInputModel inputFile) {

		ShootingLocationPropertyDetails existing = propertyDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Property not found with ID: " + id));

		// Convert DTO to temp entity
		ShootingLocationPropertyDetails tempEntity = shootingLocationPropertyConverter.dtoToEntity(dto);

		// Copy non-null scalar fields
		copyNonNullFields(tempEntity, existing);

		// Update relations
		updateRelations(existing, dto);

		// Update nested objects
		if (dto.getBusinessInformation() != null) {
			updateBusinessInfo(existing, dto.getBusinessInformation());
		}

		if (dto.getBankDetailsDTO() != null) {
			updateBankInfo(existing, dto.getBankDetailsDTO());
		}

		if (dto.getSubcategorySelectionDTO() != null) {
			updateSubcategorySelection(existing, dto.getSubcategorySelectionDTO());
		}

		// ✅ AVAILABILITY UPDATE 
		if (dto.getAvailabilityStartDate() != null) {
			existing.setAvailabilityStartDate(dto.getAvailabilityStartDate());
		}

		if (dto.getAvailabilityEndDate() != null) {
			existing.setAvailabilityEndDate(dto.getAvailabilityEndDate());
		}

		if (dto.getPausedDates() != null) {
			existing.setPausedDates(dto.getPausedDates());
		}


		// Set audit fields
		existing.setUpdatedOn(LocalDateTime.now());
		existing.setUpdatedBy(dto.getUserId());
		existing.setStatus(ShootingPropertyStatus.PENDING);
		// Save main entity
		ShootingLocationPropertyDetails saved = propertyDetailsRepository.save(existing);

		// ✅ Fetch the User from DB (required for file upload)
		User user = null;
		if (dto.getUserId() != null) {
			user = userRepository.findById(dto.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));
		}

		// ✅ Handle media files (only if user and file inputs exist)
		if (inputFile != null && user != null) {
			handleFileUpload(saved, inputFile, user);
		}

		return shootingLocationPropertyConverter.entityToDto(saved);
	}
	private void updateSubcategorySelection(
			ShootingLocationPropertyDetails property,
			ShootingLocationSubcategorySelectionDTO dto) {

		ShootingLocationSubcategorySelection selection =
				property.getSubcategorySelection();

		// 🔹 If exists → UPDATE
		if (selection == null) {
			selection = new ShootingLocationSubcategorySelection();
			selection.setPropertyDetails(property); // VERY IMPORTANT
		}

		ShootingLocationSubcategory subcategory =
				subcategoryRepo.findById(dto.getSubcategoryId().intValue())
				.orElseThrow(() ->
				new RuntimeException("Subcategory not found"));

		selection.setSubcategory(subcategory);
		selection.setEntireProperty(dto.getEntireProperty());
		selection.setSingleProperty(dto.getSingleProperty());

		selection.setEntireDayPropertyPrice(dto.getEntireDayPropertyPrice());
		selection.setEntireNightPropertyPrice(dto.getEntireNightPropertyPrice());
		selection.setEntireFullDayPropertyPrice(dto.getEntireFullDayPropertyPrice());

		selection.setSingleDayPropertyPrice(dto.getSingleDayPropertyPrice());
		selection.setSingleNightPropertyPrice(dto.getSingleNightPropertyPrice());
		selection.setSingleFullDayPropertyPrice(dto.getSingleFullDayPropertyPrice());

		selection.setEntirePropertyDayDiscountPercent(dto.getEntirePropertyDayDiscountPercent());
		selection.setEntirePropertyNightDiscountPercent(dto.getEntirePropertyNightDiscountPercent());
		selection.setEntirePropertyFullDayDiscountPercent(dto.getEntirePropertyFullDayDiscountPercent());

		selection.setSinglePropertyDayDiscountPercent(dto.getSinglePropertyDayDiscountPercent());
		selection.setSinglePropertyNightDiscountPercent(dto.getSinglePropertyNightDiscountPercent());
		selection.setSinglePropertyFullDayDiscountPercent(dto.getSinglePropertyFullDayDiscountPercent());

		// Attach back to property
		property.setSubcategorySelection(selection);
	}

	private void handleFileUpload(ShootingLocationPropertyDetails property,
			ShootingLocationFileInputModel inputFile,
			User user) {

		if (inputFile == null || user == null) return;

		Integer userId = user.getUserId();
		boolean replaceImages = "REPLACE".equalsIgnoreCase(inputFile.getUpdateMode());

		// ----------------------------
		// 🖼️ IMAGES (Append / Replace)
		// ----------------------------
		if (inputFile.getImages() != null && !inputFile.getImages().isEmpty()) {
			FileInputWebModel imageModel = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.shootingLocationImage)
					.categoryRefId(property.getId())
					.files(inputFile.getImages())
					.build();

			mediaFilesService.updateMediaFiles(
					MediaFileCategory.shootingLocationImage,
					property.getId(),
					userId,
					inputFile.getDeleteIds(),
					imageModel,
					replaceImages
					);
		}

		if (inputFile.getGovermentId() != null && !inputFile.getGovermentId().isEmpty()) {
			FileInputWebModel govtModel = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.shootingGovermentId)
					.categoryRefId(property.getId())
					.files(inputFile.getGovermentId())
					.build();

			mediaFilesService.updateMediaFiles(
					MediaFileCategory.shootingGovermentId,
					property.getId(),
					userId,
					null,
					govtModel,
					true // always replace Aadhaar/PAN
					);
		}

		// ----------------------------
		// 📹 VERIFICATION VIDEOS - Always Append
		// ----------------------------
		if (inputFile.getVideos() != null && !inputFile.getVideos().isEmpty()) {
			FileInputWebModel videoModel = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.shootingLocationVerificationVideo)
					.categoryRefId(property.getId())
					.files(inputFile.getVideos())
					.build();

			mediaFilesService.updateMediaFiles(
					MediaFileCategory.shootingLocationVerificationVideo,
					property.getId(),
					userId,
					null,
					videoModel,
					false // append new videos
					);
		}

		// ----------------------------
		// 📄 PROPERTY DOCUMENTS (All) - Always Replace
		// ----------------------------
		List<MultipartFile> propertyDocs = new ArrayList<>();

		if (inputFile.getSelfOwnedPropertyDocument() != null)
			propertyDocs.add(inputFile.getSelfOwnedPropertyDocument());

		if (inputFile.getMortgagePropertyDocument() != null)
			propertyDocs.add(inputFile.getMortgagePropertyDocument());

		if (inputFile.getOwnerPermittedDocument() != null)
			propertyDocs.add(inputFile.getOwnerPermittedDocument());

		if (inputFile.getPropertyDamageDocument() != null)
			propertyDocs.add(inputFile.getPropertyDamageDocument());

		if (inputFile.getCrewAccidentDocument() != null)
			propertyDocs.add(inputFile.getCrewAccidentDocument());

		if (!propertyDocs.isEmpty()) {
			FileInputWebModel docModel = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.shootingPropertyDocuments)
					.categoryRefId(property.getId())
					.files(propertyDocs)
					.build();

			mediaFilesService.updateMediaFiles(
					MediaFileCategory.shootingPropertyDocuments,
					property.getId(),
					userId,
					null,
					docModel,
					true // always replace all legal docs
					);
		}

		// Persist property changes (e.g., updated doc URLs)
		propertyDetailsRepository.saveAndFlush(property);
	}


	private void updateRelations(ShootingLocationPropertyDetails existing, ShootingLocationPropertyDetailsDTO dto) {
		if (dto.getCategoryId() != null) {
			existing.setCategory(categoryRepo.findById(dto.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Category not found")));
		}

		if (dto.getSubCategoryId() != null) {
			existing.setSubCategory(subcategoryRepo.findById(dto.getSubCategoryId())
					.orElseThrow(() -> new RuntimeException("Subcategory not found")));
		}

		if (dto.getTypesId() != null) {
			existing.setTypes(typesRepo.findById(dto.getTypesId())
					.orElseThrow(() -> new RuntimeException("Type not found")));
		}

		if (dto.getUserId() != null) {
			existing.setUser(userRepository.findById(dto.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found")));
		}

		if (dto.getIndustryId() != null) {
			existing.setIndustry(industryRepository.findById(dto.getIndustryId())
					.orElseThrow(() -> new RuntimeException("Industry not found")));
		}
	}

	private void updateBusinessInfo(ShootingLocationPropertyDetails property, BusinessInformationDTO dto) {

		ShootingLocationBusinessInformation business =
				businessInformationRepository.findByPropertyDetails(property)
				.orElse(new ShootingLocationBusinessInformation());

		business.setPropertyDetails(property);
		business.setBusinessName(dto.getBusinessName());
		business.setBusinessType(dto.getBusinessType());
		business.setBusinessLocation(dto.getBusinessLocation());
		business.setPanOrGSTNumber(dto.getPanOrGSTNumber());
		business.setLocation(dto.getLocation());
		business.setAddressLine1(dto.getAddressLine1());
		business.setAddressLine2(dto.getAddressLine2());
		business.setAddressLine3(dto.getAddressLine3());
		business.setState(dto.getState());
		business.setPostalCode(dto.getPostalCode());


		businessInformationRepository.save(business);
	}

	private void updateBankInfo(ShootingLocationPropertyDetails property, BankDetailsDTO dto) {

		ShootingLocationOwnerBankDetails bank =
				bankDetailsRepository.findByPropertyDetails(property)
				.orElse(new ShootingLocationOwnerBankDetails());

		bank.setPropertyDetails(property);
		bank.setBeneficiaryName(dto.getBeneficiaryName());
		bank.setMobileNumber(dto.getMobileNumber());
		bank.setAccountNumber(dto.getAccountNumber());
		bank.setConfirmAccountNumber(dto.getConfirmAccountNumber());
		bank.setIfscCode(dto.getIfscCode());

		bankDetailsRepository.save(bank);
	}

	private void copyNonNullFields(ShootingLocationPropertyDetails source, ShootingLocationPropertyDetails target) {

		// Basic Info
		if (source.getFullName() != null) target.setFullName(source.getFullName());
		if (source.getCitizenship() != null) target.setCitizenship(source.getCitizenship());
		if (source.getPlaceOfBirth() != null) target.setPlaceOfBirth(source.getPlaceOfBirth());
		if (source.getPropertyName() != null) target.setPropertyName(source.getPropertyName());
		if (source.getLocation() != null) target.setLocation(source.getLocation());
		if (source.getDateOfBirth() != null) target.setDateOfBirth(source.getDateOfBirth());
		if (source.getProofOfIdentity() != null) target.setProofOfIdentity(source.getProofOfIdentity());
		if (source.getCountryOfIssued() != null) target.setCountryOfIssued(source.getCountryOfIssued());

		// Listing Summary
		if (source.getNumberOfPeopleAllowed() != null) target.setNumberOfPeopleAllowed(source.getNumberOfPeopleAllowed());
		if (source.getTotalArea() != 0) target.setTotalArea(source.getTotalArea());
		if (source.getSelectedUnit() != null) target.setSelectedUnit(source.getSelectedUnit());
		if (source.getNumberOfRooms() != 0) target.setNumberOfRooms(source.getNumberOfRooms());
		if (source.getNumberOfFloor() != null) target.setNumberOfFloor(source.getNumberOfFloor());
		if (source.getCeilingHeight() != null) target.setCeilingHeight(source.getCeilingHeight());

		// Lists & Facilities
		if (source.getOutdoorFeatures() != null) target.setOutdoorFeatures(source.getOutdoorFeatures());
		if (source.getArchitecturalStyle() != null) target.setArchitecturalStyle(source.getArchitecturalStyle());
		if (source.getVintage() != null) target.setVintage(source.getVintage());
		if (source.getIndustrial() != null) target.setIndustrial(source.getIndustrial());
		if (source.getTraditional() != null) target.setTraditional(source.getTraditional());

		if (source.getPowerSupply() != null) target.setPowerSupply(source.getPowerSupply());
		if (source.getBakupGenerators() != null) target.setBakupGenerators(source.getBakupGenerators());
		if (source.getVoltageCapacity() != null) target.setVoltageCapacity(source.getVoltageCapacity());
		if (source.getWifi() != null) target.setWifi(source.getWifi());
		if (source.getAirConditionAndHeating() != null) target.setAirConditionAndHeating(source.getAirConditionAndHeating());
		if (source.getNumberOfWashrooms() != 0) target.setNumberOfWashrooms(source.getNumberOfWashrooms());
		if (source.getWaterSupply() != null) target.setWaterSupply(source.getWaterSupply());
		if (source.getChangingRooms() != null) target.setChangingRooms(source.getChangingRooms());
		if (source.getKitchen() != null) target.setKitchen(source.getKitchen());

		// Restrictions
		if (source.getDroneUsage() != null) target.setDroneUsage(source.getDroneUsage());
		if (source.getFirearms() != null) target.setFirearms(source.getFirearms());
		if (source.getActionScenes() != null) target.setActionScenes(source.getActionScenes());

		// Descriptive
		if (source.getDescription() != null) target.setDescription(source.getDescription());
		if (source.getTypeLocation() != null) target.setTypeLocation(source.getTypeLocation());
		if (source.getLocationLink() != null) target.setLocationLink(source.getLocationLink());
		if (source.getHygienStatus() != null) target.setHygienStatus(source.getHygienStatus());
		if (source.getGenderSpecific() != null) target.setGenderSpecific(source.getGenderSpecific());

		// Booleans
		if (source.isBusinessOwner() != target.isBusinessOwner()) target.setBusinessOwner(source.isBusinessOwner());


		if (source.getAvailabilityStartDate()!=null) target.setAvailabilityStartDate(source.getAvailabilityStartDate());
		if (source.getAvailabilityEndDate()!=null) target.setAvailabilityEndDate(source.getAvailabilityEndDate());
		if(source.getPausedDates()!=null) target.setPausedDates(source.getPausedDates());
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
			// 1️⃣ Fetch liked properties (only active likes)
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

			List<ShootingLocationPropertyDetailsDTO> result = new ArrayList<>();

			// 2️⃣ Loop each liked property
			for (ShootingLocationPropertyDetails property : properties) {

				Integer pid = property.getId();

				// A) Base mapping
				ShootingLocationPropertyDetailsDTO dto =
						shootingLocationPropertyConverter.entityToDto(property);

				// B) Like details
				dto.setLikedByUser(true);
				dto.setLikeCount(likeRepository.countLikesByPropertyId(pid));

				// C) Industry name
				dto.setIndustryName(
						property.getIndustry() != null
						? property.getIndustry().getIndustryName()
								: null
						);

				// D) Media files
				dto.setImageUrls(
						mediaFilesService
						.getMediaFilesByCategoryAndRefId(
								MediaFileCategory.shootingLocationImage, pid)
						);

				dto.setGovernmentIdUrls(
						mediaFilesService
						.getMediaFilesByCategoryAndRefId(
								MediaFileCategory.shootingGovermentId, pid)
						.stream()
						.map(FileOutputWebModel::getFilePath)
						.toList()
						);

				dto.setVerificationVideo(
						mediaFilesService
						.getMediaFilesByCategoryAndRefId(
								MediaFileCategory.shootingLocationVerificationVideo, pid)
						.stream()
						.map(FileOutputWebModel::getFilePath)
						.toList()
						);

				// E) Reviews + rating
				ShootingLocationPropertyReviewResponseDTO reviewData =
						getReviewsByPropertyId(pid, userId);

				dto.setTotalReviews(reviewData.getTotalReviews());
				dto.setAverageRating(reviewData.getAverageRating());
				dto.setFiveStarPercentage(reviewData.getFiveStarPercentage());
				dto.setFourStarPercentage(reviewData.getFourStarPercentage());
				dto.setThreeStarPercentage(reviewData.getThreeStarPercentage());
				dto.setTwoStarPercentage(reviewData.getTwoStarPercentage());
				dto.setOneStarPercentage(reviewData.getOneStarPercentage());
				dto.setReviews(reviewData.getReviews());

				// F) Admin rating (IMPORTANT)
				dto.setAdminRating(property.getAdminRating());
				dto.setAdminRatedOn(property.getAdminRatedOn());
				dto.setAdminRatedBy(property.getAdminRatedBy());

				// Add final DTO
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

		List<FileOutputWebModel> mediaDTOs = mediaFilesService.getMediaFilesByCategoryAndRefId(
				MediaFileCategory.ShootingLocationReview,
				review.getId()
				);

		// 4️⃣ Convert to DTO
		return ShootingLocationPropertyReviewDTO.builder()
				.id(savedReview.getId())
				.propertyId(propertyId)
				.userId(userId)
				.rating(rating)
				.reviewText(reviewText)
				.userName(user.getName())
				.profilePicUrl(
						userService.getProfilePicUrl(
								review.getUser().getUserId()))
				.files(mediaDTOs)
				.ownerReplyText(savedReview.getOwnerReplyText())				
				.ownerReplyOn(savedReview.getOwnerReplyOn())
				.createdOn(savedReview.getCreatedOn())
				.build();
	}


	@Override
	@Transactional
	public ShootingLocationPropertyReviewDTO replyToReview(Integer reviewId, Integer ownerUserId, String replyText) {

		ShootingLocationPropertyReview review = propertyReviewRepository.findById(reviewId)
				.orElseThrow(() -> new RuntimeException("Review not found"));

		ShootingLocationPropertyDetails property = review.getProperty();
		if (property == null) throw new RuntimeException("Property data missing for review");

		User propertyOwner = property.getUser();
		if (propertyOwner == null || propertyOwner.getUserId() == null)
			throw new RuntimeException("Property has no owner assigned");

		if (!propertyOwner.getUserId().equals(ownerUserId))
			throw new RuntimeException("Only the property owner can reply to this review");

		User owner = userRepository.findById(ownerUserId)
				.orElseThrow(() -> new RuntimeException("Owner user not found"));

		// ⭐ ONLY update these three fields
		review.setOwnerReplyText(replyText);
		review.setOwnerReplyBy(owner);
		review.setOwnerReplyOn(LocalDateTime.now());

		ShootingLocationPropertyReview saved = propertyReviewRepository.save(review);

		// ⭐ Get media files (your existing method)
		List<FileOutputWebModel> files = mediaFilesService
				.getMediaFilesByCategoryAndRefId(MediaFileCategory.ShootingLocationReview, saved.getId())
				.stream()
				.sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
				.collect(Collectors.toList());

		// ⭐ Build DTO exactly like your GET review API does
		return ShootingLocationPropertyReviewDTO.builder()
				.id(saved.getId())
				.propertyId(property.getId())
				.userId(saved.getUser().getUserId())
				.userName(saved.getUser().getName())
				.profilePicUrl(userService.getProfilePicUrl(review.getUser().getUserId()))
				.rating(saved.getRating())
				.reviewText(saved.getReviewText())
				.createdOn(saved.getCreatedOn())

				.files(files)

				// ⭐ reply fields
				.ownerReplyText(saved.getOwnerReplyText())
				.ownerReplyBy(ownerUserId)
				.ownerReplyOn(saved.getOwnerReplyOn())
				.build();
	}

	@Override
	@Transactional
	public ShootingLocationPropertyReviewDTO deleteReply(Integer reviewId, Integer ownerUserId) {

		ShootingLocationPropertyReview review = propertyReviewRepository.findById(reviewId)
				.orElseThrow(() -> new RuntimeException("Review not found"));

		ShootingLocationPropertyDetails property = review.getProperty();
		if (property == null)
			throw new RuntimeException("Property data missing for review");

		User propertyOwner = property.getUser();
		if (propertyOwner == null || propertyOwner.getUserId() == null)
			throw new RuntimeException("Property has no owner assigned");

		// Only owner can delete reply
		if (!propertyOwner.getUserId().equals(ownerUserId))
			throw new RuntimeException("Only the property owner can delete the reply");

		// ⭐ Delete reply by setting values to null
		review.setOwnerReplyText(null);
		review.setOwnerReplyBy(null);
		review.setOwnerReplyOn(null);

		ShootingLocationPropertyReview saved = propertyReviewRepository.save(review);

		// ⭐ Get media files
		List<FileOutputWebModel> files = mediaFilesService
				.getMediaFilesByCategoryAndRefId(MediaFileCategory.ShootingLocationReview, saved.getId())
				.stream()
				.sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
				.collect(Collectors.toList());

		// ⭐ Build DTO (same response as reply)
		return ShootingLocationPropertyReviewDTO.builder()
				.id(saved.getId())
				.propertyId(property.getId())
				.userId(saved.getUser().getUserId())
				.userName(saved.getUser().getName())
				.profilePicUrl(userService.getProfilePicUrl(saved.getUser().getUserId()))
				.rating(saved.getRating())
				.reviewText(saved.getReviewText())
				.createdOn(saved.getCreatedOn())
				.files(files)

				// reply becomes null
				.ownerReplyText(null)
				.ownerReplyBy(null)
				.ownerReplyOn(null)
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
					.fullName(property.getFullName())

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

					.waterSupply(property.getWaterSupply())
					.changingRooms(property.getChangingRooms())
					.kitchen(property.getKitchen())

					.neutralLightingConditions(property.getNeutralLightingConditions())
					.artificialLightingAvailability(property.getArtificialLightingAvailability())
					.parkingCapacity(property.getParkingCapacity())
					.droneUsage(property.getDroneUsage())
					.firearms(property.getFirearms())
					.actionScenes(property.getActionScenes())

					.structuralModification(property.getStructuralModification())
					.temporary(property.getTemporary())
					.dressing(property.getDressing())
					.insuranceRequired(property.getInsuranceRequired())		
					.description(property.getDescription())
					.businessOwner(property.isBusinessOwner())
					.businessInformation(businessInfoDTO)
					.bankDetailsDTO(bankDetailsDTO)
					.subcategorySelectionDTO(subcategorySelectionDTO)
					.category(categoryDTO)
					.subCategory(subcategoryDTO)
					.type(typeDTO)
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
					//					.availabilityDates(availabilityDates)
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
				.userName(user.getName())
				.createdOn(review.getCreatedOn())
				.profilePicUrl(
						userService.getProfilePicUrl(
								review.getUser().getUserId()))
				.files(mediaDTOs)
				.ownerReplyText(review.getOwnerReplyText())				
				.ownerReplyOn(review.getOwnerReplyOn())
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

	public List<LocalDate> getAvailableDatesForProperty(
			Integer propertyId,
			SlotType requestedSlot,
			Integer excludeBookingId) {

		ShootingLocationPropertyDetails property =
				propertyDetailsRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found"));

		LocalDate start = property.getAvailabilityStartDate();
		LocalDate end = property.getAvailabilityEndDate();

		if (start == null || end == null) {
			throw new RuntimeException("Availability dates not set");
		}

		// 1️⃣ Create full date range
		Set<LocalDate> availableDates = start
				.datesUntil(end.plusDays(1))
				.collect(Collectors.toSet());

		// 2️⃣ Remove paused dates
		if (property.getPausedDates() != null) {
			availableDates.removeAll(property.getPausedDates());
		}

		// 3️⃣ Fetch confirmed bookings
		List<ShootingLocationBooking> confirmedBookings =
				bookingRepo.findByProperty_IdAndStatus(
						propertyId,
						BookingStatus.CONFIRMED
						);

		// 4️⃣ Remove blocked dates based on slot
		for (ShootingLocationBooking booking : confirmedBookings) {

			// 🔥 Ignore current booking when editing
			if (excludeBookingId != null &&
					booking.getId().equals(excludeBookingId)) {
				continue;
			}

			if (booking.getConfirmedBookingDates() == null) continue;

			for (LocalDate bookedDate : booking.getConfirmedBookingDates()) {

				SlotType bookedSlot = booking.getSlotType();

				boolean blockDate = false;

				if (requestedSlot == SlotType.FULL_DAY) {
					// Full day needs full availability
					blockDate = true;
				}

				else if (requestedSlot == SlotType.DAY) {
					blockDate =
							bookedSlot == SlotType.DAY ||
							bookedSlot == SlotType.FULL_DAY;
				}

				else if (requestedSlot == SlotType.NIGHT) {
					blockDate =
							bookedSlot == SlotType.NIGHT ||
							bookedSlot == SlotType.FULL_DAY;
				}

				if (blockDate) {
					availableDates.remove(bookedDate);
				}
			}
		}

		// 5️⃣ Return sorted list
		return availableDates.stream()
				.sorted()
				.collect(Collectors.toList());
	}
	@Override
	public List<LocalDate> getAvailableDatesForProperty(
			Integer propertyId,
			SlotType requestedSlot) {

		ShootingLocationPropertyDetails property =
				propertyDetailsRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found"));

		LocalDate start = property.getAvailabilityStartDate();
		LocalDate end = property.getAvailabilityEndDate();

		if (start == null || end == null) {
			throw new RuntimeException("Availability dates not set");
		}

		// 1️⃣ Create full date range
		Set<LocalDate> availableDates = start
				.datesUntil(end.plusDays(1))
				.collect(Collectors.toSet());

		// 2️⃣ Remove paused dates
		if (property.getPausedDates() != null) {
			availableDates.removeAll(property.getPausedDates());
		}

		// 3️⃣ Fetch confirmed bookings
		List<ShootingLocationBooking> confirmedBookings =
				bookingRepo.findByProperty_IdAndStatus(
						propertyId,
						BookingStatus.CONFIRMED
						);

		// 4️⃣ Remove blocked dates BASED ON SLOT
		for (ShootingLocationBooking booking : confirmedBookings) {

			if (booking.getConfirmedBookingDates() == null) continue;

			for (LocalDate bookedDate : booking.getConfirmedBookingDates()) {

				SlotType bookedSlot = booking.getSlotType();

				boolean blockDate = false;

				if (requestedSlot == SlotType.FULL_DAY) {
					// Full day requires FULL availability
					blockDate = true;
				}
				else if (requestedSlot == SlotType.DAY) {
					// Day blocked if DAY or FULL_DAY booked
					blockDate =
							bookedSlot == SlotType.DAY ||
							bookedSlot == SlotType.FULL_DAY;
				}
				else if (requestedSlot == SlotType.NIGHT) {
					// Night blocked if NIGHT or FULL_DAY booked
					blockDate =
							bookedSlot == SlotType.NIGHT ||
							bookedSlot == SlotType.FULL_DAY;
				}

				if (blockDate) {
					availableDates.remove(bookedDate);
				}
			}
		}

		// 5️⃣ Sort and return
		return availableDates.stream()
				.sorted()
				.collect(Collectors.toList());
	}



	@Override
	public ShootingLocationBookingDTO createOrUpdateBooking(ShootingLocationBookingDTO dto) {

		if (dto.getBookingDates() == null || dto.getBookingDates().isEmpty()) {
			throw new RuntimeException("Booking dates are required");
		}

		// 1️⃣ Fetch Property
		ShootingLocationPropertyDetails property =
				propertyDetailsRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new RuntimeException("Property not found"));

		// 2️⃣ Fetch Client
		User client =
				userRepository.findById(dto.getClientId())
				.orElseThrow(() -> new RuntimeException("Client not found"));

		// 3️⃣ Check Existing Booking
		Optional<ShootingLocationBooking> existingBookingOpt =
				bookingRepository.findTopByClient_UserIdAndProperty_IdOrderByCreatedAtDesc(
						dto.getClientId(),
						dto.getPropertyId()
						);

		ShootingLocationBooking booking;

	    boolean isNewBooking = false;
	    boolean isModification = false;

		// ----------------------------------------------------
		// 4️⃣ CREATE NEW BOOKING
		// ----------------------------------------------------

		if (existingBookingOpt.isEmpty()) {

			booking = ShootingLocationBookingConverter.toEntity(dto);

			booking.setProperty(property);
			booking.setClient(client);
			booking.setStatus(BookingStatus.PENDING);

			booking.setTotalDays(dto.getBookingDates().size());

			booking = bookingRepository.save(booking);

			// Generate booking code
			String bookingCode = generateBookingCode(
					property.getPropertyName(),
					booking.getId()
					);

			booking.setBookingCode(bookingCode);
			booking = bookingRepository.save(booking);
		}

		// ----------------------------------------------------
		// 5️⃣ UPDATE EXISTING BOOKING
		// ----------------------------------------------------

		else {

			booking = existingBookingOpt.get();

			// PENDING → edit everything
			if (booking.getStatus() == BookingStatus.PENDING) {

				updateAllFields(booking, dto);
			      isNewBooking = true;
			}

			// APPROVED → edit but send again for approval
			else if (booking.getStatus() == BookingStatus.APPROVED) {

				updateAllFields(booking, dto);

				booking.setStatus(BookingStatus.PENDING);
			      isNewBooking = true;
			}

			// CONFIRMED → allow modification request
			else if (booking.getStatus() == BookingStatus.CONFIRMED) {

				int oldDays = booking.getTotalDays();
				int newDays = dto.getBookingDates().size();


				if (oldDays != newDays) {
					throw new RuntimeException(
							"You must select exactly " + oldDays + " days."
							);
				}

				booking.setBookingDates(dto.getBookingDates());
				booking.setModificationRequested(true);

				// send again for owner approval
				booking.setStatus(BookingStatus.PENDING);
			     isModification = true;
			}

			// REJECTED or COMPLETED → create new booking
			else {

				booking = ShootingLocationBookingConverter.toEntity(dto);

				booking.setProperty(property);
				booking.setClient(client);
				booking.setStatus(BookingStatus.PENDING);

				booking.setTotalDays(dto.getBookingDates().size());

				booking = bookingRepository.save(booking);

				String bookingCode = generateBookingCode(
						property.getPropertyName(),
						booking.getId()
						);

				booking.setBookingCode(bookingCode);
				  isNewBooking = true;
			}
		}

		// ----------------------------------------------------
		// 6️⃣ AVAILABILITY VALIDATION
		// ----------------------------------------------------

		List<LocalDate> availableDates =
				getAvailableDatesForProperty(
						property.getId(),
						dto.getSlotType(),
						booking.getId()
						);

		for (LocalDate date : dto.getBookingDates()) {

			if (!availableDates.contains(date)) {
				throw new RuntimeException(
						"Selected date " + date + " is not available"
						);
			}
		}

		// ----------------------------------------------------
		// 7️⃣ PRICE CALCULATION
		// ----------------------------------------------------

		ShootingLocationSubcategorySelection sel =
				property.getSubcategorySelection();

		if (sel == null) {
			throw new RuntimeException("Property pricing not configured");
		}

		double pricePerDay =
				getPrice(sel, dto.getBookingType(), dto.getSlotType());

		double discountPercent =
				getDiscount(sel, dto.getBookingType(), dto.getSlotType());

		int totalDays = booking.getTotalDays();

		double subtotal = pricePerDay * totalDays;

		double discountAmount = subtotal * (discountPercent / 100.0);

		double amountAfterDiscount = subtotal - discountAmount;

		double gstPercent = 18.0;

		double gstAmount = amountAfterDiscount * gstPercent / 100;

		double netAmount = amountAfterDiscount + gstAmount;

		booking.setPricePerDay(pricePerDay);
		booking.setSubtotal(subtotal);
		booking.setDiscountPercent(discountPercent);
		booking.setDiscountAmount(discountAmount);
		booking.setAmountAfterDiscount(amountAfterDiscount);
		booking.setGstPercent(gstPercent);
		booking.setGstAmount(gstAmount);
		booking.setNetAmount(netAmount);
		booking.setUpdatedAt(LocalDateTime.now());

		booking = bookingRepository.save(booking);
		
		  // ================= NOTIFICATION =================

	    try {

	        User owner = property.getUser();

	        if (owner != null) {

	            String title;
	            String message;
	            String userType;

	            if (isNewBooking) {

	                title = "New Booking Request";

	                message = client.getName() +
	                        " requested to book " +
	                        property.getPropertyName() +
	                        ". Please review.";

	                userType = "BOOKING_REQUEST";
	            }

	            else if (isModification) {

	                title = "Booking Modification Request";

	                message = client.getName() +
	                        " requested changes to the confirmed booking for " +
	                        property.getPropertyName() +
	                        ". Please review.";

	                userType = "BOOKING_MODIFICATION_REQUEST";
	            }

	            else {
	                return ShootingLocationBookingConverter.toDTO(booking);
	            }

	            // In-App Notification
	            inAppNotificationRepo.save(
	                    InAppNotification.builder()
	                            .senderId(client.getUserId())
	                            .receiverId(owner.getUserId())
	                            .title(title)
	                            .message(message)
	                            .userType(userType)
	                            .id(booking.getId())
	                            .isRead(false)
	                            .isDeleted(false)
	                            .createdOn(new Date())
	                            .createdBy(client.getUserId())
	                            .build()
	            );

	            // Push Notification
	            String token = owner.getFirebaseDeviceToken();

	            if (token != null && !token.trim().isEmpty()) {

	                Message push = Message.builder()
	                        .setToken(token)
	                        .setNotification(
	                                Notification.builder()
	                                        .setTitle(title)
	                                        .setBody(message)
	                                        .build()
	                        )
	                        .putData("type", userType)
	                        .putData("bookingId", booking.getId().toString())
	                        .build();

	                FirebaseMessaging.getInstance().send(push);
	            }
	        }

	    } catch (Exception e) {
	        logger.error("Booking notification error", e);
	    }


		return ShootingLocationBookingConverter.toDTO(booking);
	}

	private void updateAllFields(ShootingLocationBooking booking,
			ShootingLocationBookingDTO dto) {

		booking.setBookingType(dto.getBookingType());
		booking.setSlotType(dto.getSlotType());
		booking.setSlotTimings(dto.getSlotTimings());
		booking.setBookingDates(dto.getBookingDates());
		booking.setBookingMessage(dto.getBookingMessage());

		int totalDays = dto.getBookingDates().size();

		booking.setTotalDays(totalDays);

	}

	private String generateBookingCode(String propertyName, Integer bookingId) {

		// 1️⃣ First 2 letters of property name
		String cleanedName = propertyName.replaceAll("\\s+", "");
		String prefix = cleanedName
				.substring(0, Math.min(2, cleanedName.length()))
				.toUpperCase();

		// 2️⃣ Current date in YYMMDD format
		String datePart = java.time.LocalDate.now()
				.format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));

		// 3️⃣ Last 2 digits of booking ID
		String idPart = String.format("%02d", bookingId % 100);

		return prefix + datePart +"-" + idPart;
	}
	// ------------ PRICE HELPERS ------------

	private double getPrice(ShootingLocationSubcategorySelection sel,
			PropertyBookingType type,
			SlotType slot) {

		if (type == PropertyBookingType.ENTIRE_PROPERTY) {
			switch (slot) {
			case DAY: return sel.getEntireDayPropertyPrice();
			case NIGHT: return sel.getEntireNightPropertyPrice();
			case FULL_DAY: return sel.getEntireFullDayPropertyPrice();
			}
		} else {
			switch (slot) {
			case DAY: return sel.getSingleDayPropertyPrice();
			case NIGHT: return sel.getSingleNightPropertyPrice();
			case FULL_DAY: return sel.getSingleFullDayPropertyPrice();
			}
		}
		return 0;
	}

	private double getDiscount(ShootingLocationSubcategorySelection sel,
			PropertyBookingType type,
			SlotType slot) {

		if (type == PropertyBookingType.ENTIRE_PROPERTY) {
			switch (slot) {
			case DAY: return sel.getEntirePropertyDayDiscountPercent();
			case NIGHT: return sel.getEntirePropertyNightDiscountPercent();
			case FULL_DAY: return sel.getEntirePropertyFullDayDiscountPercent();
			}
		} else {
			switch (slot) {
			case DAY: return sel.getSinglePropertyDayDiscountPercent();
			case NIGHT: return sel.getSinglePropertyNightDiscountPercent();
			case FULL_DAY: return sel.getSinglePropertyFullDayDiscountPercent();
			}
		}
		return 0;
	}

	@Override
	public Payments createShootingPayment(ShootingPaymentModel model) {

		// 1️⃣ Validate userId
		if (model.getUserId() == null) {
			throw new RuntimeException("User ID is required");
		}
		if (!userRepository.existsById(model.getUserId())) {
			throw new RuntimeException("User not found with ID: " + model.getUserId());
		}

		// 2️⃣ Validate bookingId
		if (model.getBookingId() == null) {
			throw new RuntimeException("Booking ID is required");
		}
		if (!bookingRepository.existsById(model.getBookingId())) {
			throw new RuntimeException("Booking not found with ID: " + model.getBookingId());
		}

		// 3️⃣ Validate amount
		if (model.getAmount() == null || model.getAmount() <= 0) {
			throw new RuntimeException("Amount must be greater than 0");
		}

		// 4️⃣ Validate firstname & email
		if (model.getFullName() == null || model.getFullName().trim().isEmpty()) {
			throw new RuntimeException("Name is required");
		}
		if (model.getEmail() == null || model.getEmail().trim().isEmpty()) {
			throw new RuntimeException("Email is required");
		}

		// 5️⃣ Validate phone
		if (model.getPhoneNumber() == null || model.getPhoneNumber().trim().isEmpty()) {
			throw new RuntimeException("Phone number is required");
		}

		// 6️⃣ Prepare DTO for common payment service
		PaymentsDTO dto = PaymentsDTO.builder()
				.referenceId(model.getBookingId())
				.moduleType(PaymentModule.SHOOTING_LOCATION)
				.userId(model.getUserId())
				.amount(model.getAmount())
				.fullName(model.getFullName())
				.email(model.getEmail())
				.phoneNumber(model.getPhoneNumber())
				.productInfo("Shooting Location Booking")
				.txnid(model.getTxnid())
				.build();

		// 7️⃣ Pass to common payment handler
		return paymentsServiceImpl.createPayment(dto);
	}


	@Override
public ResponseEntity<Response> handleShootingLocationPaymentSuccess(String txnid) {

    try {

        Payments payment = paymentsServiceImpl.markPaymentSuccess(txnid);

        if (payment.getModuleType() != PaymentModule.SHOOTING_LOCATION) {
            throw new RuntimeException("Invalid module type");
        }

        ShootingLocationBooking booking = bookingRepository
                .findById(payment.getReferenceId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        User client = booking.getClient();
        User owner = booking.getProperty().getUser();

        ShootingLocationPropertyDetailsDTO dto =
                shootingLocationService.getPropertyByBookingId(booking.getId());

        // -------- EMAILS --------

        sendClientBookingEmail(payment, booking, dto);
        sendOwnerBookingEmail(payment, booking);

        // -------- NOTIFICATIONS --------

        sendBookingNotification(
                owner.getUserId(),
                client.getUserId(),
                "Booking Confirmed",
                "Your booking for " + dto.getPropertyName() + " has been confirmed.",
                "BOOKING_CONFIRMED",
                booking.getId(),
                client.getFirebaseDeviceToken()
        );

        sendBookingNotification(
                client.getUserId(),
                owner.getUserId(),
                "New Booking Received",
                client.getFirstName() + " booked your property " + dto.getPropertyName(),
                "NEW_BOOKING_RECEIVED",
                booking.getId(),
                owner.getFirebaseDeviceToken()
        );

        return ResponseEntity.ok(
                new Response(1, "Payment successful. Emails & notifications sent.", null));

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(0, e.getMessage(), null));
    }
}
	private void sendClientBookingEmail(
	        Payments payment,
	        ShootingLocationBooking booking,
	        ShootingLocationPropertyDetailsDTO dto) {

	    String mail = buildClientBookingMail(payment, booking, dto);

	    byte[] pdf = generateInvoicePdf(payment, booking);

	    mailNotification.sendEmailWithAttachment(
	            payment.getFullName(),
	            payment.getEmail(),
	            "Shooting Location Booking Confirmed",
	            mail,
	            pdf,
	            "Invoice_" + payment.getTxnid() + ".pdf");
	}
	private void sendOwnerBookingEmail(
	        Payments payment,
	        ShootingLocationBooking booking) {

	    User owner = booking.getProperty().getUser();

	    if (owner == null || owner.getEmail() == null) return;

	    String mail = buildOwnerBookingMail(payment, booking);

	    mailNotification.sendEmail(
	            owner.getFirstName(),
	            owner.getEmail(),
	            "New Booking for Your Property",
	            mail);
	}
	
	private void sendBookingNotification(
	        Integer senderId,
	        Integer receiverId,
	        String title,
	        String message,
	        String type,
	        Integer bookingId,
	        String token) {

	    inAppNotificationRepo.save(
	            InAppNotification.builder()
	                    .senderId(senderId)
	                    .receiverId(receiverId)
	                    .title(title)
	                    .message(message)
	                    .userType(type)
	                    .id(bookingId)
	                    .isRead(false)
	                    .isDeleted(false)
	                    .createdOn(new Date())
	                    .createdBy(senderId)
	                    .build());

	    if (token == null || token.isEmpty()) return;

	    try {

	        Message push = Message.builder()
	                .setToken(token)
	                .setNotification(
	                        Notification.builder()
	                                .setTitle(title)
	                                .setBody(message)
	                                .build())
	                .putData("type", type)
	                .putData("bookingId", bookingId.toString())
	                .build();

	        FirebaseMessaging.getInstance().send(push);

	    } catch (Exception e) {
	        logger.error("Push notification failed", e);
	    }
	}
public String buildClientBookingMail(
        Payments payment,
        ShootingLocationBooking booking,
        ShootingLocationPropertyDetailsDTO dto) {

    StringBuilder sb = new StringBuilder();

    sb.append("<html><body style='font-family:Arial;'>");

    sb.append("<h2>Shooting Location Booking Confirmed</h2>");

    sb.append("<p>Hello ").append(payment.getFullName()).append(",</p>");

    sb.append("<p>Your booking has been successfully confirmed. Below are the complete booking details.</p>");

    sb.append("<h3>Booking Information</h3>");
    sb.append("<p><b>Booking ID:</b> ").append(booking.getId()).append("</p>");
    sb.append("<p><b>Transaction ID:</b> ").append(payment.getTxnid()).append("</p>");
    sb.append("<p><b>Status:</b> ").append(booking.getStatus()).append("</p>");

    sb.append("<h3>Property Details</h3>");
    sb.append("<p><b>Property Name:</b> ").append(dto.getPropertyName()).append("</p>");
    sb.append("<p><b>Property Type:</b> ").append(dto.getTypeLocation()).append("</p>");
    sb.append("<p><b>Location:</b> ").append(dto.getLocation()).append("</p>");
    sb.append("<p><b>Google Map:</b> <a href='")
            .append(dto.getLocationLink())
            .append("'>View Location</a></p>");

    sb.append("<h3>Property Overview</h3>");
    sb.append("<p><b>Total Area:</b> ").append(dto.getTotalArea()).append(" sq.ft</p>");
    sb.append("<p><b>Crew Allowed:</b> ").append(dto.getNumberOfPeopleAllowed()).append("</p>");
    sb.append("<p><b>Rooms:</b> ").append(dto.getNumberOfRooms()).append("</p>");
    sb.append("<p><b>Floors:</b> ").append(dto.getNumberOfFloor()).append("</p>");
    sb.append("<p><b>Ceiling Height:</b> ").append(dto.getCeilingHeight()).append("</p>");

    sb.append("<h3>Facilities Available</h3>");
    sb.append("<ul>");

    if (dto.getPowerSupply() != null)
        sb.append("<li>Power Supply: ").append(dto.getPowerSupply()).append("</li>");

    if (dto.getBakupGenerators() != null)
        sb.append("<li>Backup generators available</li>");

    if (dto.getAirConditionAndHeating() != null)
        sb.append("<li>Air conditioning and heating available</li>");

    if (dto.getNumberOfWashrooms() > 0)
        sb.append("<li>Washrooms available: ").append(dto.getNumberOfWashrooms()).append("</li>");

    if (dto.getParkingCapacity() != null)
        sb.append("<li>Parking capacity: ").append(dto.getParkingCapacity()).append("</li>");

    sb.append("</ul>");

    sb.append("<h3>Filming Rules</h3>");
    sb.append("<ul>");

    if (dto.getDroneUsage() != null)
        sb.append("<li>Drone usage: ").append(dto.getDroneUsage()).append("</li>");

    if (dto.getFirearms() != null)
        sb.append("<li>Firearms allowed: ").append(dto.getFirearms()).append("</li>");

    if (dto.getActionScenes() != null)
        sb.append("<li>Action scenes: ").append(dto.getActionScenes()).append("</li>");

    if (dto.getInsuranceRequired() != null)
        sb.append("<li>Insurance documentation may be required</li>");

    sb.append("</ul>");

    sb.append("<h3>Important Instructions</h3>");
    sb.append("<ul>");
    sb.append("<li>Please carry a valid ID proof during entry.</li>");
    sb.append("<li>Entry will be allowed only during the booked slot.</li>");
    sb.append("<li>Follow all property rules and safety guidelines.</li>");
    sb.append("<li>Any damages caused to the property may incur additional charges.</li>");
    sb.append("<li>Coordination with the property owner is recommended before the shoot.</li>");
    sb.append("</ul>");

    sb.append("<h3>Payment Details</h3>");
    sb.append("<p><b>Total Amount Paid:</b> ₹").append(payment.getAmount()).append("</p>");

    sb.append("<p>If you need assistance, please contact our support team.</p>");

    sb.append("<p>Thank you for choosing FilmHook.</p>");

    sb.append("</body></html>");

    return sb.toString();
}

private String buildOwnerBookingMail(Payments payment, ShootingLocationBooking booking) {

    StringBuilder sb = new StringBuilder();

    sb.append("<html><body style='font-family:Arial;'>");

  

    sb.append("<p>A client has successfully booked your shooting location through FilmHook.</p>");

    sb.append("<h3>Booking Details</h3>");
    sb.append("<p><b>Booking ID:</b> ").append(booking.getId()).append("</p>");
    sb.append("<p><b>Property Name:</b> ")
            .append(booking.getProperty().getPropertyName()).append("</p>");

    sb.append("<p><b>Client Name:</b> ").append(payment.getFullName()).append("</p>");
    sb.append("<p><b>Client Email:</b> ").append(payment.getEmail()).append("</p>");

    sb.append("<h3>Payment Information</h3>");
    sb.append("<p><b>Transaction ID:</b> ").append(payment.getTxnid()).append("</p>");
    sb.append("<p><b>Total Amount Paid:</b> ₹").append(payment.getAmount()).append("</p>");

    sb.append("<p>The payment has been securely processed through FilmHook.</p>");

    sb.append("<h3>Next Steps</h3>");
    sb.append("<ul>");
    sb.append("<li>Please coordinate with the client regarding shoot timings.</li>");
    sb.append("<li>Ensure property access and facilities are prepared for the scheduled shoot.</li>");
    sb.append("<li>After the shoot is completed, mark the booking as completed in the platform.</li>");
    sb.append("</ul>");

    sb.append("<p>The payout will be credited to your FilmHook wallet after the booking is completed successfully.</p>");

    sb.append("<p>Thank you for listing your property with FilmHook.</p>");


    sb.append("</body></html>");

    return sb.toString();
}

	@Override
	public ResponseEntity<Response> handleShootingLocationPaymentFailed(String txnid, String reason) {
		try {
			// 1️⃣ Update payment status (single table)
			Payments payment = paymentsServiceImpl.markPaymentFailure(txnid, reason);

			// 2️⃣ Ensure this payment is for shooting location
			if (payment.getModuleType() != PaymentModule.SHOOTING_LOCATION) {
				throw new RuntimeException("Payment is not for Shooting Location");
			}

			// 3️⃣ Fetch booking using referenceId
			ShootingLocationBooking booking = bookingRepository.findById(payment.getReferenceId())
					.orElseThrow(() -> new RuntimeException("Booking not found"));

			booking.setStatus(BookingStatus.FAILED);
			bookingRepository.save(booking);

			// 4️⃣ Create failed email content
			String retryUrl = "https://filmhookapps.com/retry?txnid=" + txnid;

			String mailContent =
					"<!doctype html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>"
							+ "<style>body{font-family:Arial;color:#333;padding:18px;background:#f7f9fc}.card{max-width:600px;margin:0 auto;background:#fff;padding:16px;border-radius:6px;border:1px solid #e9edf6}a.cta{display:inline-block;padding:10px 14px;background:#0057b7;color:#fff;border-radius:6px;text-decoration:none;font-weight:600}</style>"
							+ "</head><body><div class='card'><h2 style='margin:0 0 8px 0;color:#0b2545'>Payment failed</h2>"
							+ "<p style='margin:0 0 12px 0'>We were unable to process your payment for the following booking attempt. Please try again or contact support.</p>"
							+ "<div style='border:1px solid #eef2fb;border-radius:6px;padding:12px;margin-bottom:12px'>"
							+ "<p style='margin:6px 0;'><strong>Location:</strong> " + booking.getProperty().getPropertyName() + "</p>"
							+ "<p style='margin:6px 0;'><strong>Reason:</strong> " + reason + "</p>"
							+ "</div>"
							+ "<a class='cta' href='" + retryUrl + "'>Retry payment</a>"
							+ "<p style='margin-top:12px;color:#556'>If you need help, contact <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a>.</p>"
							+ "</div></body></html>";


			// 5️⃣ Send failure email
			mailNotification.sendEmail(
					payment.getFullName(),
					payment.getEmail(),
					"Shooting Location Payment Failed ❌",
					mailContent
					);

			return ResponseEntity.ok(new Response(1, "Payment Failed — Email Sent", null));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500)
					.body(new Response(0, "Error: " + e.getMessage(), null));
		}
	}



	public static byte[] generateInvoicePdf(Payments payment, ShootingLocationBooking booking) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);
			Document doc = new Document(pdf, PageSize.A4);
			doc.setMargins(18, 18, 18, 18);

			PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);


			try {
				InputStream logoStream = new URL(
						"https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png"
						).openStream();

				Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()))
						.scaleToFit(120, 50)
						.setHorizontalAlignment(HorizontalAlignment.CENTER);

				doc.add(logo);
			} catch (Exception ignore) {}

			doc.add(new Paragraph("\n"));


			Table titleBand = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
					.useAllAvailableWidth();

			titleBand.setBorder(Border.NO_BORDER);

			Cell leftTitle = new Cell()
					.add(new Paragraph("TAX INVOICE")
							.setFont(fontBold)
							.setFontSize(16)
							.setFontColor(ColorConstants.WHITE))
					.setBackgroundColor(BRAND_BLUE)
					.setBorder(Border.NO_BORDER)
					.setPadding(10);

			titleBand.addCell(leftTitle);

			Cell rightMeta = new Cell()
					.add(new Paragraph("Invoice #: " + safe(payment.getTxnid()))
							.setFont(fontBold)
							.setFontSize(10)
							.setFontColor(ColorConstants.WHITE))
					.add(new Paragraph("Date: " + LocalDate.now())
							.setFont(fontRegular)
							.setFontSize(9)
							.setFontColor(ColorConstants.WHITE))
					.setBackgroundColor(BRAND_BLUE)
					.setTextAlignment(TextAlignment.RIGHT)
					.setBorder(Border.NO_BORDER)
					.setPadding(10);

			titleBand.addCell(rightMeta);

			doc.add(titleBand);
			doc.add(new Paragraph("\n"));


			Table parties = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
					.useAllAvailableWidth();

			// Billed To
			Cell billed = new Cell()
					.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
					.setPadding(10);

			billed.add(new Paragraph("BILLED TO")
					.setFont(fontBold).setFontSize(10).setFontColor(ColorConstants.DARK_GRAY));
			billed.add(new Paragraph(safe(payment.getFullName()))
					.setFont(fontRegular).setFontSize(11));
			billed.add(new Paragraph("Email: " + safe(payment.getEmail()))
					.setFont(fontRegular).setFontSize(10).setFontColor(ColorConstants.GRAY));

			parties.addCell(billed);

			// Issued By
			Cell issued = new Cell()
					.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
					.setPadding(10);

			issued.add(new Paragraph("ISSUED BY")
					.setFont(fontBold).setFontSize(10).setFontColor(ColorConstants.DARK_GRAY));
			issued.add(new Paragraph("Film-hook Media Apps Pvt. Ltd.")
					.setFont(fontRegular).setFontSize(11));
			issued.add(new Paragraph("GSTIN: 29ABCDE1234F2Z5")
					.setFont(fontRegular).setFontSize(10).setFontColor(ColorConstants.GRAY));
			issued.add(new Paragraph("Bangalore, Karnataka")
					.setFont(fontRegular).setFontSize(10).setFontColor(ColorConstants.GRAY));
			issued.add(new Paragraph("support@film-hookapps.com")
					.setFont(fontRegular).setFontSize(10).setFontColor(ColorConstants.GRAY));

			parties.addCell(issued);

			doc.add(parties);
			doc.add(new Paragraph("\n"));


			Table card = new Table(UnitValue.createPercentArray(new float[]{100}))
					.useAllAvailableWidth();

			card.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

			Cell header = new Cell()
					.add(new Paragraph("Booking Summary")
							.setFont(fontBold)
							.setFontSize(11))
					.setBackgroundColor(new DeviceRgb(245, 247, 250))
					.setBorder(Border.NO_BORDER)
					.setPadding(8);

			card.addCell(header);

			Table summary = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
					.useAllAvailableWidth();
			summary.setBorder(Border.NO_BORDER);

			summary.addCell(label("Location", fontBold));
			summary.addCell(value(booking.getProperty().getPropertyName(), fontRegular));

			summary.addCell(label("Booking ID", fontBold));
			summary.addCell(value(String.valueOf(booking.getId()), fontRegular));

			card.addCell(new Cell().add(summary).setPadding(10).setBorder(Border.NO_BORDER));
			doc.add(card);
			doc.add(new Paragraph("\n"));


			Table items = new Table(UnitValue.createPercentArray(new float[]{50, 10, 15, 15, 10}))
					.useAllAvailableWidth();

			items.addHeaderCell(headerCell("Description", fontBold));
			items.addHeaderCell(headerCell("Qty", fontBold));
			items.addHeaderCell(headerCell("Unit Price", fontBold));
			items.addHeaderCell(headerCell("Amount", fontBold));
			items.addHeaderCell(headerCell("Net", fontBold));

			doc.add(items);
			doc.add(new Paragraph("\n"));


			Table tax = new Table(UnitValue.createPercentArray(new float[]{80, 20}))
					.useAllAvailableWidth();

			tax.addCell(textLeft("Taxes & Platform Fees (18%)", fontBold));
			//			tax.addCell(textRight("₹ " + format(taxes), fontRegular));

			doc.add(tax);
			doc.add(new Paragraph("\n"));

			Table totalTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
					.useAllAvailableWidth();

			totalTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(" ")));

			//			totalTable.addCell(
			//					new Cell()
			//					.setBorder(Border.NO_BORDER)
			//					.add(new Paragraph("GRAND TOTAL\n₹ " + format(total))
			//							.setFont(fontBold)
			//							.setFontSize(14)
			//							.setFontColor(BRAND_BLUE)
			//							.setTextAlignment(TextAlignment.RIGHT))
			//					);

			doc.add(totalTable);
			doc.add(new Paragraph("\n"));

			//			doc.add(new Paragraph("Amount (in words): " +
			////					NumberToWordsConverter.convertToIndianCurrency(total))
			//					.setFont(fontRegular)
			//					.setFontSize(10)
			//					.setItalic()
			//					.setFontColor(ColorConstants.DARK_GRAY));

			doc.add(new Paragraph("\nPlease ensure the location is prepared for the shoot. For any assistance contact support@film-hookapps.com")
					.setFontSize(9)
					.setFontColor(ColorConstants.GRAY));

			doc.add(new Paragraph("\n\nDigitally Signed by Film-hook Media Apps Pvt. Ltd.")
					.setFont(fontBold)
					.setFontSize(9)
					.setTextAlignment(TextAlignment.RIGHT));

			doc.close();

			// RETURN PDF WITH WATERMARK
			// --------------------------------------------------------------------
			return addImageWatermark(baos.toByteArray(), "https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png");

		} catch (Exception e) {
			throw new RuntimeException("Failed generating invoice", e);
		}
	}


	// WATERMARK IMPLEMENTATION (WORKING)
	// --------------------------------------------------------------------
	private static byte[] addImageWatermark(byte[] inputPdfBytes, String imageUrl) {
		try {
			// Load watermark image
			byte[] imgBytes;
			try (InputStream is = new URL(imageUrl).openStream()) {
				imgBytes = is.readAllBytes();
			}

			ImageData imgData = ImageDataFactory.create(imgBytes);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfDocument pdfDoc = new PdfDocument(
					new PdfReader(new ByteArrayInputStream(inputPdfBytes)),
					new PdfWriter(baos)
					);

			int pages = pdfDoc.getNumberOfPages();

			for (int p = 1; p <= pages; p++) {

				PdfPage page = pdfDoc.getPage(p);
				Rectangle pageSize = page.getPageSize();

				float wmWidth = pageSize.getWidth() * 0.7f;
				float aspect = imgData.getHeight() / (float) imgData.getWidth();
				float wmHeight = wmWidth * aspect;

				float x = (pageSize.getWidth() - wmWidth) / 2f;
				float y = (pageSize.getHeight() - wmHeight) / 2f;

				// Canvas for watermark (draw AFTER content)
				PdfCanvas pdfCanvas = new PdfCanvas(
						page.newContentStreamAfter(),
						page.getResources(),
						pdfDoc
						);

				PdfExtGState gs = new PdfExtGState();
				gs.setFillOpacity(0.08f); // subtle opacity
				pdfCanvas.saveState();
				pdfCanvas.setExtGState(gs);

				// Create layout image
				Image img = new Image(imgData);
				img.scaleToFit(wmWidth, wmHeight);

				// ★★★ DIAGONAL ROTATION ★★★
				float diagonalAngle = (float) Math.toRadians(35);  // rotate 35 degrees
				img.setRotationAngle(diagonalAngle);

				// center position
				img.setFixedPosition(p,
						(pageSize.getWidth() - img.getImageScaledWidth()) / 2,
						(pageSize.getHeight() - img.getImageScaledHeight()) / 2
						);

				// Layout canvas (correct constructor)
				Canvas canvas = new Canvas(pdfCanvas, pageSize);
				canvas.add(img);
				canvas.close();

				pdfCanvas.restoreState();
			}

			pdfDoc.close();
			return baos.toByteArray();

		} catch (Exception ex) {
			throw new RuntimeException("Failed to add diagonal watermark", ex);
		}
	}


	// --------------------------------------------------------------------
	// CELL HELPERS
	// --------------------------------------------------------------------
	private static Cell label(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text)
				.setFont(font)
				.setFontSize(10)
				.setFontColor(ColorConstants.DARK_GRAY))
				.setBorder(Border.NO_BORDER)
				.setPadding(4);
	}

	private static Cell value(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text)
				.setFont(font)
				.setFontSize(10))
				.setBorder(Border.NO_BORDER)
				.setPadding(4);
	}

	private static Cell headerCell(String text, PdfFont font) {
		return new Cell()
				.add(new Paragraph(text).setFont(font).setFontSize(10))
				.setFontColor(ColorConstants.WHITE)
				.setBackgroundColor(new DeviceRgb(3, 169, 244))
				.setPadding(6);
	}

	private static Cell bodyCell(String text, PdfFont font) {
		return new Cell()
				.add(new Paragraph(text).setFont(font).setFontSize(10))
				.setPadding(6)
				.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
	}

	private static Cell textLeft(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(10))
				.setPadding(6)
				.setBorder(Border.NO_BORDER);
	}

	private static Cell textRight(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text)
				.setFont(font)
				.setFontSize(10)
				.setTextAlignment(TextAlignment.RIGHT))
				.setPadding(6)
				.setBorder(Border.NO_BORDER);
	}

	private static String format(double v) {
		return String.format("%.2f", v);
	}

	private static String safe(Object o) {
		return o == null ? "" : o.toString();
	}


//	@Scheduled(cron = "0 50 17 * * *") 
//	public void sendBookingExpiryReminders() {
//
//		LocalDate today = LocalDate.now();
//
//		// 1️⃣ Get only CONFIRMED bookings
//		List<ShootingLocationBooking> bookings =
//				bookingRepo.findByStatus(BookingStatus.CONFIRMED);
//
//		logger.info("🔍 Checking expiry reminders for {} bookings on {}", bookings.size(), today);
//
//		for (ShootingLocationBooking booking : bookings) {
//
//			Integer bookingId = booking.getId();
//
//			try {
//				// 2️⃣ Validate bookingDates
//				List<LocalDate> bookingDates = booking.getConfirmedBookingDates();
//				if (bookingDates == null || bookingDates.isEmpty()) {
//					continue;
//				}
//
//				// 3️⃣ Get LAST booking date
//				LocalDate lastDate = bookingDates.stream()
//						.max(LocalDate::compareTo)
//						.orElse(null);
//
//				if (lastDate == null) continue;
//
//				// 4️⃣ Reminder day = lastDate - 1
//				LocalDate reminderDate = lastDate.minusDays(1);
//
//				// ❌ Not reminder day
//				if (!today.equals(reminderDate)) {
//					continue;
//				}
//
//				// 5️⃣ PAYMENT CHECK
//				Optional<Payments> paymentOpt =
//						paymentsRepository.findByReferenceIdAndModuleTypeAndPaymentStatus(
//								bookingId,
//								PaymentModule.SHOOTING_LOCATION,
//								"SUCCESS"
//								);
//
//				if (paymentOpt.isEmpty()) {
//					logger.info("⏭️ Skipping booking {} – payment not successful", bookingId);
//					continue;
//				}
//
//				// 6️⃣ Retry limit
//				int retryCount =
//						inAppNotificationRepo.countExpiryReminders(
//								"SHOOTING_LOCATION_EXPIRY",
//								bookingId
//								);
//
//				if (retryCount >= 3) {
//					logger.warn("⛔ Max retry reached for booking {}", bookingId);
//					continue;
//				}
//
//				// 7️⃣ Send notifications
//				User client = booking.getClient();
//
//				String title = "⏳ Shooting Location Expiring Soon!";
//				String message =
//						"Hi " + client.getName()
//						+ ", your shooting location booking will expire in 24 hours. "
//						+ "Please renew if required.";
//
//				// ================= EMAIL =================
//				String mailBody =
//						"<p>Dear <b>" + client.getName() + "</b>,</p>"
//								+ "<p>Your shooting location booking will expire in <b>24 hours</b>.</p>"
//								+ "<p><b>Booking ID:</b> " + bookingId + "</p>"
//								+ "<p><b>Last Shoot Date:</b> " + lastDate + "</p>"
//								+ "<p>Please renew to avoid cancellation.</p>";
//
//				mailNotification.sendEmailAsync(
//						client.getName(),
//						client.getEmail(),
//						"⏳ FilmHook Reminder: Booking Expiring Soon",
//						mailBody
//						);
//
//				// ================= IN-APP =================
//				inAppNotificationRepo.save(
//						InAppNotification.builder()
//						.senderId(0)
//						.receiverId(client.getUserId())
//						.title(title)
//						.message(message)
//						.userType("SHOOTING_LOCATION_EXPIRY")
//						.id(bookingId)
//						.isRead(false)
//						.isDeleted(false)
//						.createdOn(new Date())
//						.createdBy(0)
//						.build()
//						);
//
//				// ================= PUSH =================
//				String token = client.getFirebaseDeviceToken();
//				if (token != null && !token.isBlank()) {
//
//					Message pushMessage = Message.builder()
//							.setToken(token)
//							.setNotification(
//									Notification.builder()
//									.setTitle(title)
//									.setBody(message)
//									.build()
//									)
//							.putData("type", "SHOOTING_LOCATION_EXPIRY")
//							.putData("bookingId", bookingId.toString())
//							.build();
//
//					FirebaseMessaging.getInstance().send(pushMessage);
//				}
//
//				logger.info("✅ Expiry reminder sent (attempt {}) for booking {}",
//						retryCount + 1, bookingId);
//
//			} catch (Exception e) {
//				logger.error("❌ Reminder failed for booking {}", bookingId, e);
//			}
//		}
//	}

//	@Scheduled(cron = "0 43 17 * * *", zone = "Asia/Kolkata") // 5:43 PM
//	@Transactional
//	public void markBookingsAsCompleted() {
//
//		LocalDate today = LocalDate.now();
//
//		List<ShootingLocationBooking> bookings =
//				bookingRepo.findByStatus(BookingStatus.CONFIRMED);
//
//		for (ShootingLocationBooking booking : bookings) {
//
//			Integer bookingId = booking.getId();
//			User client = booking.getClient();
//
//			try {
//				// 1️⃣ Validate booking dates
//				List<LocalDate> bookingDates = booking.getConfirmedBookingDates();
//				if (bookingDates == null || bookingDates.isEmpty()) continue;
//
//				// 2️⃣ LAST booking date
//				LocalDate lastBookingDate = bookingDates.stream()
//						.max(LocalDate::compareTo)
//						.orElse(null);
//
//				if (lastBookingDate == null || lastBookingDate.isAfter(today)) {
//					continue; // still active
//				}
//
//				// 3️⃣ Convert to end-of-day
//				LocalDateTime expiryDateTime =
//						lastBookingDate.atTime(23, 59, 59);
//
//				// 4️⃣ Payment check
//				Optional<Payments> paymentOpt =
//						paymentsRepository.findByReferenceIdAndModuleTypeAndPaymentStatus(
//								bookingId,
//								PaymentModule.SHOOTING_LOCATION,
//								"SUCCESS"
//								);
//
//				if (paymentOpt.isEmpty()) continue;
//
//				Payments payment = paymentOpt.get();
//
//				// 5️⃣ MARK PAYMENT EXPIRED (REUSE METHOD)
//				paymentsServiceImpl.markExpired(payment, expiryDateTime);
//
//				// 6️⃣ Mark booking completed
////				booking.setStatus(BookingStatus.COMPLETED);
////				booking.setUpdatedAt(LocalDateTime.now());
//				bookingRepo.save(booking);
//
//				// ================= IN-APP =================
//				String title = "Shooting Location Completed";
//				String message =
//						"Hi " + client.getName()
//						+ ", your booking at "
//						+ booking.getProperty().getPropertyName()
//						+ " has been successfully completed.";
//
//				inAppNotificationRepo.save(
//						InAppNotification.builder()
//						.senderId(0)
//						.receiverId(client.getUserId())
//						.title(title)
//						.message(message)
//						.userType("SHOOTING_LOCATION_COMPLETED")
//						.id(bookingId)
//						.isRead(false)
//						.isDeleted(false)
//						.createdOn(new Date())
//						.createdBy(0)
//						.build()
//						);
//
//				// ================= PUSH =================
//				if (client.getFirebaseDeviceToken() != null) {
//					Message push = Message.builder()
//							.setToken(client.getFirebaseDeviceToken())
//							.setNotification(
//									Notification.builder()
//									.setTitle(title)
//									.setBody(message)
//									.build()
//									)
//							.putData("bookingId", bookingId.toString())
//							.build();
//
//					FirebaseMessaging.getInstance().send(push);
//				}
//
//				// ================= EMAIL =================
//				String subject = "Booking Completed – FilmHook";
//
//				String mailContent =
//						"<div style='font-family:Arial;padding:20px'>"
//								+ "<h2>Booking Completed 🎬</h2>"
//								+ "<p>Hi <b>" + client.getName() + "</b>,</p>"
//								+ "<p>Your shooting location booking has been <b>successfully completed</b>.</p>"
//								+ "<hr>"
//								+ "<p><b>Booking ID:</b> " + bookingId + "</p>"
//								+ "<p><b>Property:</b> " + booking.getProperty().getPropertyName() + "</p>"
//								+ "<p><b>Last Shoot Date:</b> " + lastBookingDate + "</p>"
//								+ "<p><b>Total Days:</b> " + booking.getTotalDays() + "</p>"
//								+ "<p><b>Amount Paid:</b> ₹" + booking.getNetAmount() + "</p>"
//								+ "<hr>"
//								+ "<p>Thank you for choosing <b>FilmHook</b>.</p>"
//								+ "</div>";
//
//				mailNotification.sendEmailAsync(
//						client.getName(),
//						client.getEmail(),
//						subject,
//						mailContent
//						);
//
//			} catch (Exception e) {
//				logger.error("❌ Failed to complete booking {}", bookingId, e);
//			}
//		}
//	}

	@Scheduled(cron = "0 0 * * * *", zone = "Asia/Kolkata")
	@Transactional
	public void notifyOwnerToCompleteBooking() {

	    LocalDate today = LocalDate.now();

	    List<ShootingLocationBooking> bookings =
	            bookingRepo.findByStatus(BookingStatus.IN_PROGRESS);

	    for (ShootingLocationBooking booking : bookings) {

	        Integer bookingId = booking.getId();
	        User owner = booking.getProperty().getUser();

	        try {

	            List<LocalDate> bookingDates = booking.getConfirmedBookingDates();
	            if (bookingDates == null || bookingDates.isEmpty()) continue;

	            LocalDate lastBookingDate = bookingDates.stream()
	                    .max(LocalDate::compareTo)
	                    .orElse(null);

	            if (lastBookingDate == null || lastBookingDate.isAfter(today)) {
	                continue;
	            }

	            Optional<Payments> paymentOpt =
	                    paymentsRepository.findByReferenceIdAndModuleTypeAndPaymentStatus(
	                            bookingId,
	                            PaymentModule.SHOOTING_LOCATION,
	                            "SUCCESS"
	                    );

	            if (paymentOpt.isEmpty()) continue;

	            // ---------------- Notification Content ----------------

	            String propertyName = booking.getProperty().getPropertyName();

	            String title = "Complete Booking";

	            String message =
	            	    "The shoot at " + propertyName +
	            	    " has ended. Please enter the OTP to mark the booking as completed. "
	            	    + "Your payout will be credited to your wallet within 2–7 business days.";

	            // ---------------- IN-APP NOTIFICATION ----------------

	            inAppNotificationRepo.save(
	                    InAppNotification.builder()
	                            .senderId(0)
	                            .receiverId(owner.getUserId())
	                            .title(title)
	                            .message(message)
	                            .userType("BOOKING_COMPLETION_REQUIRED")
	                            .id(bookingId)
	                            .isRead(false)
	                            .isDeleted(false)
	                            .createdOn(new Date())
	                            .createdBy(0)
	                            .build()
	            );

	            // ---------------- PUSH NOTIFICATION ----------------

	            if (owner.getFirebaseDeviceToken() != null &&
	                    !owner.getFirebaseDeviceToken().trim().isEmpty()) {

	                Message push = Message.builder()
	                        .setToken(owner.getFirebaseDeviceToken())
	                        .setNotification(
	                                Notification.builder()
	                                        .setTitle(title)
	                                        .setBody(message)
	                                        .build()
	                        )
	                        .putData("type", "booking_completion")
	                        .putData("bookingId", bookingId.toString())
	                        .build();

	                FirebaseMessaging.getInstance().send(push);
	            }

	            // ---------------- EMAIL ----------------

	            String subject = "Action Required: Complete Booking – FilmHook";

	            String mailContent =
	                    "<div style='font-family:Arial;padding:20px'>"
	                            + "<h2>Complete Your Booking 🎬</h2>"
	                            + "<p>Hi <b>" + owner.getName() + "</b>,</p>"
	                            + "<p>The shooting schedule for your property has now ended.</p>"
	                            + "<p>Please complete the booking by verifying the <b>OTP</b> provided by the client.</p>"
	                            + "<hr>"
	                            + "<p><b>Booking ID:</b> " + bookingId + "</p>"
	                            + "<p><b>Property:</b> " + propertyName + "</p>"
	                            + "<p><b>Last Shoot Date:</b> " + lastBookingDate + "</p>"
	                            + "<hr>"
	                            + "<h3>Steps to Complete the Booking</h3>"
	                            + "<ol>"
	                            + "<li>Open the FilmHook app</li>"
	                            + "<li>Go to your <b>Bookings</b></li>"
	                            + "<li>Select the completed booking</li>"
	                            + "<li>Enter the OTP provided by the client</li>"
	                            + "</ol>"
	                            + "<hr>"
	                            + "<p>After OTP verification, the payout will be processed.</p>"
	                            + "<p>Your earnings will be credited to your <b>FilmHook Wallet within 2–7 business days</b>.</p>"
	                            + "<p>Thank you for hosting on <b>FilmHook</b>.</p>"
	                            + "</div>";

	            mailNotification.sendEmailAsync(
	                    owner.getName(),
	                    owner.getEmail(),
	                    subject,
	                    mailContent
	            );

	        } catch (Exception e) {
	            logger.error("Failed to notify owner for booking {}", bookingId, e);
	        }
	    }
	}
	
	@Override
	@Transactional
	public ResponseEntity<?> saveAdminPropertyRating(ShootingLocationPropertyDetailsDTO request) {

		if (request.getId() == null) {
			return ResponseEntity.badRequest()
					.body("Property ID is required");
		}

		if (request.getAdminRating() == null ||
				request.getAdminRating() < 1 ||
				request.getAdminRating() > 5) {

			return ResponseEntity.badRequest()
					.body("Admin rating must be between 1 and 5");
		}

		ShootingLocationPropertyDetails property =
				propertyDetailsRepository.findById(request.getId())
				.orElseThrow(() ->
				new RuntimeException("Property not found"));


		property.setAdminRating(request.getAdminRating());
		property.setAdminRatedOn(LocalDateTime.now());
		property.setAdminRatedBy(userDetails.userInfo().getId()); 

		propertyDetailsRepository.save(property);

		return ResponseEntity.ok(
				"Admin rating saved successfully for property ID "
						+ request.getId()
				);
	}

	@Override
	public List<BookingWithPropertyDTO> getBookingHistoryByClientId(Integer clientId) {

		List<ShootingLocationBooking> bookings =
				bookingRepository.findByClient_UserIdOrderByUpdatedAtDesc(clientId);

		return bookings.stream()
				.map(booking -> BookingWithPropertyDTO.builder()
						.booking(ShootingLocationBookingConverter.toDTO(booking))
						.property(
								shootingLocationPropertyConverter.entityToDto(
										booking.getProperty()
										)
								)
						.build()
						)
				.collect(Collectors.toList());
	}


	@Override
	public List<ShootingLocationPropertyDetailsDTO> getPropertiesSorted(
			Integer industryId,
			String sortBy,
			String order,
			String propertyType,
			String priceType) {

		// 1️⃣ Fetch active properties
		List<ShootingLocationPropertyDetails> entities =
				propertyDetailsRepository.findByIndustryIndustryIdAndStatusTrue(industryId);

		if (entities == null || entities.isEmpty()) {
			return Collections.emptyList();
		}

		// 2️⃣ Build comparator
		Comparator<ShootingLocationPropertyDetails> comparator;

		// ---------- RATING + PRICE ----------
		if ("rating_price".equalsIgnoreCase(sortBy)) {

			Comparator<Double> ratingComparator =
					"desc".equalsIgnoreCase(order)
					? Comparator.reverseOrder()
							: Comparator.naturalOrder();

			Comparator<Double> priceComparator =
					"desc".equalsIgnoreCase(order)
					? Comparator.reverseOrder()
							: Comparator.naturalOrder();

			comparator = Comparator
					.comparing(
							(ShootingLocationPropertyDetails p) ->
							p.getAdminRating() != null ? p.getAdminRating() : 0.0,
									ratingComparator
							)
					.thenComparing(
							p -> getPrice(p, propertyType, priceType),
							priceComparator
							);
		}

		// ---------- PRICE ONLY ----------
		else if ("price".equalsIgnoreCase(sortBy)) {

			comparator = Comparator.comparing(
					p -> getPrice(p, propertyType, priceType)
					);

			if ("desc".equalsIgnoreCase(order)) {
				comparator = comparator.reversed();
			}
		}

		// ---------- RATING ONLY ----------
		else if ("rating".equalsIgnoreCase(sortBy)) {

			comparator = Comparator.comparing(
					(ShootingLocationPropertyDetails p) ->
					p.getAdminRating() != null ? p.getAdminRating() : 0.0
					);

			if ("desc".equalsIgnoreCase(order)) {
				comparator = comparator.reversed();
			}
		}

		// ---------- FALLBACK ----------
		else {
			comparator = Comparator.comparing(ShootingLocationPropertyDetails::getId);
		}

		// 3️⃣ Preload likes (avoid N+1)
		Set<Integer> likedPropertyIds = likeRepository.findAll()
				.stream()
				.filter(PropertyLike::getStatus)
				.map(l -> l.getProperty().getId())
				.collect(Collectors.toSet());

		// 4️⃣ Sort + Enrich DTO
		List<ShootingLocationPropertyDetailsDTO> dtoList = new ArrayList<>();

		for (ShootingLocationPropertyDetails p :
			entities.stream().sorted(comparator).collect(Collectors.toList())) {

			ShootingLocationPropertyDetailsDTO dto =
					shootingLocationPropertyConverter.entityToDto(p);

			// ---------- Industry ----------
			if (p.getIndustry() != null) {
				dto.setIndustryId(p.getIndustry().getIndustryId());
				dto.setIndustryName(p.getIndustry().getIndustryName());
			}

			// ---------- Likes ----------
			dto.setLikedByUser(likedPropertyIds.contains(p.getId()));
			dto.setLikeCount(likeRepository.countLikesByPropertyId(p.getId()));

			dto.setGovernmentIdUrls(
					mediaFilesService
					.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.shootingGovermentId, p.getId())
					.stream()
					.map(FileOutputWebModel::getFilePath)
					.collect(Collectors.toList())
					);

			dto.setVerificationVideo(
					mediaFilesService
					.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.shootingLocationVerificationVideo, p.getId())
					.stream()
					.map(FileOutputWebModel::getFilePath)
					.collect(Collectors.toList())
					);

			// ---------- Reviews ----------
			ShootingLocationPropertyReviewResponseDTO reviewData =
					getReviewsByPropertyId(p.getId(), null);

			dto.setReviews(reviewData.getReviews());
			dto.setAverageRating(reviewData.getAverageRating());
			dto.setTotalReviews(reviewData.getTotalReviews());
			dto.setFiveStarPercentage(reviewData.getFiveStarPercentage());
			dto.setFourStarPercentage(reviewData.getFourStarPercentage());
			dto.setThreeStarPercentage(reviewData.getThreeStarPercentage());
			dto.setTwoStarPercentage(reviewData.getTwoStarPercentage());
			dto.setOneStarPercentage(reviewData.getOneStarPercentage());
			dtoList.add(dto);
		}

		return dtoList;
	}

	private Double getPrice(
			ShootingLocationPropertyDetails p,
			String propertyType,
			String priceType) {

		ShootingLocationSubcategorySelection s = p.getSubcategorySelection();
		if (s == null) return 0.0;

		if ("entire".equalsIgnoreCase(propertyType)) {
			if ("day".equalsIgnoreCase(priceType))
				return s.getEntireDayPropertyPrice() != null ? s.getEntireDayPropertyPrice() : 0.0;
			if ("night".equalsIgnoreCase(priceType))
				return s.getEntireNightPropertyPrice() != null ? s.getEntireNightPropertyPrice() : 0.0;
			if ("full".equalsIgnoreCase(priceType))
				return s.getEntireFullDayPropertyPrice() != null ? s.getEntireFullDayPropertyPrice() : 0.0;
		}

		if ("single".equalsIgnoreCase(propertyType)) {
			if ("day".equalsIgnoreCase(priceType))
				return s.getSingleDayPropertyPrice() != null ? s.getSingleDayPropertyPrice() : 0.0;
			if ("night".equalsIgnoreCase(priceType))
				return s.getSingleNightPropertyPrice() != null ? s.getSingleNightPropertyPrice() : 0.0;
			if ("full".equalsIgnoreCase(priceType))
				return s.getSingleFullDayPropertyPrice() != null ? s.getSingleFullDayPropertyPrice() : 0.0;
		}

		return 0.0;
	}

	@Override
	public Response approveProperty(Integer propertyId) {
		ShootingLocationPropertyDetails property =
				propertyDetailsRepository.findById(propertyId).orElse(null);

		if (property == null) {
			return new Response(-1, "Property not found", null);
		}

		// 2️⃣ Validate status
		if (property.getStatus() != ShootingPropertyStatus.PENDING) {
			return new Response(-1, "Property already processed", null);
		}

		Integer adminId = userDetails.userInfo().getId();

		property.setStatus(ShootingPropertyStatus.APPROVED);
		property.setApprovedBy(adminId);
		property.setApprovedOn(LocalDateTime.now());
		property.setRejectionReason(null);

		propertyDetailsRepository.save(property);
		try {

			User owner = property.getUser();

			if (owner != null) {

				Integer ownerId = owner.getUserId();
				String deviceToken = owner.getFirebaseDeviceToken();

				String notificationTitle = "Property Approved";
				String notificationMessage = 
						"Property " + property.getPropertyCode() + " (" + property.getPropertyName() + ") has been approved.";

				// -------- In-App Notification --------

				InAppNotification notification = InAppNotification.builder()
						.senderId(adminId)
						.receiverId(ownerId)
						.title(notificationTitle)
						.message(notificationMessage)
						.createdOn(new Date())
						.isRead(false)
						.adminReview(owner.getAdminReview())
						.Profession(owner.getUserType())
						.isDeleted(false)
						.createdBy(adminId)
						.userType("PROPERTY")
						.id(property.getId())   
						.build();

				inAppNotificationRepository.save(notification);

				// -------- Push Notification --------

				if (deviceToken != null && !deviceToken.trim().isEmpty()) {

					Notification notificationData = Notification.builder()
							.setTitle(notificationTitle)
							.setBody(notificationMessage)
							.build();

					AndroidNotification androidNotification = AndroidNotification.builder()
							.setIcon("ic_notification")
							.setColor("#00A2E8")
							.build();

					AndroidConfig androidConfig = AndroidConfig.builder()
							.setNotification(androidNotification)
							.build();

					Message message = Message.builder()
							.setNotification(notificationData)
							.setAndroidConfig(androidConfig)
							.putData("type", "property")
							.putData("propertyId", String.valueOf(property.getId()))
							.putData("status", "APPROVED")
							.setToken(deviceToken)
							.build();

					FirebaseMessaging.getInstance().send(message);
				}
			}

		} catch (Exception e) {
			logger.error("Error sending property approval notification", e);
		}
		adminService.log(
				adminId,
				"APPROVED",
				"SHOOTING_LOCATION",
				propertyId
				);

		return new Response(1, "Property approved successfully", null);
	}



	@Override
	public Response rejectProperty(Integer propertyId, String rejectionReason) {

		if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
			return new Response(-1, "Rejection reason is required", null);
		}

		ShootingLocationPropertyDetails property =
				propertyDetailsRepository.findById(propertyId).orElse(null);

		if (property == null) {
			return new Response(-1, "Property not found", null);
		}

		// 3️⃣ Validate current status
		if (property.getStatus() != ShootingPropertyStatus.PENDING) {
			return new Response(-1, "Property already processed", null);
		}
		Integer adminId = userDetails.userInfo().getId();

		property.setStatus(ShootingPropertyStatus.REJECTED);
		property.setApprovedBy(adminId);
		property.setApprovedOn(LocalDateTime.now());
		property.setRejectionReason(rejectionReason);
		propertyDetailsRepository.save(property);
		try {

			User owner = property.getUser();

			if (owner != null) {

				Integer ownerId = owner.getUserId();
				String deviceToken = owner.getFirebaseDeviceToken();

				String notificationTitle = "Property Rejected";
				String notificationMessage ="Property " + property.getPropertyCode() + " (" + property.getPropertyName() + ") was rejected.";

				// -------- In-App Notification --------

				InAppNotification notification = InAppNotification.builder()
						.senderId(adminId)
						.receiverId(ownerId)
						.title(notificationTitle)
						.message(notificationMessage)
						.createdOn(new Date())
						.isRead(false)
						.adminReview(owner.getAdminReview())
						.Profession(owner.getUserType())
						.isDeleted(false)
						.createdBy(adminId)
						.userType("PROPERTY")
						.id(property.getId())
						.build();

				inAppNotificationRepository.save(notification);

				// -------- Push Notification --------

				if (deviceToken != null && !deviceToken.trim().isEmpty()) {

					Notification notificationData = Notification.builder()
							.setTitle(notificationTitle)
							.setBody(notificationMessage)
							.build();

					AndroidNotification androidNotification = AndroidNotification.builder()
							.setIcon("ic_notification")
							.setColor("#00A2E8")
							.build();

					AndroidConfig androidConfig = AndroidConfig.builder()
							.setNotification(androidNotification)
							.build();

					Message message = Message.builder()
							.setNotification(notificationData)
							.setAndroidConfig(androidConfig)
							.putData("type", "property")
							.putData("propertyId", String.valueOf(property.getId()))
							.putData("status", "REJECTED")
							.setToken(deviceToken)
							.build();

					FirebaseMessaging.getInstance().send(message);
				}
			}

		} catch (Exception e) {
			logger.error("Error sending property rejection notification", e);
		}

		adminService.log(
				adminId,
				"REJECTED",
				"SHOOTING_LOCATION",
				propertyId
				);

		return new Response(1, "Property rejected successfully", null);
	}

	@Override
	public Response getPendingProperties(PropertyBookingType propertyType) {

		// 1️⃣ Fetch ALL pending properties
		List<ShootingLocationPropertyDetails> properties =
				propertyDetailsRepository
				.findByStatusOrderByIdDesc(ShootingPropertyStatus.PENDING);

		if (properties == null || properties.isEmpty()) {
			return new Response(1, "No pending properties found", List.of());
		}

		// 2️⃣ Filter by property type ONLY if passed
		if (propertyType != null) {

			properties = properties.stream()
					.filter(p -> {
						ShootingLocationSubcategorySelection s =
								p.getSubcategorySelection();
						if (s == null) return false;

						if (propertyType == PropertyBookingType.ENTIRE_PROPERTY) {
							return Boolean.TRUE.equals(s.getEntireProperty());
						}

						if (propertyType == PropertyBookingType.SINGLE_PROPERTY) {
							return Boolean.TRUE.equals(s.getSingleProperty());
						}

						return true;
					})
					.toList();
		}

		// 3️⃣ Convert to DTO + enrich with media & reviews
		List<ShootingLocationPropertyDetailsDTO> dtoList = new ArrayList<>();

		for (ShootingLocationPropertyDetails p : properties) {

			ShootingLocationPropertyDetailsDTO dto =
					shootingLocationPropertyConverter.entityToDto(p);

			// 🔹 Property images
			List<FileOutputWebModel> imageUrls = mediaFilesService
					.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.shootingLocationImage, p.getId());

			// 🔹 Government ID images
			List<String> govtIdUrls = mediaFilesService
					.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.shootingGovermentId, p.getId())
					.stream()
					.map(FileOutputWebModel::getFilePath)
					.collect(Collectors.toList());

			// 🔹 Verification video
			List<String> verificationVideo = mediaFilesService
					.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.shootingLocationVerificationVideo, p.getId())
					.stream()
					.map(FileOutputWebModel::getFilePath)
					.collect(Collectors.toList());

			dto.setImageUrls(imageUrls);
			dto.setGovernmentIdUrls(govtIdUrls);
			dto.setVerificationVideo(verificationVideo);

			dtoList.add(dto);
		}

		return new Response(1, "Pending properties fetched successfully", dtoList);
	}

	@Override
	public List<ShootingLocationPropertySummaryDTO> getPropertySummaryByTypesStatusAndUserType(
			Integer typesId,
			ShootingPropertyStatus status,
			String userType) {

		List<ShootingLocationPropertyDetails> list =
				propertyDetailsRepository.findByTypesStatusAndUserType(typesId, status, userType);

		return list.stream()
				.map(p -> ShootingLocationPropertySummaryDTO.builder()
						.id(p.getId())
						.propertyName(p.getPropertyName())
						.fullName(p.getFullName())
						.propertyCode(p.getPropertyCode())
						.approvedOn(p.getApprovedOn())
						.createdOn(p.getCreatedOn())
						.status(p.getStatus())
						.governmentPermission(p.getGovernmentPermission())
						.publicPermission(p.getPublicPermission())
						.build())
				.toList();
	}

	@Override
	@CacheEvict(value = "propertyCache", key = "#propertyId")
	public ShootingLocationPropertyDetailsDTO getPropertyById(Integer propertyId) {

		ShootingLocationPropertyDetails entity = 
				propertyDetailsRepository.fetchPropertyFull(propertyId);

		if (entity == null) return null;

		ShootingLocationPropertyDetailsDTO dto = 
				shootingLocationPropertyConverter.entityToDto(entity);

		Integer userId   = userDetails.userInfo().getId();
		String userType  = userDetails.userInfo().getUserType();
		boolean isOwner  = entity.getUser().getUserId().equals(userId);


		List<FileOutputWebModel> imageUrls = mediaFilesService
				.getAllMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationImage, propertyId);

		List<String> govtIdUrls = mediaFilesService
				.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingGovermentId, propertyId)
				.stream()
				.map(FileOutputWebModel::getFilePath)
				.collect(Collectors.toList());

		List<String> verificationVideo = mediaFilesService
				.getMediaFilesByCategoryAndRefId(MediaFileCategory.shootingLocationVerificationVideo, propertyId)
				.stream()
				.map(FileOutputWebModel::getFilePath)
				.collect(Collectors.toList());		


		dto.setImageUrls(imageUrls);
		dto.setGovernmentIdUrls(govtIdUrls);
		dto.setVerificationVideo(verificationVideo);
		dto.setApprovedBy(userRepository.findNameById(entity.getApprovedBy()));
		ShootingLocationPropertyReviewResponseDTO reviewResponse =
				getReviewsByPropertyId(propertyId, userId);

		dto.setReviews(reviewResponse.getReviews());
		dto.setAverageRating(reviewResponse.getAverageRating());
		dto.setTotalReviews(reviewResponse.getTotalReviews());
		dto.setFiveStarPercentage(reviewResponse.getFiveStarPercentage());
		dto.setFourStarPercentage(reviewResponse.getFourStarPercentage());
		dto.setThreeStarPercentage(reviewResponse.getThreeStarPercentage());
		dto.setTwoStarPercentage(reviewResponse.getTwoStarPercentage());
		dto.setOneStarPercentage(reviewResponse.getOneStarPercentage());

		List<PropertyLike> userLikes = likeRepository.findByLikedById(userId);

		Set<Integer> likedPropertyIds = userLikes.stream()
				.filter(PropertyLike::getStatus)
				.map(l -> l.getProperty().getId())
				.collect(Collectors.toSet());

		dto.setLikedByUser(likedPropertyIds.contains(propertyId));

		// Total likes (use existing property-based method)
		dto.setLikeCount(
				likeRepository.countLikesByPropertyId(propertyId)
				);


		/* ======================================================
		 * 4) AVAILABILITY (USE YOUR EXISTING METHOD)
		 * ====================================================== */
		//	    dto.setAvailabilityDates(
		//	            getPropertyAvailability(propertyId)
		//	    );


		/* ======================================================
		 * 5) ADMIN RATING
		 * ====================================================== */
		dto.setAdminRating(entity.getAdminRating());
		dto.setAdminRatedOn(entity.getAdminRatedOn());
		dto.setAdminRatedBy(entity.getAdminRatedBy());


		return dto;
	}


	@Override
	@Transactional
	public Response reviewShootingLocationMedia(ShootingPropertyMediaRequest request) {

		Integer userId = userDetails.userInfo().getId();

		MediaFiles media = mediaFilesRepository.findById(request.getMediaId())
				.orElse(null);

		if (media == null) {
			return new Response(-1, "Media not found", null);
		}

		// 🚨 Mandatory flag check (VERY IMPORTANT)
		if (request.getApproved() == null) {
			return new Response(-1, "Approved flag is required", null);
		}

		// ❌ Reject
		if (!request.getApproved()) {

			if (request.getReason() == null || request.getReason().isBlank()) {
				return new Response(-1, "Rejection reason is required", null);
			}

			media.setStatus(false);
			media.setDescription(request.getReason());
			media.setFileStatus(FileStatus.REJECTED);;

		}
		// ✅ Approve
		else {
			media.setStatus(true);
			media.setDescription(null);
			media.setFileStatus(FileStatus.APPROVED);
		}

		media.setUpdatedBy(userId);
		media.setUpdatedOn(new Date());

		mediaFilesRepository.save(media);

		return new Response(1, "Media review updated successfully", null);
	}


	@Transactional
	@Override
	public Response updatePermission(ShootingLocationPropertyDetailsDTO dto) {

		ShootingLocationPropertyDetails property =
				propertyDetailsRepository.findById(dto.getId()).orElse(null);

		if (property == null) {
			return new Response(-1, "Property not found", null);
		}

		// Update Government Permission
		if (dto.getGovernmentPermission() != null) {
			property.setGovernmentPermission(dto.getGovernmentPermission());
		}

		// Update Public Permission
		if (dto.getPublicPermission() != null) {
			property.setPublicPermission(dto.getPublicPermission());
		}

		propertyDetailsRepository.save(property);

		return new Response(1, "Permission updated successfully", null);
	}

	@Override
	public List<ShootingLocationPropertyReviewDTO> getReviewsByPropertyId(Integer propertyId) {

		List<ShootingLocationPropertyReview> reviews = propertyReviewRepository.findByProperty_IdOrderByCreatedOnDesc(propertyId);

		return reviews.stream().map(review -> {

			return ShootingLocationPropertyReviewDTO.builder()
					.id(review.getId())
					.propertyId(review.getProperty().getId())
					.userId(review.getUser().getUserId())
					.userName(review.getUser().getName())
					.profilePicUrl(userService.getProfilePicUrl(review.getUser().getUserId()))
					.rating(review.getRating())
					.reviewText(review.getReviewText())
					.createdOn(review.getCreatedOn())
					.ownerReplyText(review.getOwnerReplyText())
					.ownerReplyOn(review.getOwnerReplyOn())
					.ownerReplyBy(
							review.getOwnerReplyBy() != null ?
									review.getOwnerReplyBy().getUserId() : null)
					.ownerReplyByName(
							review.getOwnerReplyBy() != null ?
									review.getOwnerReplyBy().getName() : null)
					.build();

		}).collect(Collectors.toList());
	}


	@Override
	public ShootingLocationPropertyReviewResponseDTO getAllReviewsByPropertyId(Integer propertyId) {

		List<ShootingLocationPropertyReview> reviews = propertyReviewRepository.findAllByPropertyId(propertyId);

		long totalReviews = reviews.size();

		long five = 0, four = 0, three = 0, two = 0, one = 0;
		double averageRating = 0;

		if (totalReviews > 0) {

			int totalRatingSum = 0;

			for (ShootingLocationPropertyReview r : reviews) {

				totalRatingSum += r.getRating();

				switch (r.getRating()) {
				case 5: five++; break;
				case 4: four++; break;
				case 3: three++; break;
				case 2: two++; break;
				case 1: one++; break;
				}
			}

			averageRating = (double) totalRatingSum / totalReviews;
		}

		double fivePercent = totalReviews > 0 ? (five * 100.0) / totalReviews : 0;
		double fourPercent = totalReviews > 0 ? (four * 100.0) / totalReviews : 0;
		double threePercent = totalReviews > 0 ? (three * 100.0) / totalReviews : 0;
		double twoPercent = totalReviews > 0 ? (two * 100.0) / totalReviews : 0;
		double onePercent = totalReviews > 0 ? (one * 100.0) / totalReviews : 0;

		List<ShootingLocationPropertyReviewDTO> reviewDTOList =
				reviews.stream()
				.map(r -> {

					List<FileOutputWebModel> mediaDTOs =
							mediaFilesService.getMediaFilesByCategoryAndRefId(
									MediaFileCategory.ShootingLocationReview,
									r.getId()
									);

					return ShootingLocationPropertyReviewDTO.builder()
							.id(r.getId())
							.propertyId(propertyId)
							.userId(r.getUser().getUserId())
							.userName(r.getUser() != null ? r.getUser().getName() : "")
							.profilePicUrl(
									userService.getProfilePicUrl(
											r.getUser().getUserId()))
							.rating(r.getRating())
							.reviewText(r.getReviewText())
							.createdOn(r.getCreatedOn())
							.ownerReplyText(r.getOwnerReplyText())
							.ownerReplyOn(r.getOwnerReplyOn())
							.ownerReplyBy(
									r.getOwnerReplyBy() != null ?
											r.getOwnerReplyBy().getUserId() : null)
							.ownerReplyByName(
									r.getOwnerReplyBy() != null ?
											r.getOwnerReplyBy().getName() : null)
							.files(mediaDTOs)
							.build();
				})
				.collect(Collectors.toList());

		return ShootingLocationPropertyReviewResponseDTO.builder()
				.averageRating(averageRating)
				.totalReviews(totalReviews)
				.fiveStarPercentage(fivePercent)
				.fourStarPercentage(fourPercent)
				.threeStarPercentage(threePercent)
				.twoStarPercentage(twoPercent)
				.oneStarPercentage(onePercent)
				.reviews(reviewDTOList)
				.build();
	}

	//	=========================================================================================================================================

	@Override
	public List<ShootingLocationBookingDTO> getOwnerBookings(Integer ownerId, BookingStatus status) {

		List<ShootingLocationBooking> bookings;

		if (status == BookingStatus.ACTIVE) {

			bookings = bookingRepository.findByOwnerAndStatuses(
					ownerId,
					List.of(
							BookingStatus.APPROVED,
							BookingStatus.CONFIRMED,
							BookingStatus.FAILED
							)
					);

		} else {

			bookings = bookingRepository.findBookingsForOwner(ownerId, status);
		}

		return bookings.stream()
				.map(shootingLocationBookingConverter::convertToDTO)
				.toList();
	}


	public Response  updateBookingStatus( Integer bookingId, Integer ownerId, BookingStatus newStatus) {

		// Fetch booking
		ShootingLocationBooking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		// Validate owner
		if (!booking.getProperty().getUser().getUserId().equals(ownerId)) {
			throw new RuntimeException("You are not authorized to update this booking");
		}
		if (booking.getStatus() != BookingStatus.PENDING) {
			throw new RuntimeException("Only PENDING bookings can be approved or rejected");
		}
		//  Validate status update request
		if (newStatus != BookingStatus.APPROVED &&
				newStatus != BookingStatus.REJECTED) {

			throw new RuntimeException("Invalid status update. Only APPROVED or REJECTED allowed.");
		}

		String message="";
		// ------------------------------------------------
		// OWNER APPROVES BOOKING
		// ------------------------------------------------

		if (newStatus == BookingStatus.APPROVED) {

			if (Boolean.TRUE.equals(booking.getModificationRequested())) {

				// Apply edited dates
				booking.setConfirmedBookingDates(
						new ArrayList<>(booking.getBookingDates())
						);

				booking.setModificationRequested(false);

				// 🔥 Keep booking CONFIRMED
				booking.setStatus(BookingStatus.CONFIRMED);
				message = "Booking approved successfully";
			}

			else {

				// First time approval
				booking.setConfirmedBookingDates(
						new ArrayList<>(booking.getBookingDates())
						);

				booking.setStatus(BookingStatus.APPROVED);
				message = "Booking approved successfully";
			}
		}
		// ------------------------------------------------
		// REJECT
		// ------------------------------------------------
		if (newStatus == BookingStatus.REJECTED) {

			if (Boolean.TRUE.equals(booking.getModificationRequested())) {

				// Restore previous confirmed dates
				booking.setBookingDates(
						new ArrayList<>(booking.getConfirmedBookingDates())
						);

				booking.setModificationRequested(false);

				// 🔥 Status stays CONFIRMED
				booking.setStatus(BookingStatus.CONFIRMED);
				message = "Booking rejected successfully";
			}

			else {
				booking.setStatus(BookingStatus.REJECTED);
				message = "Booking rejected successfully";
			}
		}

		booking.setUpdatedAt(LocalDateTime.now());

		booking = bookingRepository.save(booking);

		try {

			User client = booking.getClient();

			if (client != null) {

				Integer clientId = client.getUserId();
				String deviceToken = client.getFirebaseDeviceToken();

				String notificationTitle = "Booking Update";
				String notificationMessage = "";

				if (booking.getStatus() == BookingStatus.APPROVED) {

					notificationMessage =
							"Your booking is approved. Pay now to confirm.";

				} else if (booking.getStatus() == BookingStatus.REJECTED) {

					notificationMessage =
							"Your booking request has been declined by the owner.";
				}

				// ------------------------------------------------
				// 1️⃣ Save In-App Notification
				// ------------------------------------------------

				InAppNotification notification = InAppNotification.builder()
						.senderId(ownerId)
						.receiverId(clientId)
						.title(notificationTitle)
						.message(notificationMessage)
						.createdOn(new Date())
						.isRead(false)
						.adminReview(booking.getProperty().getUser().getAdminReview())
						.Profession(booking.getProperty().getUser().getUserType())
						.isDeleted(false)
						.createdBy(ownerId)
						.userType("BOOKING")
						.id(booking.getId())
						.postId(null)
						.build();

				inAppNotificationRepository.save(notification);

				// ------------------------------------------------
				// 2️⃣ Send FCM Push Notification
				// ------------------------------------------------

				if (deviceToken != null && !deviceToken.trim().isEmpty()) {

					Notification notificationData = Notification.builder()
							.setTitle(notificationTitle)
							.setBody(notificationMessage)
							.build();

					AndroidNotification androidNotification = AndroidNotification.builder()
							.setIcon("ic_notification")
							.setColor("#00A2E8")
							.build();

					AndroidConfig androidConfig = AndroidConfig.builder()
							.setNotification(androidNotification)
							.build();

					Message messagePush = Message.builder()
							.setNotification(notificationData)
							.setAndroidConfig(androidConfig)
							.putData("type", "booking")
							.putData("bookingId", String.valueOf(booking.getId()))
							.putData("status", booking.getStatus().name())
							.putData("propertyId", String.valueOf(
									booking.getProperty().getId()))
							.setToken(deviceToken)
							.build();

					FirebaseMessaging.getInstance().send(messagePush);
				}

			}

		} catch (Exception e) {
			logger.error("Error sending booking notification", e);
		}

		// ======================================================

		ShootingLocationBookingDTO dto =
				ShootingLocationBookingConverter.toDTO(booking);

		return new Response(1, message, dto);
	}

	@Override
	public Response getClientBookingsByStatus(Integer clientId, BookingStatus status) {

		try {

			List<ShootingLocationBooking> bookings =
					bookingRepository
					.findByClient_UserIdAndStatusAndDeletedByClientFalseOrderByUpdatedAtDesc(
							clientId, status);

			List<ShootingLocationBookingDTO> dtoList = bookings.stream()
					.map(shootingLocationBookingConverter::convertToDTO)
					.toList();

			return new Response(1, "Filtered bookings fetched successfully", dtoList);

		} catch (Exception e) {

			return new Response(-1, "Error", e.getMessage());
		}
	}

	@Override
	public ShootingLocationBookingDTO cancelBooking(  Integer bookingId, Integer clientId, String reason) {

		Integer userId = userDetails.userInfo().getId();
		ShootingLocationBooking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		// Validate owner of booking
		if (!booking.getClient().getUserId().equals(clientId)) {
			throw new RuntimeException("You cannot cancel this booking");
		}

		// Only allow cancellation before payment
		if (booking.getStatus() != BookingStatus.PENDING &&
				booking.getStatus() != BookingStatus.APPROVED) {

			throw new RuntimeException(
					"Booking cannot be cancelled after confirmation/payment");
		}

		booking.setStatus(BookingStatus.CANCELLED);
		booking.setCancelledAt(LocalDateTime.now());
		booking.setCancellationReason(reason);
		booking.setCancelledBy(userId);

		booking.setUpdatedAt(LocalDateTime.now());

		booking = bookingRepository.save(booking);
		try {

			User owner = booking.getProperty().getUser();

			if (owner != null) {

				Integer ownerId = owner.getUserId();
				String deviceToken = owner.getFirebaseDeviceToken();

				String notificationTitle = "Booking Cancelled";
				String notificationMessage = "A client has cancelled their booking request.";

				// ------------------------------------------------
				// 1️⃣ Save In-App Notification
				// ------------------------------------------------

				InAppNotification notification = InAppNotification.builder()

						.senderId(clientId)
						.receiverId(ownerId)
						.title(notificationTitle)
						.message(notificationMessage)
						.createdOn(new Date())
						.isRead(false)
						.adminReview(owner.getAdminReview())
						.Profession(owner.getUserType())
						.isDeleted(false)
						.createdBy(clientId)
						.userType("BOOKING")
						.id(booking.getId())
						.postId(null)
						.build();

				inAppNotificationRepository.save(notification);

				// ------------------------------------------------
				// 2️⃣ Push Notification (FCM)
				// ------------------------------------------------

				if (deviceToken != null && !deviceToken.trim().isEmpty()) {

					Notification notificationData = Notification.builder()
							.setTitle(notificationTitle)
							.setBody(notificationMessage)
							.build();

					AndroidNotification androidNotification = AndroidNotification.builder()
							.setIcon("ic_notification")
							.setColor("#00A2E8")
							.build();

					AndroidConfig androidConfig = AndroidConfig.builder()
							.setNotification(androidNotification)
							.build();

					Message messagePush = Message.builder()
							.setNotification(notificationData)
							.setAndroidConfig(androidConfig)
							.putData("type", "booking")
							.putData("bookingId", String.valueOf(booking.getId()))
							.putData("status", booking.getStatus().name())
							.putData("action", "BOOKING_CANCELLED")
							.setToken(deviceToken)
							.build();

					FirebaseMessaging.getInstance().send(messagePush);
				}

			}

		} catch (Exception e) {
			logger.error("Error sending cancellation notification", e);
		}


		return ShootingLocationBookingConverter.toDTO(booking);
	}

	@Override
	public void deleteBooking(Integer bookingId, Integer userId) {

		ShootingLocationBooking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (booking.getClient().getUserId().equals(userId)) {

			booking.setDeletedByClient(true);

		} else if (booking.getProperty().getUser().getUserId().equals(userId)) {

			booking.setDeletedByOwner(true);

		} else {

			throw new RuntimeException("Not authorized");
		}

		booking.setUpdatedAt(LocalDateTime.now());

		bookingRepository.save(booking);
	}

	@Override
	public void generateShootOtp(String bookingCode, Integer ownerId) {

		ShootingLocationBooking booking = bookingRepository.findByBookingCode(bookingCode)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (!booking.getProperty().getUser().getUserId().equals(ownerId)) {
			throw new RuntimeException("You are not authorized to generate OTP");
		}

		if (booking.getStatus() != BookingStatus.CONFIRMED) {
			throw new RuntimeException("OTP can only be generated for confirmed bookings");
		}
		// 🔹 If already verified
		if (Boolean.TRUE.equals(booking.getShootVerified())) {
			throw new RuntimeException("Shoot already verified for this booking");
		}


		if (booking.getConfirmedBookingDates() == null || booking.getConfirmedBookingDates().isEmpty()) {
			throw new RuntimeException("No confirmed booking dates available");
		}

		// 🔹 Get first shoot date
		LocalDate firstShootDate = booking.getConfirmedBookingDates()
				.stream()
				.sorted()
				.findFirst()
				.get();

		if (!LocalDate.now().equals(firstShootDate)) {
			throw new RuntimeException("OTP can only be generated on the first shoot date");
		}

		String otp = String.valueOf(new java.util.Random().nextInt(900000) + 100000);

		booking.setShootOtp(otp);

		bookingRepository.save(booking);

		String subject = "FilmHook – Shoot Verification OTP";

		String content =
				"<p>Your shooting session verification details are below:</p>" +
						"<p><b>Booking Code:</b> " + booking.getBookingCode() + "</p>" +
						"<p><b>Shoot Date:</b> " + firstShootDate + "</p>" +
						"<p>Your One-Time Password (OTP) to start the shoot is:</p>" +
						"<h2 style='color:#2c7be5;'>" + otp + "</h2>" +
						"<p>Please share this OTP with the property owner for verification.</p>";

		mailNotification.sendEmailSync(
				booking.getClient().getName(),
				booking.getClient().getEmail(),
				subject,
				content
				);
	}

	@Override
	public void verifyShootOtp(String bookingCode, String otp) {

		ShootingLocationBooking booking = bookingRepository.findByBookingCode(bookingCode)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (booking.getStatus() != BookingStatus.CONFIRMED) {
			throw new RuntimeException("Booking not eligible for verification");
		}

		if (booking.getConfirmedBookingDates() == null || booking.getConfirmedBookingDates().isEmpty()) {
			throw new RuntimeException("No confirmed booking dates available");
		}

		LocalDate firstShootDate = booking.getConfirmedBookingDates()
				.stream()
				.sorted()
				.findFirst()
				.get();

		if (!LocalDate.now().equals(firstShootDate)) {
			throw new RuntimeException("OTP verification allowed only on first shoot date");
		}

		if (booking.getShootOtp() == null) {
			throw new RuntimeException("OTP not generated for this booking");
		}

		if (!otp.equals(booking.getShootOtp())) {
			throw new RuntimeException("Invalid OTP");
		}

		booking.setShootVerified(true);
		booking.setStatus(BookingStatus.IN_PROGRESS);
		booking.setShootOtp(null);

		bookingRepository.save(booking);

		// Email notification
		String subject = "FilmHook – Shoot Started Successfully";

		String content =
				"<p>Your shooting session has been successfully verified by the property owner.</p>" +
						"<p><b>Booking Code:</b> " + booking.getBookingCode() + "</p>" +
						"<p>The shoot has now officially started.</p>";

		mailNotification.sendEmailSync(
				booking.getClient().getName(),
				booking.getClient().getEmail(),
				subject,
				content
				);


	}

	@Override
	public void generateCompletionOtp(String bookingCode, Integer ownerId) {

		ShootingLocationBooking booking = bookingRepository.findByBookingCode(bookingCode)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (!booking.getProperty().getUser().getUserId().equals(ownerId)) {
			throw new RuntimeException("You are not authorized");
		}

		if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
			throw new RuntimeException("Booking must be in progress");
		}

		if (booking.getConfirmedBookingDates() == null || booking.getConfirmedBookingDates().isEmpty()) {
			throw new RuntimeException("No confirmed booking dates");
		}

		// 🔹 Get last shoot date
		LocalDate lastShootDate = booking.getConfirmedBookingDates()
				.stream()
				.sorted()
				.reduce((first, second) -> second)
				.get();

		if (!LocalDate.now().equals(lastShootDate)) {
			throw new RuntimeException("Completion allowed only on last shoot date");
		}

		String otp = String.valueOf(new java.util.Random().nextInt(900000) + 100000);

		booking.setShootOtp(otp);

		bookingRepository.save(booking);

		String subject = "FilmHook – Shoot Completion OTP";

		String content =
				"<p>Your shoot session is ready for completion.</p>" +
						"<p><b>Booking Code:</b> " + booking.getBookingCode() + "</p>" +
						"<p>Your OTP to complete the shoot is:</p>" +
						"<h2 style='color:#2c7be5'>" + otp + "</h2>" +
						"<p>Please share this OTP with the property owner.</p>";

		mailNotification.sendEmailSync(
				booking.getClient().getName(),
				booking.getClient().getEmail(),
				subject,
				content
				);
	}


	@Override
	public void verifyCompletionOtp(String bookingCode, String otp) {

		ShootingLocationBooking booking = bookingRepository.findByBookingCode(bookingCode)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
			throw new RuntimeException("Booking not in progress");
		}

		if (booking.getShootOtp() == null) {
			throw new RuntimeException("OTP not generated");
		}

		if (!otp.equals(booking.getShootOtp())) {
			throw new RuntimeException("Invalid OTP");
		}

		booking.setStatus(BookingStatus.COMPLETED);
		booking.setShootOtp(null);

		bookingRepository.save(booking);

		// Rating link
		String ratingLink =
				"https://filmhookapp.com/rate-property?propertyId="
						+ booking.getProperty().getId();

		String subject = "FilmHook – Rate Your Shooting Location";

		String content =
				"<p>Your shooting session has been completed successfully.</p>" +
						"<p><b>Booking Code:</b> " + booking.getBookingCode() + "</p>" +
						"<p>Please rate the property:</p>" +
						"<a href='" + ratingLink + "' style='padding:10px 15px;background:#2c7be5;color:white;text-decoration:none;border-radius:5px;'>Rate Property</a>";

		mailNotification.sendEmailSync(
				booking.getClient().getName(),
				booking.getClient().getEmail(),
				subject,
				content
				);
	}
}