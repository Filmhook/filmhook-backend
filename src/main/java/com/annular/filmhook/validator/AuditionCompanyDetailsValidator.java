package com.annular.filmhook.validator;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.annular.filmhook.CurrentUserContext; // ✅ use your own class
import com.annular.filmhook.model.AuditionCompanyDetails.VerificationStatus;
import com.annular.filmhook.util.CustomValidator;
import com.annular.filmhook.util.DateUtils;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;

@Component
public class AuditionCompanyDetailsValidator implements Validator {

	private static final String BAD_REQUEST_ERROR_CD = "400";

	@Autowired 
	private CurrentUserContext userDetails; // ✅ inject your security-based user util

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
		if (dto.isGstRegistered() && CustomValidator.isEmpty(dto.getGstNumber()))
			errors.rejectValue("gstNumber", BAD_REQUEST_ERROR_CD, "GST Number required when GST registered");
		if (dto.isBusinessCertificate() && CustomValidator.isEmpty(dto.getBusinessCertificateNumber()))
			errors.rejectValue("businessCertificateNumber", BAD_REQUEST_ERROR_CD, 
					"BusinessCertificate Number required when BusinessCertificate registered");
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
