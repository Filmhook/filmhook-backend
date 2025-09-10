package com.annular.filmhook.controller;



import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.Response;
import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.User;
import com.annular.filmhook.service.AuditionNewService;
import com.annular.filmhook.validator.AuditionCompanyDetailsValidator;
import com.annular.filmhook.validator.AuditionProjectValidator;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;

@RestController
@RequestMapping("/audition")
public class AuditionNewController {
	public static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	 @Autowired
	 AuditionProjectValidator projectValidator;
    @Autowired
    private AuditionNewService projectService;
    
    
    
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
    public ResponseEntity<?> getProjectsByCompanyId(@RequestParam Integer companyId, @RequestParam(required = false) Integer teamNeedId) {
        try {
            if (companyId == null || companyId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new Response(0, "Invalid companyId. Must be greater than 0.", null));
            }

            List<AuditionNewProjectWebModel> projects = projectService.getProjectsByCompanyIdAndTeamNeed(companyId, teamNeedId);

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

    @PostMapping("/toggleLike/{teamNeedId}")
    public ResponseEntity<Response> toggleTeamNeedLike(
            @PathVariable Integer teamNeedId,
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

    
    @PostMapping("/addView/{teamNeedId}")
    public ResponseEntity<Response> addView(
            @PathVariable Integer teamNeedId,
            @RequestParam Integer userId) {
        try {
        	projectService.addView(teamNeedId, userId);
            long views = projectService.getViewCount(teamNeedId);
            return ResponseEntity.ok(new Response(1, "View added", views));
        } catch (Exception e) {
            return ResponseEntity.ok(new Response(-1, "Failed to add view: " + e.getMessage(), ""));
        }
    }
    
    
    
}