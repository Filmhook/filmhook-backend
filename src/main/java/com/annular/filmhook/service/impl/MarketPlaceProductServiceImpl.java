package com.annular.filmhook.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.annular.filmhook.converter.MarketPlaceProductConverter;
import com.annular.filmhook.model.MarketPlaceCategories;
import com.annular.filmhook.model.MarketPlaceProducts;
import com.annular.filmhook.model.MarketPlaceSubCategories;
import com.annular.filmhook.model.MarketPlaceSubCategoryFields;
import com.annular.filmhook.model.SellerInfo;
import com.annular.filmhook.model.SellerMediaFile;
import com.annular.filmhook.repository.MarketPlaceCategoryRepository;
import com.annular.filmhook.repository.MarketPlaceProductRepository;
import com.annular.filmhook.repository.MarketPlaceSubCategoryFiledsRepository;
import com.annular.filmhook.repository.MarketPlaceSubCategoryRepository;
import com.annular.filmhook.repository.SellerInfoRepository;
import com.annular.filmhook.repository.SellerMediaFileRepository;
import com.annular.filmhook.service.MarketPlaceProductService;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.MarketPlaceCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryFieldDTO;
import com.annular.filmhook.webmodel.SellerFileInputModel;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MarketPlaceProductServiceImpl implements MarketPlaceProductService{
	@Autowired
	private MarketPlaceSubCategoryRepository subCategoryRepo;

	@Autowired
	private MarketPlaceCategoryRepository categoryRepo;

	@Autowired
	private MarketPlaceSubCategoryFiledsRepository subCategoryFieldsRepo;

	@Autowired
	private MarketPlaceProductRepository productRepo;

	@Autowired
	private SellerMediaFileRepository sellerMediaFileRepository;
	
     @Autowired
    private SellerInfoRepository sellerInfoRepo;
     
	@Autowired
	private S3Util s3Util;

	private static final Logger logger = LoggerFactory.getLogger(MarketPlaceProductServiceImpl.class);

	@Override
	public List<MarketPlaceCategoryDTO> getAllCategories() {
		try {
			List<MarketPlaceCategoryDTO> list = categoryRepo.findAll()
					.stream()
					.map(MarketPlaceProductConverter::toDto)
					.collect(Collectors.toList());
			logger.info("Fetched {} marketplace categories", list.size());
			return list;
		} catch (Exception e) {
			logger.error("Error fetching marketplace categories: {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public MarketPlaceCategoryDTO getCategoryById(Integer id) {
		try {
			return categoryRepo.findById(id)
					.map(MarketPlaceProductConverter::toDto)
					.orElse(null);
		} catch (Exception e) {
			logger.error("Error fetching category by ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public MarketPlaceCategoryDTO saveCategory(MarketPlaceCategoryDTO categoryDTO) {
		try {
			MarketPlaceCategories entity = MarketPlaceProductConverter.toEntity(categoryDTO);
			MarketPlaceCategories saved = categoryRepo.save(entity);
			logger.info("Saved marketplace category with ID {}", saved.getId());
			return MarketPlaceProductConverter.toDto(saved);
		} catch (Exception e) {
			logger.error("Error saving marketplace category: {}", e.getMessage(), e);
			return null;
		}
	}
	@Override
	public List<MarketPlaceSubCategoryDTO> getAllSubCategories(Integer categoryId) {
		try {
			if (categoryId == null) {
				logger.warn("Category ID is null");
				return Collections.emptyList();
			}

			List<MarketPlaceSubCategories> entities = subCategoryRepo.findByCategory_Id(categoryId);

			if (entities.isEmpty()) {
				logger.info("No subcategories found for categoryId {}", categoryId);
				return Collections.emptyList();
			}

			List<MarketPlaceSubCategoryDTO> subList = entities.stream()
					.map(MarketPlaceProductConverter::toDto)
					.collect(Collectors.toList());

			logger.info("Fetched {} subcategories for categoryId {}", subList.size(), categoryId);
			return subList;

		} catch (Exception e) {
			logger.error("Error fetching subcategories for categoryId {}: {}", categoryId, e.getMessage(), e);
			return Collections.emptyList();
		}
	}



	@Override
	public MarketPlaceSubCategoryDTO saveSubCategory(MarketPlaceSubCategoryDTO dto) {
		try {
			MarketPlaceSubCategories entity = MarketPlaceProductConverter.toEntity(dto);
			MarketPlaceSubCategories saved = subCategoryRepo.save(entity);
			return MarketPlaceProductConverter.toDto(saved);
		} catch (Exception e) {
			logger.error("Error saving subcategory: {}", e.getMessage());
			throw e;
		}
	}

	@Override
	public MarketPlaceSubCategoryFieldDTO saveSubCategoryField(MarketPlaceSubCategoryFieldDTO dto) {
		try {
			MarketPlaceSubCategoryFields entity = MarketPlaceProductConverter.toEntity(dto, subCategoryRepo);

			MarketPlaceSubCategoryFields saved = subCategoryFieldsRepo.save(entity);
			logger.info("Saved field [{}] for subCategoryId {}", saved.getFieldKey(), dto.getSubCategoryId());
			return MarketPlaceProductConverter.toDto(saved);
		} catch (Exception e) {
			logger.error("Error saving subcategory field: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public List<MarketPlaceSubCategoryFieldDTO> getFieldsBySubCategoryId(Integer subCategoryId) {
		try {
			List<MarketPlaceSubCategoryFields> fields = subCategoryFieldsRepo.findBySubCategories_Id(subCategoryId);
			return fields.stream()
					.map(MarketPlaceProductConverter::toDto)
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error fetching fields for subCategoryId {}: {}", subCategoryId, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public MarketPlaceProductDTO saveProduct(MarketPlaceProductDTO dto, SellerFileInputModel mediaFiles) {
		 try {
		        if (dto.getSellerId() == null) {
		            throw new RuntimeException("Seller ID is required to add a product.");
		        }

		        SellerInfo sellerInfo = sellerInfoRepo.findById(dto.getSellerId())
		                .orElseThrow(() -> new RuntimeException("Seller not found. Please create a seller account first."));


		        MarketPlaceSubCategories subCategory = subCategoryRepo.findById(dto.getSubCategoryId())
		                .orElseThrow(() -> new RuntimeException("SubCategory not found with ID: " + dto.getSubCategoryId()));

		        MarketPlaceProducts product = MarketPlaceProductConverter.toEntity(dto, subCategory, sellerInfo);
		        MarketPlaceProducts savedProduct = productRepo.save(product);

			List<SellerMediaFile> mediaList = new ArrayList<>();

			// Upload and attach product images
			if (mediaFiles != null && mediaFiles.getProductImages() != null && !mediaFiles.getProductImages().isEmpty()) {
				for (MultipartFile image : mediaFiles.getProductImages()) {
					SellerMediaFile imageFile = saveProductMediaFile(image, savedProduct, "PRODUCT_IMAGE");
					mediaList.add(imageFile);
				}
			}

			if (mediaFiles != null && mediaFiles.getProductVideos() != null && !mediaFiles.getProductVideos().isEmpty()) {
				for (MultipartFile video : mediaFiles.getProductVideos()) {
					SellerMediaFile videoFile = saveProductMediaFile(video, savedProduct, "PRODUCT_VIDEO");
					mediaList.add(videoFile);
				}
			}
			savedProduct.setMediaList(mediaList);
			savedProduct = productRepo.save(savedProduct);

			return MarketPlaceProductConverter.toDTO(savedProduct);
		} catch (Exception e) {
			logger.error("Error saving product: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to save product", e);
		}
	}



	@Override
	public List<MarketPlaceProductDTO> getAllProducts() {
		try {
			return productRepo.findAll()
					.stream()
					.map(MarketPlaceProductConverter::toDTO)
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error fetching all products: {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	// 3. Get product by ID
	@Override
	public MarketPlaceProductDTO getProductById(Integer id) {
	    try {
	        MarketPlaceProducts product = productRepo.findById(id)
	                .orElseThrow(() -> {
	                    logger.warn("Product not found with ID: {}", id);
	                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id);
	                });

	        return MarketPlaceProductConverter.toDTO(product);

	    } catch (ResponseStatusException e) {
	        throw e; 
	    } catch (Exception e) {
	        logger.error("Error fetching product by ID {}: {}", id, e.getMessage(), e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get product");
	    }
	}


	// 4. Delete product
	@Override
	public void deleteProduct(Integer id) {
	    try {
	        Optional<MarketPlaceProducts> optionalProduct = productRepo.findById(id);

	        if (!optionalProduct.isPresent()) {
	            logger.warn("Product not found with ID: {}", id);
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id);
	        }

	        MarketPlaceProducts product = optionalProduct.get();

	        // Delete each media file from S3
	        List<SellerMediaFile> mediaFiles = product.getMediaList();
	        if (mediaFiles != null && !mediaFiles.isEmpty()) {
	            for (SellerMediaFile media : mediaFiles) {
	                if (media.getFilePath() != null) {
	                    s3Util.deleteFileFromS3(media.getFilePath()); 
	                    logger.info("Deleted media file from S3: {}", media.getFilePath());
	                }
	            }
	        }

	        productRepo.deleteById(id);
	        logger.info("Product and associated media files deleted successfully: ID = {}", id);

	    } catch (ResponseStatusException e) {
	        throw e;

	    } catch (Exception e) {
	        logger.error("Error deleting product with ID {}: {}", id, e.getMessage(), e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete product");
	    }
	}



	// 5. Get products by subcategory
	@Override
	public List<MarketPlaceProductDTO> getProductsBySubCategoryId(Integer subCategoryId) {
		try {
			List<MarketPlaceProducts> products = productRepo.findBySubCategory_Id(subCategoryId);
			return products.stream()
					.map(MarketPlaceProductConverter::toDTO)
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error fetching products for subCategoryId {}: {}", subCategoryId, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void updateProduct(Integer productId, MarketPlaceProductDTO dto, SellerFileInputModel mediaFiles) {
	    try {
	        logger.info("Updating product with ID: {}", productId);

	        MarketPlaceProducts existing = productRepo.findById(productId)
	                .orElseThrow(() -> {
	                    logger.warn("Product not found: ID {}", productId);
	                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + productId);
	                });

	        MarketPlaceSubCategories subCategory = subCategoryRepo.findById(dto.getSubCategoryId())
	                .orElseThrow(() -> {
	                    logger.warn("SubCategory not found: ID {}", dto.getSubCategoryId());
	                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "SubCategory not found with ID: " + dto.getSubCategoryId());
	                });

	        SellerInfo seller = sellerInfoRepo.findById(dto.getSellerId())
	                .orElseThrow(() -> {
	                    logger.warn("Seller not found: ID {}", dto.getSellerId());
	                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found with ID: " + dto.getSellerId());
	                });

	        ObjectMapper objectMapper = new ObjectMapper();
	        String dynamicAttributesJson = dto.getDynamicAttributesJson() != null
	                ? objectMapper.writeValueAsString(dto.getDynamicAttributesJson())
	                : null;

	        existing.setBrandName(dto.getBrandName());
	        existing.setModelName(dto.getModelName());
	        existing.setPrice(dto.getPrice());
	        existing.setAvailability(dto.getAvailability());
	        existing.setSubCategory(subCategory);
	        existing.setDynamicAttributesJson(dynamicAttributesJson);
	        existing.setUpdatedBy(dto.getSellerId());
	        existing.setCreatedBy(dto.getSellerId());
	        existing.setSeller(seller);

	        // Validate update mode
	        String updateMode = mediaFiles.getUpdateMode();
	        boolean isReplace = "REPLACE".equalsIgnoreCase(updateMode);
	        boolean isAppend = "APPEND".equalsIgnoreCase(updateMode);

	        if (!isReplace && !isAppend) {
	            logger.warn("Invalid update mode: {}", updateMode);
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid update mode. Use APPEND or REPLACE.");
	        }

	        // Delete existing media if REPLACE
	        if (isReplace && existing.getMediaList() != null) {
	            for (SellerMediaFile media : existing.getMediaList()) {
	                s3Util.deleteFileFromS3(media.getFilePath());
	            }
	            existing.getMediaList().clear();
	            logger.info("Existing media files deleted for REPLACE mode");
	        }

	        // Add new media files
	        if (mediaFiles != null) {
	            if (mediaFiles.getProductImages() != null) {
	                for (MultipartFile image : mediaFiles.getProductImages()) {
	                    SellerMediaFile newImage = saveProductMediaFile(image, existing, "PRODUCT_IMAGE");
	                    existing.getMediaList().add(newImage);
	                }
	            }

	            if (mediaFiles.getProductVideos() != null) {
	                for (MultipartFile video : mediaFiles.getProductVideos()) {
	                    SellerMediaFile newVideo = saveProductMediaFile(video, existing, "PRODUCT_VIDEO");
	                    existing.getMediaList().add(newVideo);
	                }
	            }
	        }

	        productRepo.save(existing);
	        logger.info("Product updated successfully: ID {}", productId);

	    } catch (ResponseStatusException e) {
	        throw e;

	    } catch (Exception e) {
	        logger.error("Unexpected error while updating product: {}", e.getMessage(), e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product");
	    }
	}

	private SellerMediaFile saveProductMediaFile(MultipartFile file, MarketPlaceProducts product, String category) {
		String uploadedUrl = uploadFileToS3(file, category, product.getId());

		return sellerMediaFileRepository.save(SellerMediaFile.builder()
				.product(product)
				.category(category)
				.fileId(UUID.randomUUID().toString())
				.fileName(file.getOriginalFilename())
				.fileSize(file.getSize())
				.fileType(file.getContentType())
				.filePath(uploadedUrl)
				.status(true)
				.createdBy(product.getSeller().getId() != null ? product.getSeller().getId().intValue() : null)
				.updatedBy(product.getSeller().getId() != null ? product.getSeller().getId().intValue() : null)
				.notificationCount(0)
				.unverifiedList(false)
				.build());
	}

	private String uploadFileToS3(MultipartFile file, String folder, Integer productId) {
		try {
			AwsCredentialsProvider credentialsProvider = s3Util.getAwsCredentialsProvider();
			Region region = Region.of(s3Util.getS3RegionName());

			String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
			String s3Key = "MarketPlace/" + folder + "/" + productId + "/" + fileName;

			try (S3Client s3Client = S3Client.builder()
					.region(region)
					.credentialsProvider(credentialsProvider)
					.build()) {

				PutObjectRequest putRequest = PutObjectRequest.builder()
						.bucket(s3Util.getS3BucketName())
						.key(s3Key)
						.contentType(file.getContentType())
						.build();

				s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
			}

			return s3Util.getS3BaseURL() + "/" + s3Key;
		} catch (IOException e) {
			throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
		}

	}
	
	@Override
	public List<MarketPlaceProductDTO> getProductsByUserId(Long userId) {
	    try {
	        logger.info("Fetching seller for userId: {}", userId);

	        // 1. Find seller by user ID
	        SellerInfo seller = sellerInfoRepo.findSellerInfoByUserId(userId.intValue())
	                .orElseThrow(() -> new RuntimeException("Seller not found for userId: " + userId));

	        logger.info("Fetching products for sellerId: {}", seller.getId());

	        // 2. Fetch all products linked to this seller
	        List<MarketPlaceProducts> products = productRepo.findBySellerId(seller.getId());

	        // 3. Map to DTOs (which now includes subCategory name)
	        return products.stream()
	                .map(MarketPlaceProductConverter::toDTO)
	                .collect(Collectors.toList());

	    } catch (Exception e) {
	        logger.error("Error fetching products for userId: {}", userId, e);
	        throw new RuntimeException("Failed to fetch products. Reason: " + e.getMessage());
	    }
	}

	@Override
	public String toggleLike(Integer productId, Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
