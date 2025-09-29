package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionNewService;
import com.annular.filmhook.webmodel.FilmProfessionResponseDTO;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.AuditionPayment;
import com.annular.filmhook.validator.AuditionProjectValidator;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
import com.annular.filmhook.webmodel.AuditionPaymentDTO;
import com.annular.filmhook.webmodel.AuditionPaymentWebModel;

@RestController
@RequestMapping("/audition")
public class AuditionNewController {
	public static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	@Value("${payu.key}")
	private String key;
	 @Autowired
	 AuditionProjectValidator projectValidator;
    @Autowired
    private AuditionNewService projectService;
    @Autowired
    private UserRepository userRepository;

    
    @PostMapping(value = "/saveAuditions", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createProject(
            @RequestPart("auditionDetails") AuditionNewProjectWebModel dto,
            @RequestPart(value = "profilePictureFiles", required = false) List<MultipartFile> files) {

        // Attach files
        if (files != null && !files.isEmpty()) {
            dto.setProfilePictureFiles(files);
        }

        // ✅ Validate DTO
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "auditionNewProjectWebModel");
      
		projectValidator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getAllErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.badRequest()
                    .body(new Response(0, "Validation failed", errors.toString()));
        }

        try {
            // ✅ Save project
            AuditionNewProject project = projectService.createProject(dto);
            return ResponseEntity.ok(new Response(1, "Success", AuditionCompanyConverter.toDto(project)));

        } catch (Exception ex) {
        	logger.error("❌ Failed to create audition project", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(0, "Failed to create project: " + ex.getMessage(), null));
        }
    }
    
    @GetMapping("/BySubprofession/{subProfessionId}")
    public ResponseEntity<?> getProjectsBySubProfession(@PathVariable Integer subProfessionId) {
        try {
            if (subProfessionId == null || subProfessionId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new Response(0, "Invalid subProfessionId. Must be greater than 0.", null));
            }

            List<AuditionNewProjectWebModel> projects = projectService.getProjectsBySubProfession(subProfessionId);

            if (projects == null || projects.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(0, "No Auditions found for subProfessionId: " + subProfessionId, null));
            }

            return ResponseEntity.ok(new Response(1, "Success", projects));
        } catch (Exception e) {
            // log exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(0, "Something went wrong while fetching Audition.", null));
        }
    }

    @GetMapping("/ByCompany")
    public ResponseEntity<?> getProjectsByCompanyId(@RequestParam Integer companyId, @RequestParam(required = false) Integer teamNeedId, @RequestParam Integer professionId) {
        try {
            if (companyId == null || companyId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new Response(0, "Invalid companyId. Must be greater than 0.", null));
            }

            List<AuditionNewProjectWebModel> projects = projectService.getProjectsByCompanyIdAndTeamNeed(companyId, teamNeedId, professionId);

            if (projects.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(0, "No Audition found for companyId: " + companyId, null));
            }

            return ResponseEntity.ok(new Response(1, "Success", projects));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(0, "Something went wrong while fetching projects.", null));
        }
    }
    
  


	@GetMapping("/categories")
	public ResponseEntity<?> getCategories() {
		try {
			logger.info("Fetching movie categories");
			List<MovieCategory> categories = projectService.getAllCategories();
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
			List<MovieSubCategory> subCategories = projectService.getSubCategories(id);
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
	            List<FilmSubProfessionResponseDTO> subProfessions = projectService.getAllSubProfessions();
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
	            		projectService.getSubProfessionsByProfessionId(professionId);
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
	    	projectService.addToCart(userId, companyId, subProfessionId, count);
	        return ResponseEntity.ok(new Response(1, "Cart updated successfully", null));
	    }

	    @GetMapping("/cart")
	    public ResponseEntity<?> getCart(@RequestParam Integer userId,
	                                     @RequestParam Integer companyId) {
	        List<FilmSubProfessionResponseDTO> cart = projectService.getCart(userId, companyId);
	        return ResponseEntity.ok(cart);
	    }
	    @GetMapping("/professions")
	    public ResponseEntity<List<FilmProfessionResponseDTO>> getAllProfessions() {
	        logger.info("Fetching all professions");
	        return ResponseEntity.ok(projectService.getAllProfessions());
	    }

    @PostMapping("/toggleLike")
    public ResponseEntity<Response> toggleTeamNeedLike(
            @RequestParam Integer teamNeedId,
            @RequestParam Integer userId) {
        try {
            String result = projectService.toggleTeamNeedLike(teamNeedId, userId);
            return ResponseEntity.ok(new Response(1, result, null));

        } catch (IllegalArgumentException e) {
            // Missing or invalid input
            return ResponseEntity.badRequest()
                    .body(new Response(-1, e.getMessage(), null));

        } catch (EntityNotFoundException e) {
            // TeamNeed or User not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(-1, e.getMessage(), null));

        } catch (IllegalStateException e) {
            // TeamNeed inactive
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(-1, e.getMessage(), null));

        } catch (Exception e) {
 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Something went wrong. Please try again.", null));
        }
    }

    
    @PostMapping("/addView")
    public ResponseEntity<Response> addView(
    		@RequestParam Integer teamNeedId,
            @RequestParam Integer userId) {
        try {
        	projectService.addView(teamNeedId, userId);
            long views = projectService.getViewCount(teamNeedId);
            return ResponseEntity.ok(new Response(1, "View added", views));
        } catch (Exception e) {
            return ResponseEntity.ok(new Response(-1, "Failed to add view: " + e.getMessage(), ""));
        }
    }
    
    @PostMapping("/createPayment")
    public ResponseEntity<Response> createPayment(@RequestBody AuditionPaymentWebModel webModel) {
        try {
            AuditionPayment payment = projectService.createPayment(webModel);
	
			
			// 2️⃣ Fetch user
          
            AuditionPaymentWebModel responseWebModel = AuditionCompanyConverter.toWebModel(payment, key);

            return ResponseEntity.ok(new Response(1, "Payment created successfully", responseWebModel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Response(-1, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(-1, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Something went wrong. Please try again.", null));
        }
    }

    @PostMapping("/payment-success")
    public ResponseEntity<?> paymentSuccess(@RequestParam String txnid) {
        return projectService.paymentSuccess(txnid);
    }
    
    @PostMapping("/payment-failure")
    public ResponseEntity<?> paymentFailure(@RequestParam String txnid,
                                            @RequestParam(required = false) String errorMessage) {
        return projectService.paymentFailure(txnid, errorMessage != null ? errorMessage : "Unknown error");
    }
    
    @GetMapping("/payment/{txnid}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String txnid) {
        return projectService.getPaymentByTxnid(txnid);
    }

    @GetMapping("/auditionPayments")
    public AuditionPaymentDTO calculatePayment(
            @RequestParam Integer projectId,
            @RequestParam Integer userId,
            @RequestParam Integer selectedDays
    ) {
        return projectService.calculateAuditionPayment(projectId, userId, selectedDays);
    }

    
    @DeleteMapping("/deleteAuditionTeamNeed")
    public ResponseEntity<Response> deleteTeamNeed(
            @RequestParam Integer teamNeedId,
            @RequestParam Integer userId,
            @RequestParam Integer companyId) {
        try {
            projectService.softDeleteTeamNeed(teamNeedId, userId, companyId);
            return ResponseEntity.ok(new Response(1, "Audition deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Response(-1, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(-1, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Something went wrong. Please try again.", null));
        }
    }
    
}