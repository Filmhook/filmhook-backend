package com.annular.filmhook.service;



import java.util.List;

import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;


public interface AuditionCompanyService {
	 AuditionCompanyDetailsDTO saveCompany(AuditionCompanyDetailsDTO dto);
	 List<AuditionCompanyDetailsDTO> getCompaniesByUserId(Integer userId);
	 List<AuditionCompanyDetailsDTO> getAllCompanies();
	 List<AuditionCompanyDetailsDTO> getAllActivePendingCompanies();
	 
	 
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
