package com.annular.filmhook.validator;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.annular.filmhook.UserDetails; // ✅ use your own class
import com.annular.filmhook.model.AuditionCompanyDetails.VerificationStatus;
import com.annular.filmhook.util.CustomValidator;
import com.annular.filmhook.util.DateUtils;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;

@Component
public class AuditionCompanyDetailsValidator implements Validator {

	private static final String BAD_REQUEST_ERROR_CD = "400";

	@Autowired 
	private UserDetails userDetails; // ✅ inject your security-based user util

	@Override
	public boolean supports(Class<?> clazz) {
		return AuditionCompanyDetailsDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		saveCompany((AuditionCompanyDetailsDTO) target, errors);
	}

	public void saveCompany(AuditionCompanyDetailsDTO dto, Errors errors) {
		String loggedUser = userDetails.userInfo().getUsername(); // ✅ get current username
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

		if (CustomValidator.isEmpty(dto.getCompanyName()))
			errors.rejectValue("companyName", BAD_REQUEST_ERROR_CD, "Company Name is empty or invalid");
		if (CustomValidator.isEmpty(dto.getLocation()))
			errors.rejectValue("location", BAD_REQUEST_ERROR_CD, "Location is empty or invalid");
		if (CustomValidator.isEmpty(dto.getCompanyType()))
			errors.rejectValue("companyType", BAD_REQUEST_ERROR_CD, "Company Type is empty or invalid");
		 if (dto.getCompanyCertificateFiles() == null || dto.getCompanyCertificateFiles().isEmpty()) {
	            errors.rejectValue(
	                    "companyCertificateFiles",
	                    BAD_REQUEST_ERROR_CD,
	                    "Company Certificate file is required");
	        }
	       // =========================
	        // GST (ONLY WHEN REGISTERED)
	        // =========================
		  if (Boolean.TRUE.equals(dto.isGstRegistered())) {

	            boolean hasGstNumber =
	                    !CustomValidator.isEmpty(dto.getGstNumber());

	            boolean hasGstCertificate =
	                    dto.getGstCertificateFiles() != null
	                            && !dto.getGstCertificateFiles().isEmpty();

	            // Either GST number OR GST certificate is required
	            if (!hasGstNumber && !hasGstCertificate) {
	                errors.rejectValue(
	                        "gstNumber",
	                        BAD_REQUEST_ERROR_CD,
	                        "GST Number or GST Certificate file is required");
	            }
	        }
		  
	       // =========================
	        // BUSINESS CERTIFICATE (ONLY WHEN TRUE)
	        // =========================

		 if (Boolean.TRUE.equals(dto.isBusinessCertificate())) {

	            // File is mandatory ONLY when businessCertificate = true
	            if (dto.getBusinessCertificateFiles() == null
	                    || dto.getBusinessCertificateFiles().isEmpty()) {

	                errors.rejectValue(
	                        "businessCertificateFiles",
	                        BAD_REQUEST_ERROR_CD,
	                        "Business Certificate file is required");
	            }
	            
	            
		if (dto.isGovtVerified() && CustomValidator.isEmpty(dto.getGovtVerificationLink())) 
		    errors.rejectValue("govtVerificationLink", BAD_REQUEST_ERROR_CD,
		        "Government Verification Link is required when the company is government verified");
		
	}

	//    public void updateCompany(AuditionCompanyDetailsDTO dto, Errors errors) {
	//        String loggedUser = userDetails.userInfo().getUsername();
	//        var now = DateUtils.getAsiaLocalDateTimeInCustomFormat();
	//
	//        if (CustomValidator.isEmpty(dto.getId()))
	//            errors.rejectValue("id", BAD_REQUEST_ERROR_CD, "Company ID is required for update");
	//        if (dto.isGstRegistered() && CustomValidator.isEmpty(dto.getGstNumber()))
	//            errors.rejectValue("gstNumber", BAD_REQUEST_ERROR_CD, "GST Number required when GST registered");
	//
	//        if (CustomValidator.isEmpty(dto.getStatus()))
	//            dto.setStatus("ACTIVE");
	//
	//        dto.setUpdatedBy(loggedUser);
	//        dto.setUpdatedDate(now);
	//    }
	}
	}
