package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.service.AuditionNewService;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audition")
public class AuditionNewController {
	private static final Logger logger = LoggerFactory.getLogger(MovieController.class);


	@Autowired
	AuditionNewService auditionNewService;


	@GetMapping("/categories")
	public ResponseEntity<?> getCategories() {
		try {
			logger.info("Fetching movie categories");
			List<MovieCategory> categories = auditionNewService.getAllCategories();
			return ResponseEntity.ok(categories);
		} catch (Exception e) {
			logger.error("Error in getCategories: {}", e.getMessage(), e);
			return ResponseEntity.ok(new Response(-1, "Failed to fetch categories", null));
		}
	}

	@GetMapping("/categories/{id}/subcategories")
	public ResponseEntity<?> getSubCategories(@PathVariable Integer id) {
		try {
			logger.info("Fetching subcategories for category id: {}", id);
			List<MovieSubCategory> subCategories = auditionNewService.getSubCategories(id);
			return ResponseEntity.ok(subCategories);
		} catch (Exception e) {
			logger.error("Error in getSubCategories: {}", e.getMessage(), e);
			return ResponseEntity.ok(new Response(-1, "Failed to fetch subcategories", null));
		}
	}
	 @GetMapping("/professions/sub-professions")
	    public ResponseEntity<?> getAllSubProfessions() {
	        try {
	            logger.info("Fetching all film sub professions");
	            List<FilmSubProfessionResponseDTO> subProfessions = auditionNewService.getAllSubProfessions();
	            return ResponseEntity.ok(subProfessions);
	        } catch (Exception e) {
	            logger.error("Error in getAllSubProfessions: {}", e.getMessage(), e);
	            return ResponseEntity.ok(new Response(-1, "Failed to fetch sub professions", null));
	        }
	    }

	    @GetMapping("/professions/{professionId}/sub-professions")
	    public ResponseEntity<?> getSubProfessionsByProfessionId(@PathVariable Integer professionId) {
	        try {
	            logger.info("Fetching sub professions for professionId: {}", professionId);
	            List<FilmSubProfessionResponseDTO> subProfessions =
	                    auditionNewService.getSubProfessionsByProfessionId(professionId);
	            return ResponseEntity.ok(subProfessions);
	        } catch (Exception e) {
	            logger.error("Error in getSubProfessionsByProfessionId: {}", e.getMessage(), e);
	            return ResponseEntity.ok(new Response(-1, "Failed to fetch sub professions by professionId", null));
	        }
	    }
	    @PostMapping("/cart")
	    public ResponseEntity<Response> addToCart(@RequestParam Integer userId,
	                                              @RequestParam Integer companyId,
	                                              @RequestParam Integer subProfessionId,
	                                              @RequestParam Integer count) {
	        auditionNewService.addToCart(userId, companyId, subProfessionId, count);
	        return ResponseEntity.ok(new Response(1, "Cart updated successfully", null));
	    }

	    @GetMapping("/cart")
	    public ResponseEntity<?> getCart(@RequestParam Integer userId,
	                                     @RequestParam Integer companyId) {
	        List<FilmSubProfessionResponseDTO> cart = auditionNewService.getCart(userId, companyId);
	        return ResponseEntity.ok(cart);
	    }


}
