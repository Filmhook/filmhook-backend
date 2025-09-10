package com.annular.filmhook.service;



import java.util.List;

import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;


public interface AuditionCompanyService {
	 AuditionCompanyDetailsDTO saveCompany(AuditionCompanyDetailsDTO dto);
	 List<AuditionCompanyDetailsDTO> getCompaniesByUserId(Integer userId);
	 List<AuditionCompanyDetailsDTO> getAllCompanies();
	 List<AuditionCompanyDetailsDTO> getAllActivePendingCompanies();
	 AuditionCompanyDetails updateVerificationStatus(Integer companyId, boolean approved);
	 AuditionCompanyDetailsDTO markCompanyAsContinued(Integer companyId, Integer userId);
	 
//	    AuditionCompanyDetailsDTO updateCompany(Long id, AuditionCompanyDetailsDTO dto);
//
//	    AuditionCompanyDetailsDTO getCompanyById(Long id);
//
//	    List<AuditionCompanyDetailsDTO> getAllCompanies();
//
//	    void deleteCompany(Long id);
//
//	    AuditionUserCompanyRoleDTO assignUserRole(AuditionUserCompanyRoleDTO dto);
}
