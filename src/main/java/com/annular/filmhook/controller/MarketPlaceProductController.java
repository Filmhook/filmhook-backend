package com.annular.filmhook.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MarketPlaceSubCategories;
import com.annular.filmhook.repository.SellerInfoRepository;
import com.annular.filmhook.service.MarketPlaceProductService;
import com.annular.filmhook.webmodel.MarketPlaceCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryFieldDTO;
import com.annular.filmhook.webmodel.SellerFileInputModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/marketPlace")
public class MarketPlaceProductController {

	@Autowired
	private MarketPlaceProductService service;  
	

	private static final Logger logger = LoggerFactory.getLogger(MarketPlaceProductController.class);


	@GetMapping("/getCategory")
	public Response getAll() {
		try {
			List<MarketPlaceCategoryDTO> categories = service.getAllCategories();
			return new Response(1, "Categories fetched successfully", categories);
		} catch (Exception e) {
			logger.error("Error at getAll() -> [{}]", e.getMessage(), e);
			return new Response(-1, "Error fetching categories", e.getMessage());
		}
	}

	@GetMapping("/getCategoryById/{id}")
	public Response getById(@PathVariable Integer id) {
		try {
			MarketPlaceCategoryDTO dto = service.getCategoryById(id);
			if (dto != null) {
				return new Response(1, "Category fetched successfully", dto);
			} else {
				return new Response(0, "Category not found", null);
			}
		} catch (Exception e) {
			logger.error("Error at getById({}) -> [{}]", id, e.getMessage(), e);
			return new Response(-1, "Error fetching category", e.getMessage());
		}
	}

	@PostMapping("/saveCategory")
	public Response save(@RequestBody MarketPlaceCategoryDTO dto) {
		try {
			MarketPlaceCategoryDTO saved = service.saveCategory(dto);
			return new Response(1, "Category saved successfully", saved);
		} catch (Exception e) {
			logger.error("Error at save() -> [{}]", e.getMessage(), e);
			return new Response(-1, "Error saving category", e.getMessage());
		}
	}
	@GetMapping("/getSubcategory")
	public Response getAllSubCategories(@RequestParam Integer categoryId) {
		try {
			List<MarketPlaceSubCategoryDTO> list = service.getAllSubCategories(categoryId);
			return new Response(1, "Success", list);
		} catch (Exception e) {
			logger.error("Error in getAllSubCategories: {}", e.getMessage(), e);
			return new Response(-1, "Error", e.getMessage());
		}
	}

	@PostMapping("/saveSubcategory")

