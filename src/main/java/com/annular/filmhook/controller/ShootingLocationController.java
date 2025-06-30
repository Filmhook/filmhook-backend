package com.annular.filmhook.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.ShootingLocationPropertyReview;
import com.annular.filmhook.service.ShootingLocationService;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.webmodel.PropertyAvailabilityDTO;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategorySelectionDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shooting-location")
//@RequiredArgsConstructor
public class ShootingLocationController {
	@Autowired
	private ShootingLocationService service;
	


	public static final Logger logger = LoggerFactory.getLogger(ShootingLocationController.class);


	@GetMapping("/types")
	public ResponseEntity<?> getTypes() {
	    try {
	        List<ShootingLocationTypeDTO> types = service.getAllTypes();
	        return ResponseEntity.ok(new Response(1, "Success", types));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Failed", null));
	    }
	}

	 @GetMapping("/categories/{typeId}")
	    public ResponseEntity<?> getCategories(@PathVariable Integer typeId) {
	        try {
	            logger.info("Fetching categories for typeId: {}", typeId);

	            List<ShootingLocationCategoryDTO> categories = service.getCategoriesByTypeId(typeId);

	            logger.info("Fetched {} categories for typeId: {}", categories.size(), typeId);
	            return ResponseEntity.ok(new Response(1, "Success", categories));

	        } catch (Exception e) {
	            logger.error("Failed to fetch categories for typeId {}: {}", typeId, e.getMessage(), e);
	            return ResponseEntity.status(500).body(new Response(-1, "Failed to fetch categories", null));
	        }
	    }


