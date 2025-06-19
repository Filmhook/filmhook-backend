package com.annular.filmhook.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.annular.filmhook.model.MarketPlaceCategories;
import com.annular.filmhook.model.MarketPlaceProducts;
import com.annular.filmhook.model.MarketPlaceSubCategories;
import com.annular.filmhook.model.MarketPlaceSubCategoryFields;
import com.annular.filmhook.model.SellerInfo;
import com.annular.filmhook.model.SellerMediaFile;
import com.annular.filmhook.repository.MarketPlaceSubCategoryFiledsRepository;
import com.annular.filmhook.repository.MarketPlaceSubCategoryRepository;
import com.annular.filmhook.webmodel.MarketPlaceCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductDTO;
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
	        entity.setLabel(dto.getLabel());
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
	                entity.getLabel(),
	                entity.getType(),
	                entity.isRequired(),
	                entity.getSection(),
	                entity.getOptions(),
	                subCategoryId
	        );
	    }
	    
	    
	    // Convert Request DTO to Entity MarketPlaceProducts
	    public static MarketPlaceProducts toEntity(MarketPlaceProductDTO dto, MarketPlaceSubCategories subCategory, SellerInfo seller) {
	        ObjectMapper objectMapper = new ObjectMapper();
	        String dynamicAttributesJson = null;

	        try {
	            if (dto.getDynamicAttributesJson() != null) {
	                dynamicAttributesJson = objectMapper.writeValueAsString(dto.getDynamicAttributesJson());
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("Failed to convert dynamicAttributesJson to JSON string", e);
	        }

	        return MarketPlaceProducts.builder()
	                .id(dto.getId())
	                .brandName(dto.getBrandName())
	                .modelName(dto.getModelName())
	                .price(dto.getPrice())
	                .availability(dto.getAvailability())
	                .dynamicAttributesJson(dynamicAttributesJson)
	                .seller(seller)
	                .subCategory(subCategory)
	                .createdBy(dto.getSellerId())
	              
	                .build();
	    }


	    // Convert Entity to DTO
	    public static MarketPlaceProductDTO toDTO(MarketPlaceProducts entity) {
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, Object> dynamicAttributesMap = null;

	        try {
	            if (entity.getDynamicAttributesJson() != null) {
	                dynamicAttributesMap = objectMapper.readValue(entity.getDynamicAttributesJson(), HashMap.class);
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to parse dynamicAttributesJson", e);
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
	                    } else if (mediaCategory.equalsIgnoreCase("PRODUCT_VIDEO")) {
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

	        return MarketPlaceProductDTO.builder()
	                .id(entity.getId())
	                .subCategoryId(entity.getSubCategory() != null ? entity.getSubCategory().getId() : null)
	                .brandName(entity.getBrandName())
	                .modelName(entity.getModelName())
	                .price(entity.getPrice())
	                .availability(entity.getAvailability())
	                .dynamicAttributesJson(dynamicAttributesMap)
	                .imageUrls(imageUrls)
	                .videoUrls(videoUrls)
	                .sellerId(entity.getSeller() != null ? entity.getSeller().getId() : null)
	                .sellerFullName(sellerFullName)
	                .sellerEmail(sellerEmail)
	                .subCategoryName(entity.getSubCategory() != null ? entity.getSubCategory().getName() : null)
	                .build();
	    }
}
