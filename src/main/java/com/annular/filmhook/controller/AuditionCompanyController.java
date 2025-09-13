package com.annular.filmhook.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.security.UserDetailsImpl;
import com.annular.filmhook.service.AuditionCompanyService;
import com.annular.filmhook.service.impl.UserDetailsServiceImpl;
import com.annular.filmhook.validator.AuditionCompanyDetailsValidator;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;
import com.annular.filmhook.webmodel.AuditionUserCompanyRoleDTO;

@RestController
@RequestMapping("/api/companies")
public class AuditionCompanyController {

    @Autowired
    private AuditionCompanyService companyService;

    @Autowired
    private AuditionCompanyDetailsValidator companyValidator;
    
    @Autowired
	private  UserDetails userDetails;
    @Autowired
    private UserRepository userRepository;

    /**
     * Save Company Details
     */
    @PostMapping(consumes = {"multipart/form-data"}, path = "/saveAuditionCompany")
    public ResponseEntity<?> saveCompany(
            @RequestPart("company") AuditionCompanyDetailsDTO dto,
            @RequestPart(value = "logoFiles", required = false) MultipartFile[] logoFiles) {

        // Attach files
        if (logoFiles != null && logoFiles.length > 0) {
            dto.setLogoFiles(Arrays.asList(logoFiles));
        }

        // Validate DTO
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "auditionCompanyDetailsDTO");
        companyValidator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getAllErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.badRequest().body(new Response(0, "Validation failed", errors.toString()));
        }

        // Save company
        AuditionCompanyDetailsDTO saved = companyService.saveCompany(dto);

        return ResponseEntity.ok(new Response(1, "Success", saved));
    }
    
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCompanyByUserId(@PathVariable Integer userId) {
        // Simply call the service without validation
    	 List<AuditionCompanyDetailsDTO> companyDTO = companyService.getCompaniesByUserId(userId);
        return ResponseEntity.ok(new Response(1, "Success", companyDTO));
    }
      
    
    @GetMapping("/getAllCompanies")
    public ResponseEntity<?> getAllCompanies() {
        List<AuditionCompanyDetailsDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(new Response(1, "Success", companies));
    }
    
    
    @GetMapping("/activeAndPendingAudition")
    public ResponseEntity<List<AuditionCompanyDetailsDTO>> getActivePendingCompanies() {
        List<AuditionCompanyDetailsDTO> companies = companyService.getAllPendingCompanies();
        return ResponseEntity.ok(companies);
    }
    
    @PutMapping("/{companyId}/verify")
    public ResponseEntity<?> verifyCompany(@PathVariable Integer companyId, @RequestParam boolean approved) {
        AuditionCompanyDetails updatedCompany = companyService.updateVerificationStatus(companyId, approved);

        Map<String, Object> response = new HashMap<>();
        response.put("companyId", updatedCompany.getId());
        response.put("verificationStatus", updatedCompany.getVerificationStatus());
        response.put("message", approved ? "Company verification SUCCESS" : "Company verification FAILED");

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/StatusChange")
    public ResponseEntity<Response> continueCompany(
    		@RequestParam Integer companyId,
            @RequestParam Integer userId) {

        AuditionCompanyDetailsDTO updatedCompany = 
        		companyService.markCompanyAsContinued(companyId, userId);

        return ResponseEntity.ok(new Response(1, "Success", updatedCompany));
    }

    
    @PostMapping("/assignAccessCode")
    public ResponseEntity<AuditionUserCompanyRoleDTO> assignAccess(@RequestBody AuditionUserCompanyRoleDTO request) {
        return ResponseEntity.ok(companyService.assignAccess(request));
    }
    
    @PostMapping("/AuditionLogin")
    public ResponseEntity<Response> postAudition(
            @RequestParam(required = false) String filmHookCode,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String accessCode) {

        // ✅ Get current logged-in user
        Integer userId = userDetails.userInfo().getId();
        User loggedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        try {
            Object result = companyService.handleAuditionAccess(loggedUser, filmHookCode, designation, accessCode);
            return ResponseEntity.ok(new Response(1, "Success", result));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new Response(-1, e.getMessage(), null));
        }
    }

    
    @GetMapping("/Auditioncompany/{companyId}")
    public ResponseEntity<Response> getCompanyById(@PathVariable Integer companyId) {
        try {
            AuditionCompanyDetailsDTO dto = companyService.getCompanyById(companyId);
            return ResponseEntity.ok(new Response(1, "Success", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new Response(-1, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/remove-access/{AccessId}")
    public ResponseEntity<Response> removeAccess(@PathVariable Integer AccessId) {
    	try {
        companyService.removeAccess(AccessId);
        return ResponseEntity.ok(new Response(1, "Success","Access removed successfully"));
        
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(new Response(-1, e.getMessage(), null));
    }
    }
    
    
}
