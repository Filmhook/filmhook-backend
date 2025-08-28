package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.converter.AuditionConverter;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.AuditionCompanyDetails.VerificationStatus;
import com.annular.filmhook.repository.AuditionCompanyRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionCompanyService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.util.CustomValidator;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service

public class AuditionCompanyServiceImpl implements AuditionCompanyService {

	 @Autowired
	 private AuditionCompanyRepository companyRepository;
	 @Autowired
	 private MediaFilesService mediaFilesService;
	 @Autowired
	 private UserRepository userRepository;
	 
    @Override
    public AuditionCompanyDetailsDTO saveCompany(AuditionCompanyDetailsDTO dto) {
      
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

        LocalDateTime now = LocalDateTime.now();
        AuditionCompanyDetails entity = AuditionConverter.toCompanyEntity(dto, user);


        entity.setCreatedBy(user.getName());
        entity.setUpdatedBy(user.getName());
        entity.setCreatedDate(now);
        entity.setUpdatedDate(now);
   
        AuditionCompanyDetails saved = companyRepository.save(entity);
        
        AuditionConverter.handleCompanyLogoFile(dto, saved, user, mediaFilesService);
     
        return AuditionConverter.toCompanyDTO(saved);
    }
    

    @Override
    public List<AuditionCompanyDetailsDTO> getCompaniesByUserId(Integer userId) {
        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Fetch all companies
        List<AuditionCompanyDetails> companies = companyRepository.findAllByUser(user);

        // Convert each company to DTO
        return companies.stream().map(company -> {
            AuditionCompanyDetailsDTO dto = AuditionConverter.toCompanyDTO(company);

            // Fetch logo files
            List<FileOutputWebModel> logoFiles = mediaFilesService
                    .getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, company.getId());

            if (!logoFiles.isEmpty()) {
                dto.setLogoFilesOutput(logoFiles); 

            }

            return dto;
        }).toList();
    }

    @Override
    public List<AuditionCompanyDetailsDTO> getAllCompanies() {
    	List<AuditionCompanyDetails> companies = companyRepository.findByStatusTrue();

        return companies.stream().map(company -> {
            AuditionCompanyDetailsDTO dto = AuditionConverter.toCompanyDTO(company);

            List<FileOutputWebModel> logoFiles = mediaFilesService
                    .getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, company.getId());

            if (!logoFiles.isEmpty()) {
                dto.setLogoFilesOutput(logoFiles);
            }

            return dto;
        }).toList();
    } 
    
    @Override
	public List<AuditionCompanyDetailsDTO> getAllActivePendingCompanies() {
	    List<AuditionCompanyDetails> companies = companyRepository.findByStatusTrueAndVerificationStatus(AuditionCompanyDetails.VerificationStatus.PENDING);

	    return companies.stream().map(company -> {
	        AuditionCompanyDetailsDTO dto = AuditionConverter.toCompanyDTO(company);

	        // Fetch only logo files
	        List<FileOutputWebModel> logoFiles = mediaFilesService
	                .getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, company.getId());

	        if (!logoFiles.isEmpty()) {
	            dto.setLogoFilesOutput(logoFiles);
	        }

	        return dto;
	    }).toList();
	}

    
} 

