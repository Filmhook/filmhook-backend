package com.annular.filmhook.converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.annular.filmhook.model.MarketPlaceCategories;
import com.annular.filmhook.model.MarketPlaceLikes;
import com.annular.filmhook.model.MarketPlaceProductDynamicAttribute;
import com.annular.filmhook.model.MarketPlaceProductReview;
import com.annular.filmhook.model.MarketPlaceProducts;
import com.annular.filmhook.model.MarketPlaceSubCategories;
import com.annular.filmhook.model.MarketPlaceSubCategoryFields;
import com.annular.filmhook.model.SellerInfo;
import com.annular.filmhook.model.SellerMediaFile;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MarketPlaceSubCategoryFiledsRepository;
import com.annular.filmhook.repository.MarketPlaceSubCategoryRepository;
import com.annular.filmhook.webmodel.MarketPlaceCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceDynamicAttributeDTO;
import com.annular.filmhook.webmodel.MarketPlaceLikesDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductReviewDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryFieldDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarketPlaceProductConverter {

	private static MarketPlaceSubCategoryRepository subCategoryRepo;


	/* Marketplacecategory */
	public static MarketPlaceCategoryDTO toDto(MarketPlaceCategories category) {
		return MarketPlaceCategoryDTO.builder()
				.id(category.getId())
				.name(category.getName())
				.build();
	}

	public static MarketPlaceCategories toEntity(MarketPlaceCategoryDTO dto) {
		return MarketPlaceCategories.builder()
				.id(dto.getId())
				.name(dto.getName())
				.build();
	}

	/* MarketPlaceSubCategory */
	public static MarketPlaceSubCategories toEntity(MarketPlaceSubCategoryDTO dto) {
		MarketPlaceSubCategories entity = new MarketPlaceSubCategories();
		entity.setId(dto.getId());
		entity.setName(dto.getName());

		if (dto.getCategoryId() != null) {
			MarketPlaceCategories category = new MarketPlaceCategories();
			category.setId(dto.getCategoryId());
			entity.setCategory(category);
		}

		return entity;
	}
	public static MarketPlaceSubCategoryDTO toDto(MarketPlaceSubCategories entity) {
		Integer categoryId = (entity.getCategory() != null) ? entity.getCategory().getId() : null;

		return new MarketPlaceSubCategoryDTO(
				entity.getId(),
				entity.getName(),
				categoryId
				);
	}

	/* MarketplaceSubcategoryfileds */
	public static MarketPlaceSubCategoryFields toEntity(
			MarketPlaceSubCategoryFieldDTO dto,
			MarketPlaceSubCategoryRepository subCategoryRepo) {

		MarketPlaceSubCategoryFields entity = new MarketPlaceSubCategoryFields();
		entity.setId(dto.getId());
		entity.setFieldKey(dto.getFieldKey());
		//		entity.setLabel(dto.getLabel());
		entity.setType(dto.getType());
		entity.setRequired(dto.isRequired());
		entity.setSection(dto.getSection());
		entity.setOptions(dto.getOptions());


		if (dto.getSubCategoryId() != null) {
			MarketPlaceSubCategories subCategory = subCategoryRepo.findById(dto.getSubCategoryId())
					.orElseThrow(() -> new RuntimeException("SubCategory not found with id: " + dto.getSubCategoryId()));
			entity.setSubCategories(subCategory);
		} else {
			throw new IllegalArgumentException("subCategoryId must not be null");
		}

		return entity;
	}

	public static MarketPlaceSubCategoryFieldDTO toDto(MarketPlaceSubCategoryFields entity) {
		Integer subCategoryId = (entity.getSubCategories() != null) ? entity.getSubCategories().getId() : null;

		return new MarketPlaceSubCategoryFieldDTO(
				entity.getId(),
				entity.getFieldKey(),
				//				entity.getLabel(),
				entity.getType(),
				entity.isRequired(),
				entity.getSection(),
				entity.getOptions(),
				subCategoryId
				);
	}

	// Convert Request DTO to Entity MarketPlaceProducts
	public static MarketPlaceProducts toEntity(MarketPlaceProductDTO dto, MarketPlaceSubCategories subCategory, SellerInfo seller) {

		MarketPlaceProducts product = MarketPlaceProducts.builder()
				.id(dto.getId())
				.brandName(dto.getBrandName())
				.modelName(dto.getModelName())
				.price(dto.getPrice())
				.availability(dto.getAvailability())
				.seller(seller)
				.subCategory(subCategory)
				.createdBy(dto.getSellerId())
				.additionalDetails(dto.getAdditionalDetails())
				.status(dto.getStatus())

				.build();
		if (dto.getDynamicAttributes() != null && !dto.getDynamicAttributes().isEmpty()) {
			List<MarketPlaceProductDynamicAttribute> attributes = dto.getDynamicAttributes().stream()
					.map(attr -> MarketPlaceProductDynamicAttribute.builder()
							.fieldKey(attr.getFieldKey())
							.value(attr.getValue())
							.section(attr.getSection())
							.product(product)
							.build())
					.collect(Collectors.toList());
			product.setDynamicAttributes(attributes);
		}

		return product;
	}


	public static MarketPlaceProductDTO toDTO(MarketPlaceProducts entity) {
	    return toDTO(entity, null); // default: no current user
	}
	
	
	// Convert Entity to DTO
	public static MarketPlaceProductDTO toDTO(MarketPlaceProducts entity, Integer currentUserId) {

		Map<String, List<MarketPlaceDynamicAttributeDTO>> groupedAttributes = new LinkedHashMap<>();
		if (entity.getDynamicAttributes() != null) {
			for (MarketPlaceProductDynamicAttribute attr : entity.getDynamicAttributes()) {
				String section = attr.getSection() != null ? attr.getSection() : "Other";
				groupedAttributes
				.computeIfAbsent(section, k -> new ArrayList<>())
				.add(new MarketPlaceDynamicAttributeDTO(attr.getFieldKey(), attr.getValue(), section));
			}
		}

		List<String> imageUrls = new ArrayList<>();
		List<String> videoUrls = new ArrayList<>();

		List<SellerMediaFile> mediaFiles = entity.getMediaList();

		if (mediaFiles != null) {
			for (SellerMediaFile media : mediaFiles) {
				String mediaCategory = media.getCategory();
				if (mediaCategory != null) {
					if (mediaCategory.equalsIgnoreCase("PRODUCT_IMAGE")) {
						imageUrls.add(media.getFilePath());
					} else if (mediaCategory.equalsIgnoreCase("PRODUCT_VIDEO") && media.getFilePath() != null && !media.getFilePath().isEmpty()) {
						videoUrls.add(media.getFilePath());
					}
				}
			}
		}

		String sellerFullName = null;
		String sellerEmail = null;
		if (entity.getSeller() != null) {
			SellerInfo seller = entity.getSeller();
			sellerFullName = (seller.getFirstName() != null ? seller.getFirstName() + " " : "") +
					(seller.getMiddleName() != null ? seller.getMiddleName() + " " : "") +
					(seller.getLastName() != null ? seller.getLastName() : "");

			if (seller.getUser() != null) {
				sellerEmail = seller.getUser().getEmail();
			}
		}
		
		   List<MarketPlaceProductReviewDTO> reviewDTOs = new ArrayList<>();
		    double avgRating = 0.0;
		    if (entity.getReviews() != null && !entity.getReviews().isEmpty()) {
		        int total = 0;
		        for (MarketPlaceProductReview review : entity.getReviews()) {
		            total += review.getRating();
		            reviewDTOs.add(MarketPlaceProductReviewDTO.builder()
		                    .productId(entity.getId())
		                    .userId(review.getUser().getUserId())
		                    .userName(review.getUser().getName()) 
		                    .rating(review.getRating())
		                    .reviewText(review.getReviewText())
		                    .build());
		        }
		        avgRating = (double) total / entity.getReviews().size();
		    }
		    
		    Boolean isLiked = false;
		    if (currentUserId != null && entity.getLikes() != null) {
		        isLiked = entity.getLikes().stream()
		            .anyMatch(like -> like.getLikedBy().getUserId().equals(currentUserId) && Boolean.TRUE.equals(like.getStatus()));
		    }


		return MarketPlaceProductDTO.builder()
				.id(entity.getId())
				.subCategoryId(entity.getSubCategory() != null ? entity.getSubCategory().getId() : null)
				.brandName(entity.getBrandName())
				.modelName(entity.getModelName())
				.price(entity.getPrice())
				.availability(entity.getAvailability())
				.imageUrls(imageUrls)
				.groupedAttributes(groupedAttributes)
				.videoUrls(videoUrls)
				.createdBy(entity.getSeller() != null && entity.getSeller().getId() != null 
		           ? entity.getSeller().getId().intValue() 
		           : null)
				.status(entity.getStatus())
				.sellerId(entity.getSeller() != null ? entity.getSeller().getId() : null)
				.sellerFullName(sellerFullName)
				.sellerEmail(sellerEmail)
				.subCategoryName(entity.getSubCategory() != null ? entity.getSubCategory().getName() : null)
				.additionalDetails(entity.getAdditionalDetails())
				.averageRating(avgRating)
				.reviews(reviewDTOs)
				.likedByUser(isLiked)
				.build();
	}
	
	
//	reviews
	 public static MarketPlaceProductReview toEntity(MarketPlaceProductReviewDTO dto, MarketPlaceProducts product, User user) {
	        return MarketPlaceProductReview.builder()
	                .rating(dto.getRating())
	                .reviewText(dto.getReviewText())
	                .product(product)
	                .user(user)
	                .build();
	    }

	    // Convert Entity to DTO
	    public static MarketPlaceProductReviewDTO toDTO(MarketPlaceProductReview review) {
	        return MarketPlaceProductReviewDTO.builder()
	                .productId(review.getProduct().getId())
	                .userId(review.getUser().getUserId())
	                .userName(review.getUser().getName())
	                .rating(review.getRating())
	                .reviewText(review.getReviewText())
	                .userName(review.getUser().getName())
	                .build();
	    }
	    	    
	    public static MarketPlaceLikes toEntity(MarketPlaceLikesDTO dto, MarketPlaceProducts product, User user) {
	        return MarketPlaceLikes.builder()
	                .product(product)
	                .likedBy(user)
	               	.createdBy(dto.getUserId())
	                .liveDate(LocalDateTime.now().toString())
	                .build();
	    }
}
