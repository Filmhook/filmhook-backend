package com.annular.filmhook.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.Response;

import com.annular.filmhook.service.ShootingLocationService;
import com.annular.filmhook.service.UserMediaFilesService;

import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
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
		  public ResponseEntity<?> getAllProperties() {
		      logger.info("GET /getAll - Fetching all property details");

		      try {
		          List<ShootingLocationPropertyDetailsDTO> properties = service.getAllProperties();

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
		    public ResponseEntity<Map<String, Object>> deleteProperty(@PathVariable Long id) {
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

		  @PutMapping("update/{id}")
		  public ResponseEntity<Map<String, String>> updateProperty(
		          @PathVariable Long id,
		          @RequestBody ShootingLocationPropertyDetailsDTO dto) {

		      Map<String, String> response = new HashMap<>();

		      try {
		          logger.info("Attempting to update property with ID: {}", id);

		          service.updateProperty(id, dto);

		          response.put("status", "success");
		          response.put("message", "Property updated successfully");

		          logger.info("Property updated successfully for ID: {}", id);
		          return ResponseEntity.ok(response); 

		      } catch (RuntimeException e) {
		          logger.error("Property not found or update failed for ID: {}, Error: {}", id, e.getMessage());
		          response.put("status", "error");
		          response.put("message", "Property not found or update failed for ID: " + id);
		          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 

		      } catch (Exception e) {
		          logger.error("Unexpected error while updating property ID: {}, Error: {}", id, e.getMessage(), e);
		          response.put("status", "error");
		          response.put("message", "Internal server error occurred");
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
		  

}
		