	 @GetMapping("/subcategories/{categoryId}")
	 public ResponseEntity<Response> getSubcategories(@PathVariable Integer categoryId) {
	     try {
	         logger.info("getSubcategories controller called for categoryId: {}", categoryId);

	         List<ShootingLocationSubcategoryDTO> subcategories = service.getSubcategoriesByCategoryId(categoryId);

	         logger.info("Successfully fetched {} subcategories for categoryId: {}", subcategories.size(), categoryId);
	         return ResponseEntity.ok(new Response(1, "Success", subcategories));

	     } catch (Exception e) {
	         logger.error("Failed to fetch subcategories for categoryId {}: {}", categoryId, e.getMessage(), e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(new Response(-1, "Failed to fetch subcategories", null));
	     }
	 }


	 @PostMapping(value = "/propertySelection", consumes = "application/json")
	 public ResponseEntity<?> saveSelection(@RequestBody ShootingLocationSubcategorySelectionDTO dto) {
	     try {
	         logger.info("Received selection save request for subcategoryId: {}, entire: {}, single: {}",
	                 dto.getSubcategoryId(), dto.getEntireProperty(), dto.getSingleProperty());


	         logger.info("Selection saved successfully for subcategoryId: {}", dto.getSubcategoryId());
	         return ResponseEntity.ok(new Response(1, "Selection saved successfully", null));

	     } catch (Exception e) {
	         logger.error("Failed to save selection for subcategoryId {}: {}", dto.getSubcategoryId(), e.getMessage(), e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(new Response(-1, "Failed to save selection", null));
	     }
	 }



	 @GetMapping("/getAllProperty")
	 public ResponseEntity<?> getAllProperties(@RequestParam Integer userId) {
	     logger.info("GET /getAllProperty - Fetching all property details for userId: {}", userId);

	     try {
	         List<ShootingLocationPropertyDetailsDTO> properties = service.getAllProperties(userId);

	         if (properties.isEmpty()) {
	             logger.warn("No properties found in the database");
	             Map<String, String> response = new HashMap<>();
	             response.put("status", "empty");
	             response.put("message", "No property records found");
	             return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
	         }

	         logger.info("Successfully fetched {} properties", properties.size());
	         return ResponseEntity.ok(properties);

	     } catch (Exception e) {
	         logger.error("Error occurred while fetching property details: {}", e.getMessage(), e);
	         Map<String, String> response = new HashMap<>();
	         response.put("status", "error");
	         response.put("message", "Internal Server Error: " + e.getMessage());
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	     }
	 }


		  @GetMapping("/user/{userId}")
		    public ResponseEntity<List<ShootingLocationPropertyDetailsDTO>> getPropertiesByUserId(@PathVariable Integer userId) {
		        List<ShootingLocationPropertyDetailsDTO> properties = service.getPropertiesByUserId(userId);
		        if (properties.isEmpty()) {
		            return ResponseEntity.noContent().build();
		        }
		        return ResponseEntity.ok(properties);
		    }
		  @DeleteMapping("deleteProperty/{id}")
		    public ResponseEntity<Map<String, Object>> deleteProperty(@PathVariable Integer id) {
		        Map<String, Object> response = new HashMap<>();
		        try {
		            logger.info("Attempting to delete property with ID: {}", id);

		            service.deletePropertyById(id);

		            response.put("status", "success");
		            response.put("message", "Property deleted successfully");
		            response.put("propertyId", id);

		            logger.info("Successfully deleted property with ID: {}", id);
		            return ResponseEntity.ok(response); 
		        } catch (RuntimeException e) {
		            logger.error("Property not found or error occurred while deleting. ID: {}, Error: {}", id, e.getMessage());
		            response.put("status", "error");
		            response.put("message", "Property not found with ID: " + id);
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		        } catch (Exception e) {
		            logger.error("Unexpected error occurred while deleting property ID: {}, Error: {}", id, e.getMessage(), e);
		            response.put("status", "error");
		            response.put("message", "Internal server error");
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); 
		        }
		    }


		  @PostMapping("/savePropertyDetails")
		  public ResponseEntity<?> savePropertyDetails(@ModelAttribute ShootingLocationFileInputModel inputFile,
				  @RequestPart(value ="propertyDetails", required = false) ShootingLocationPropertyDetailsDTO propertyDetailsDTO) {
		      logger.info("POST /save - Received request to save property with n: {}", propertyDetailsDTO.getPropertyName());
		      logger.info("POST /save - Received request to save property with name: {}", propertyDetailsDTO.getSubcategorySelectionDTO());
		      try {
		    	  service.savePropertyDetails(propertyDetailsDTO, inputFile);
		          logger.info("Property '{}' saved successfully", propertyDetailsDTO.getPropertyName());

		          Map<String, String> response = new HashMap<>();
		          response.put("status", "success");
		          response.put("message", "Property details saved successfully");
		          return ResponseEntity.status(HttpStatus.CREATED).body(response);

		      } catch (Exception e) {
		          logger.error("Error saving property '{}': {}", propertyDetailsDTO.getPropertyName(), e.getMessage(), e);

		          Map<String, String> response = new HashMap<>();
		          response.put("status", "error");
		          response.put("message", "Failed to save property details: " + e.getMessage());
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		      }
		  }
		  

		  @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		  public ResponseEntity<Response> updateProperty(
		          @RequestParam("propertyId") Integer propertyId,
		          @RequestPart("propertyDetails") ShootingLocationPropertyDetailsDTO dto,
		          @ModelAttribute ShootingLocationFileInputModel mediaFiles) {

		      try {
		          service.updatePropertyDetails(propertyId, dto, mediaFiles);
		          return ResponseEntity.ok(new Response(1, "Property updated successfully", null));

		      } catch (ResponseStatusException e) {
		          return ResponseEntity.status(e.getStatus())
		                  .body(new Response(-1, e.getReason(), null));

		      } catch (Exception e) {
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                  .body(new Response(-1, "Unexpected error during update", e.getMessage()));
		      }
		  }


		  @PostMapping("/addLike")
		  public ResponseEntity<Map<String, String>> toggleLike(@RequestParam Integer propertyId, @RequestParam Integer userId) {
		      Logger logger = LoggerFactory.getLogger(this.getClass());
		      Map<String, String> response = new HashMap<>();

		      try {
		          logger.info("Toggling like for propertyId: {}, userId: {}", propertyId, userId);
		          String message = service.toggleLike(propertyId, userId);
		          response.put("message", message);
		          return ResponseEntity.ok(response);

		      } catch (RuntimeException ex) {
		          logger.error("Error: {}", ex.getMessage());
		          response.put("message", "Error: " + ex.getMessage());
		          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

		      } catch (Exception ex) {
		          logger.error("Unexpected error: {}", ex.getMessage());
		          response.put("message", "Something went wrong.");
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		      }
		  }

		  @GetMapping("/countLikes")
		  public ResponseEntity<?> countLikes(@RequestParam Integer propertyId) {
		      Logger logger = LoggerFactory.getLogger(this.getClass());
		      try {
		          logger.info("Received request to count likes for property ID: {}", propertyId);

		          Long count = service.countLikes(propertyId);

		          logger.info("Like count for property ID {} is {}", propertyId, count);
		          return ResponseEntity.ok(count);

		      } catch (RuntimeException ex) {
		          logger.error("Failed to count likes for property ID {}: {}", propertyId, ex.getMessage(), ex);
		          return ResponseEntity
		                  .status(HttpStatus.NOT_FOUND)
		                  .body("Error: " + ex.getMessage());

		      } catch (Exception ex) {
		          logger.error("Unexpected error occurred while counting likes for property ID {}: {}", propertyId, ex.getMessage(), ex);
		          return ResponseEntity
		                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
		                  .body("An unexpected error occurred. Please try again later.");
		      }


		  }
		  
		  
		  @GetMapping("/getPropertiesByliked")
		  public ResponseEntity<?> getLikedProperties(@RequestParam Integer userId) {
		      Logger logger = LoggerFactory.getLogger(this.getClass());
		      Map<String, String> response = new HashMap<>();

		      try {
		          logger.info("Fetching liked properties for userId: {}", userId);

		          if (userId == null) {
		              logger.warn("User ID is null");
		              response.put("message", "User ID cannot be null");
		              return ResponseEntity.badRequest().body(response);
		          }
          List<ShootingLocationPropertyDetailsDTO> likedProperties = service.getPropertiesLikedByUser(userId);

		          if (likedProperties.isEmpty()) {
		              logger.info("No liked properties found for userId: {}", userId);
		              response.put("message", "No properties found in wishlist");
		              return ResponseEntity.ok(response);
		          }

		          logger.info("Found {} liked properties for userId: {}", likedProperties.size(), userId);
		          return ResponseEntity.ok(likedProperties);

		      } catch (Exception ex) {
		          logger.error("Unexpected error while fetching liked properties for userId {}: {}", userId, ex.getMessage());
		          response.put("message", "Something went wrong while fetching wishlist");
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		      }
		  }


		  //============================================================
		  
		  @PostMapping("/getPropertiesByIndustry")
		  public ResponseEntity<List<ShootingLocationPropertyDetailsDTO>> getAllPropertiesByIndustryIds(
		          @RequestBody Map<String, Object> payload) {
		      
		      logger.info("Received request to fetch properties for industry IDs with payload: {}", payload);

		      try {
		          // Extract industryIds
		          List<Integer> industryIds = (List<Integer>) payload.get("industryIds");
		          Integer userId = (payload.get("userId") instanceof Integer)
		                  ? (Integer) payload.get("userId")
		                  : null;

		          // Validate input
		          if (industryIds == null || industryIds.isEmpty()) {
		              logger.warn("Industry ID list is null or empty.");
		              return ResponseEntity.badRequest().body(Collections.emptyList());
		          }

		          if (userId == null) {
		              logger.warn("User ID is missing or invalid in the payload.");
		              return ResponseEntity.badRequest().body(Collections.emptyList());
		          }

		          // Service call
		          List<ShootingLocationPropertyDetailsDTO> result = service.getPropertiesByIndustryIds(industryIds, userId);
		          logger.info("Returning {} properties for industryIds: {}", result.size(), industryIds);
		          return ResponseEntity.ok(result);

		      } catch (ClassCastException e) {
		          logger.error("Invalid data types in request payload: {}", e.getMessage());
		          return ResponseEntity.badRequest().body(Collections.emptyList());
		      } catch (Exception e) {
		          logger.error("Error occurred while fetching properties by industry IDs: ", e);
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
		      }
		  }

		  @PostMapping("/review")
		  public ResponseEntity<String> addReview(@RequestBody ShootingLocationPropertyReviewDTO propertyReviewDTO) {
		      try {
		          service.saveReview(
		              propertyReviewDTO.getPropertyId(),
		              propertyReviewDTO.getUserId(),
		              propertyReviewDTO.getRating(),
		              propertyReviewDTO.getReviewText()
		          );
		          return ResponseEntity.ok("Review saved");
		      } catch (RuntimeException e) {
		      
		          logger.warn("Validation failed: {}", e.getMessage());
		          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		      } catch (Exception e) {
		          logger.error("Error saving review", e);
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save review");
		      }
		  }

		  @GetMapping("/average-rating/{propertyId}")
		    public ResponseEntity<?> getAverageRating(@PathVariable Integer propertyId) {
		        logger.info("Fetching average rating for property ID: {}", propertyId);
		        try {
		            double averageRating = service.getAverageRating(propertyId);
		            logger.info("Average rating for property {} is {}", propertyId, averageRating);
		            return ResponseEntity.ok(new Response(1, "Success", averageRating));
		        } catch (Exception e) {
		            logger.error("Failed to fetch average rating for property ID: {}", propertyId, e);
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                                 .body(new Response(-1, "Failed to get average rating", null));
		        }
		    }
		  @GetMapping("/property/{propertyId}")
		    public ResponseEntity<?> getReviewsByProperty(@PathVariable Integer propertyId) {
		        logger.info("Fetching reviews for property ID: {}", propertyId);
		        try {
		            List<ShootingLocationPropertyReviewDTO> reviews = service.getReviewsByPropertyId(propertyId);
		            logger.info("Fetched {} reviews for property ID: {}", reviews.size(), propertyId);
		            return ResponseEntity.ok(new Response(1, "Success", reviews));
		        } catch (Exception e) {
		            logger.error("Failed to fetch reviews for property ID: {}", propertyId, e);
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                                 .body(new Response(-1, "Failed to get property reviews", null));
		        }
		    }
		  
		  
		  @PostMapping("/save/availabilityDates")
		    public ResponseEntity<?> saveAvailability(@RequestBody PropertyAvailabilityDTO dto) {
		        try {
		            PropertyAvailabilityDTO saved = service.saveAvailability(dto);
		            return ResponseEntity.ok(new Response(1, "Availability Saved", saved));
		        } catch (Exception e) {
		            return ResponseEntity.internalServerError().body(new Response(-1, e.getMessage(), null));
		        }
		    }

		    @GetMapping("/availabilityDates/{propertyId}")
		    public ResponseEntity<?> getAvailability(@PathVariable Integer propertyId) {
		        try {
		            List<PropertyAvailabilityDTO> list = service.getAvailabilityByPropertyId(propertyId);
		            return ResponseEntity.ok(new Response(1, "Fetched successfully", list));
		        } catch (Exception e) {
		            return ResponseEntity.internalServerError().body(new Response(-1, e.getMessage(), null));
		        }
		    }
		    
		    @PutMapping("/availabilityDates/update")
		    public ResponseEntity<?> updateAvailability(@RequestBody List<PropertyAvailabilityDTO> availabilityList) {
		        try {
		            if (availabilityList == null || availabilityList.isEmpty()) {
		                return ResponseEntity.badRequest().body("❌ Availability list is empty.");
		            }

		            // Validate date ranges
		            for (PropertyAvailabilityDTO dto : availabilityList) {
		                if (dto.getStartDate() == null || dto.getEndDate() == null) {
		                    return ResponseEntity.badRequest().body("❌ Start date and end date must not be null.");
		                }
		                if (!dto.getEndDate().isAfter(dto.getStartDate())) {
		                    return ResponseEntity.badRequest().body(
		                            "❌ End date must be after start date. Found: startDate=" + dto.getStartDate() +
		                            ", endDate=" + dto.getEndDate()
		                    );
		                }
		            }

		            Integer propertyId = availabilityList.get(0).getPropertyId();
		            service.updateAvailabilityDates(propertyId, availabilityList);

		            return ResponseEntity.ok("✅ Availability dates updated successfully.");
		        } catch (Exception e) {
		            e.printStackTrace();
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                    .body("❌ Failed to update availability dates: " + e.getMessage());
		        }
		    }


}

