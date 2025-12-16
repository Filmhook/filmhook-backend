package com.annular.filmhook.controller;

import java.time.LocalDate;
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
import com.annular.filmhook.model.Payments;
import com.annular.filmhook.model.ShootingLocationPropertyReview;
import com.annular.filmhook.model.User;
import com.annular.filmhook.service.ShootingLocationService;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.webmodel.PaymentsDTO;
import com.annular.filmhook.webmodel.PropertyAvailabilityDTO;
import com.annular.filmhook.webmodel.ReviewReplyRequestDTO;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewResponseDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategorySelectionDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;
import com.annular.filmhook.webmodel.ShootingPaymentModel;
import com.annular.filmhook.webmodel.ShootingPropertyByIndustryAndDateRequest;

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
	public ResponseEntity<Response> getPropertiesByUserId(@PathVariable Integer userId) {

		Response response = service.getPropertiesByUserId(userId);

		// If no data
		if (response.getStatus() == 0) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("deleteProperty/{id}")
	public ResponseEntity<Response> deleteProperty(@PathVariable Integer id) {
		Response response = service.deletePropertyById(id);
		return ResponseEntity.ok(response);
	}


	@PostMapping("/savePropertyDetails")
	public ResponseEntity<Response> savePropertyDetails(
			@ModelAttribute ShootingLocationFileInputModel inputFile,
			@RequestPart(value = "propertyDetails", required = false)
			ShootingLocationPropertyDetailsDTO propertyDetailsDTO) {

		try {
			Response response = service.savePropertyDetails(propertyDetailsDTO, inputFile);

			// If service returns status = 1 → Success
			if (response.getStatus() == 1) {
				return ResponseEntity.status(HttpStatus.CREATED).body(response);
			}

			// If service returns status = -1 → Validation Error
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to save property details", e.getMessage()));
		}
	}
	 @PutMapping(value = "/updateProperty/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    public ResponseEntity<Response> updateProperty(
	            @PathVariable("propertyId") Integer propertyId,
	            @RequestPart("propertyDetails") ShootingLocationPropertyDetailsDTO dto,
	            @ModelAttribute ShootingLocationFileInputModel mediaFiles) {

	        try {
	            // Optional validation: ensure path ID matches DTO ID if present
	            if (dto.getId() != null && !dto.getId().equals(propertyId)) {
	                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mismatched property IDs");
	            }

	            ShootingLocationPropertyDetailsDTO updated =
	                    service.updatePropertyDetails(propertyId, dto, mediaFiles);

	            return ResponseEntity.ok(new Response(1, "Property updated successfully", updated));

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

	@PostMapping(value = "/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ShootingLocationPropertyReviewDTO> addReview(
			@RequestParam Integer propertyId,
			@RequestParam Integer userId,
			@RequestParam int rating,
			@RequestParam (required = false) String reviewText,
			@RequestParam(required = false) List<MultipartFile> files) {

		try {
			ShootingLocationPropertyReviewDTO savedReview = service.saveReview(
					propertyId,
					userId,
					rating,
					reviewText,
					files
					);

			return ResponseEntity.ok(savedReview);
		} catch (RuntimeException e) {
			logger.warn("Validation failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception e) {
			logger.error("Error saving review", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value = "/review/reply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ShootingLocationPropertyReviewDTO> replyToReview(@RequestBody ReviewReplyRequestDTO request) {
	    try {
	        ShootingLocationPropertyReviewDTO dto = service.replyToReview(
	                request.getReviewId(),
	                request.getOwnerUserId(),
	                request.getReplyText()
	        );
	        return ResponseEntity.ok(dto);
	    } catch (RuntimeException e) {
	        logger.warn("Reply failed: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // or 404 depending on message
	    } catch (Exception e) {
	        logger.error("Error replying to review", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	@DeleteMapping("/review/reply")
	public ResponseEntity<ShootingLocationPropertyReviewDTO> deleteReply(
	        @RequestParam Integer reviewId,
	        @RequestParam Integer ownerUserId) {

	    try {
	        ShootingLocationPropertyReviewDTO dto =
	                service.deleteReply(reviewId, ownerUserId);

	        return ResponseEntity.ok(dto);

	    } catch (RuntimeException e) {
	        logger.warn("Delete reply failed: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	    } catch (Exception e) {
	        logger.error("Error deleting reply", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
	public ResponseEntity<?> getReviewsByProperty(@PathVariable Integer propertyId, @PathVariable Integer userId ) {
		logger.info("Fetching reviews for property ID: {}", propertyId);
		try {
			ShootingLocationPropertyReviewResponseDTO reviews = service.getReviewsByPropertyId(propertyId, userId);

			return ResponseEntity.ok(new Response(1, "Success", reviews));
		} catch (Exception e) {
			logger.error("Failed to fetch reviews for property ID: {}", propertyId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to get property reviews", null));
		}
	}


	@GetMapping("/{bookingId}/property")
	public ShootingLocationPropertyDetailsDTO getPropertyByBookingId(@PathVariable Integer bookingId) {
		return service.getPropertyByBookingId(bookingId);
	}

	@PutMapping(value = "/updateReview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ShootingLocationPropertyReviewDTO> updateReview(
			@RequestParam Integer reviewId,
			@RequestParam Integer propertyId,
			@RequestParam Integer userId,
			@RequestParam int rating,
			@RequestParam(required = false) String reviewText,

			// 🔹 files to ADD (optional)
			@RequestParam(required = false) List<MultipartFile> files,

			// 🔹 specific existing file IDs to DELETE (optional)
			@RequestParam(required = false, name = "deletedFileIds") List<Integer> deletedFileIds
			) {
		try {
			ShootingLocationPropertyReviewDTO updatedReview =
					service.updateReview(
							reviewId, propertyId, userId, rating, reviewText,
							files, deletedFileIds
							);
			return ResponseEntity.ok(updatedReview);

		} catch (RuntimeException e) {
			logger.warn("Validation failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			logger.error("Error updating review", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}



	@DeleteMapping("/deleteReview")
	public ResponseEntity<Response> deleteReview(
			@RequestParam Integer reviewId,
			@RequestParam Integer userId) {
		try {
			String message = service.deleteReview(reviewId, userId);
			return ResponseEntity.ok(new Response(1, "Success", message));
		} catch (RuntimeException e) {
			return ResponseEntity.ok(new Response(0, e.getMessage(), null));
		}
	}

	@GetMapping("/availableDates/{propertyId}")
	public ResponseEntity<?> getAvailableDates(@PathVariable Integer propertyId) {
		try {
			List<LocalDate> availableDates = service.getAvailableDatesForProperty(propertyId);

			return ResponseEntity.ok(availableDates);

		} catch (RuntimeException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("error", e.getMessage()));

		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "Something went wrong"));
		}
	}


	@PostMapping("/createBooking")
	public ResponseEntity<ShootingLocationBookingDTO> createBooking(@RequestBody ShootingLocationBookingDTO dto) {

		ShootingLocationBookingDTO response = service.createBooking(dto);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/createPayment")
	public ResponseEntity<?> initiatePayment(@RequestBody ShootingPaymentModel model) {

		try {
			// Service contains validations
			Payments payment = service.createShootingPayment(model);

			return ResponseEntity.ok(
					new Response(1, "Payment initiated successfully", payment)
					);

		} catch (RuntimeException e) {

			// 404: Not Found cases (User / Booking not found)
			if (e.getMessage().contains("not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, e.getMessage(), null));
			}

			// 400: Validation issues
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new Response(-1, e.getMessage(), null));

		} catch (Exception e) {

			// 500: Server related issues
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1,
							"Failed to initiate payment: " + e.getMessage(),
							null));
		}
	}

	 @GetMapping("/successPayment")
	    public ResponseEntity<Response> shootingPaymentSuccess(@RequestParam String txnid) {
	        return service.handleShootingLocationPaymentSuccess(txnid);
	    }
	 
	    @GetMapping("/failedPayment")
	    public ResponseEntity<?> shootingPaymentFailed( @RequestParam String txnid, @RequestParam(required = false, defaultValue = "Transaction Failed") String reason) {

	        return service.handleShootingLocationPaymentFailed(txnid, reason);
	    }
	    
	    @PostMapping("/properties/byIndustry")
	    public ResponseEntity<?> getProperties(@RequestBody ShootingPropertyByIndustryAndDateRequest req) {
	        List<ShootingLocationPropertyDetailsDTO> result =
	            service.getPropertiesByIndustryIdsAndDates(
	                req.getIndustryId(),
	                req.getUserId(),
	                req.getStartDate(), 
	                req.getEndDate()     
	            );

	        return ResponseEntity.ok(new Response(1, "Success", result));
	    }
	    
	    
	    
	    @PostMapping("/saveAdminRating")
	    public ResponseEntity<?> saveAdminRating(
	            @RequestBody ShootingLocationPropertyDetailsDTO request) {

	        return service.saveAdminPropertyRating(request);
	    }

}