	public ResponseEntity<Response> saveSubCategory(@RequestBody MarketPlaceSubCategoryDTO dto) {
		try {
			MarketPlaceSubCategoryDTO savedDto = service.saveSubCategory(dto);
			return ResponseEntity.ok(new Response(1, "Subcategory saved successfully", savedDto));
		} catch (Exception e) {
			logger.error("Failed to save subcategory: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error saving subcategory", e.getMessage()));
		}
	}

	@PostMapping("/saveSubcategoryField")
	public Response saveSubCategoryField(@RequestBody MarketPlaceSubCategoryFieldDTO dto) {
		try {
			MarketPlaceSubCategoryFieldDTO savedDto = service.saveSubCategoryField(dto);
			return new Response(1, "Field saved successfully", savedDto);
		} catch (Exception e) {
			logger.error("Error saving field: {}", e.getMessage(), e);
			return new Response(-1, "Failed to save field", e.getMessage());
		}
	}

	@GetMapping("/getSubcategoryField")
	public Response getFields(@RequestParam Integer subCategoryId) {
		try {
			List<MarketPlaceSubCategoryFieldDTO> fields = service.getFieldsBySubCategoryId(subCategoryId);
			return new Response(1, "Success", fields);
		} catch (Exception e) {
			logger.error("Error fetching fields: {}", e.getMessage(), e);
			return new Response(-1, "Failed to fetch fields", e.getMessage());
		}
	}
	@PostMapping("/saveProduct")
	public ResponseEntity<?> saveProduct(
	        @ModelAttribute SellerFileInputModel mediaFiles,
	        @RequestPart(value = "product", required = false) MarketPlaceProductDTO productDto) {

	    logger.info("POST /saveProduct - Received product: {}", 
	        productDto != null ? productDto.getModelName() : "No product data");

	    Map<String, Object> response = new HashMap<>();

	    try {
	        if (productDto == null || productDto.getSellerId() == null) {
	            response.put("status", "error");
	            response.put("message", "Seller ID is required. Please log in or register as a seller.");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        MarketPlaceProductDTO savedProduct = service.saveProduct(productDto, mediaFiles);

	        response.put("status", "success");
	        response.put("message", "Product saved successfully.");
	        response.put("data", savedProduct);
	        return ResponseEntity.status(HttpStatus.CREATED).body(response);

	    } catch (RuntimeException e) {
	        logger.error("Error while saving product: {}", e.getMessage(), e);
	        response.put("status", "error");
	        response.put("message", e.getMessage()); 
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    } catch (Exception e) {
	        logger.error("Unexpected error while saving product: {}", e.getMessage(), e);
	        response.put("status", "error");
	        response.put("message", "Something went wrong while saving product.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}


    @GetMapping("/getAllProducts")
    public ResponseEntity<Response> getAllProducts() {
        try {
            logger.info("Fetching all products");
            List<MarketPlaceProductDTO> products = service.getAllProducts();
            return ResponseEntity.ok(new Response(1, "Success", products));
        } catch (Exception e) {
            logger.error("Error fetching products: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new Response(-1, "Failed to fetch products", e.getMessage()));
        }
    }

    @GetMapping("/getProductsById/{id}")
    public ResponseEntity<Response> getProductById(@PathVariable Integer id) {
        try {
            logger.info("Fetching product by id: {}", id);
            MarketPlaceProductDTO product = service.getProductById(id);

            return ResponseEntity.ok(new Response(1, "Success", product));

        } catch (ResponseStatusException e) {
            logger.warn("Product not found: {}", e.getReason());
            return ResponseEntity.status(e.getStatus())
                    .body(new Response(-1, e.getReason(), null));

        } catch (Exception e) {
            logger.error("Unexpected error fetching product: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(new Response(-1, "Something went wrong", e.getMessage()));
        }
    }


    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<Response> deleteProduct(@PathVariable Integer id) {
        try {
            logger.info("Deleting product by id: {}", id);
            service.deleteProduct(id);
            return ResponseEntity.ok(new Response(1, "Product deleted successfully", null));

        } catch (ResponseStatusException e) {
            logger.warn("Product deletion failed: {}", e.getReason());
            return ResponseEntity.status(e.getStatus())
                    .body(new Response(-1, e.getReason(), null));

        } catch (Exception e) {
            logger.error("Error deleting product: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(new Response(-1, "Unexpected error during deletion", e.getMessage()));
        }
    }


    @GetMapping("/getProductsBysubcategory/{subCategoryId}")
    public ResponseEntity<Response> getProductsBySubCategory(@PathVariable Integer subCategoryId) {
        try {
            logger.info("Fetching products by subCategoryId: {}", subCategoryId);
            List<MarketPlaceProductDTO> products = service.getProductsBySubCategoryId(subCategoryId);
            return ResponseEntity.ok(new Response(1, "Success", products));
        } catch (Exception e) {
            logger.error("Error fetching products by subCategoryId: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new Response(-1, "Failed to fetch products", e.getMessage()));
        }
    }

    @PutMapping(value = "/updateProduct/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> updateProduct(
            @PathVariable Integer productId,
            @RequestPart("productDetails") MarketPlaceProductDTO dto,
            @ModelAttribute SellerFileInputModel mediaFiles) {

        try {
            logger.info("Initiating update for product ID: {}", productId);
            service.updateProduct(productId, dto, mediaFiles);
            return ResponseEntity.ok(new Response(1, "Product updated successfully", null));

        } catch (ResponseStatusException e) {
            logger.warn("Update failed: {}", e.getReason());
            return ResponseEntity.status(e.getStatus())
                    .body(new Response(-1, e.getReason(), null));

        } catch (Exception e) {
            logger.error("Unexpected error updating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Unexpected error during update", e.getMessage()));
        }
    }

    
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Response> getProductsByUserId(@PathVariable Long userId) {
        try {
            logger.info("Received request to fetch products by userId: {}", userId);

            List<MarketPlaceProductDTO> productList = service.getProductsByUserId(userId);

            if (productList.isEmpty()) {
                logger.warn("No products found for userId: {}", userId);
                return ResponseEntity.ok(new Response(1, "No products found for this user.", productList));
            }

            logger.info("Returning {} products for userId: {}", productList.size(), userId);
            return ResponseEntity.ok(new Response(1, "Products fetched successfully.", productList));

        } catch (Exception e) {
            logger.error("Failed to fetch products for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Failed to fetch products", e.getMessage()));
        }
    }




}
