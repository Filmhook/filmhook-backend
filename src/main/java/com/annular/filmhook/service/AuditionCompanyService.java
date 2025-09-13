package com.annular.filmhook.service;



import java.util.List;

import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;
import com.annular.filmhook.webmodel.AuditionUserCompanyRoleDTO;


public interface AuditionCompanyService {
	 AuditionCompanyDetailsDTO saveCompany(AuditionCompanyDetailsDTO dto);
	 List<AuditionCompanyDetailsDTO> getCompaniesByUserId(Integer userId);
	 List<AuditionCompanyDetailsDTO> getAllCompanies();
	 List<AuditionCompanyDetailsDTO> getAllPendingCompanies();
	 AuditionCompanyDetails updateVerificationStatus(Integer companyId, boolean approved);
	 AuditionCompanyDetailsDTO markCompanyAsContinued(Integer companyId, Integer userId);
	 AuditionUserCompanyRoleDTO assignAccess(AuditionUserCompanyRoleDTO request);
	 Object handleAuditionAccess(User loggedUser, String filmHookCode, String designation, String accessCode);
	 
	 AuditionCompanyDetailsDTO getCompanyById(Integer companyId);
	 void removeAccess(Integer roleId);
	 
	 

}
