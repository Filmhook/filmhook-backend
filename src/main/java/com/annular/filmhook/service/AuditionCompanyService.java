package com.annular.filmhook.service;



import java.util.List;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;
import com.annular.filmhook.webmodel.AuditionUserCompanyRoleDTO;


public interface AuditionCompanyService {
	 AuditionCompanyDetailsDTO saveCompany(AuditionCompanyDetailsDTO dto);
	 List<AuditionCompanyDetailsDTO> getCompaniesByUserId(Integer userId);
	 List<AuditionCompanyDetailsDTO> getAllCompanies();
	 List<AuditionCompanyDetailsDTO> getCompaniesByVerificationStatus(
		        AuditionCompanyDetails.VerificationStatus verificationStatus);
	 AuditionCompanyDetails updateVerificationStatus(Integer companyId, boolean approved);
	 AuditionCompanyDetailsDTO markCompanyAsContinued(Integer companyId, Integer userId);
	 AuditionUserCompanyRoleDTO assignAccess(AuditionUserCompanyRoleDTO request);
	 Object handleAuditionAccess(User loggedUser, String filmHookCode, String designation, String accessCode);	 
	 AuditionCompanyDetailsDTO getCompanyById(Integer companyId);
	 void removeAccess(Integer roleId);
	 void softDeleteCompany(Integer companyId);
	 
	 List<AuditionCompanyDetailsDTO> getCompaniesForLoggedInUser(Integer userId);

}
