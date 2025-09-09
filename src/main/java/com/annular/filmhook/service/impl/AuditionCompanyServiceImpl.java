package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;

import com.annular.filmhook.repository.AuditionCompanyRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionCompanyService;
import com.annular.filmhook.service.MediaFilesService;

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
	     AuditionCompanyDetails entity;

	     if (dto.getId() != null) {
	         // ✅ Existing company update
	         entity = companyRepository.findById(dto.getId())
	                 .orElseThrow(() -> new RuntimeException("Company not found with ID: " + dto.getId()));

	         // If previously FAILED, reset verification to PENDING
	         if (entity.getVerificationStatus() == AuditionCompanyDetails.VerificationStatus.FAILED) {
	             entity.setVerificationStatus(AuditionCompanyDetails.VerificationStatus.PENDING);
	         }

	         // Update fields from DTO
	         entity.setCompanyName(dto.getCompanyName());
	         entity.setLocation(dto.getLocation());
	         entity.setCompanyType(dto.getCompanyType());
	         entity.setGstRegistered(dto.isGstRegistered());
	         entity.setBusinessCertificate(dto.isBusinessCertificate());
	         entity.setBusinessCertificateNumber(dto.getBusinessCertificateNumber());
	         entity.setGstNumber(dto.getGstNumber());
	         entity.setState(dto.getState());
	         entity.setHouseNumber(dto.getHouseNumber());
	         entity.setLandMark(dto.getLandMark());
	         entity.setPinCode(dto.getPinCode());
	         entity.setGovtVerified(dto.isGovtVerified());
	         entity.setGovtVerificationLink(dto.getGovtVerificationLink());
	         entity.setUpdatedBy(user.getUserId());
	         entity.setUpdatedDate(now);

	         // ✅ Handle logo update: delete existing and upload new
	         if (dto.getLogoFiles() != null) {
	             mediaFilesService.deleteMediaFilesByCategoryAndRefIds(
	                 MediaFileCategory.Audition, 
	                 List.of(entity.getId())
	             );
	             AuditionCompanyConverter.handleCompanyLogoFile(dto, entity, user, mediaFilesService);
	         }

	     } else {
	         // ✅ New company creation
	         entity = AuditionCompanyConverter.toCompanyEntity(dto, user);
	         entity.setCreatedBy(user.getUserId());
	         entity.setCreatedDate(now);
	         entity.setUpdatedDate(now);

	         AuditionCompanyDetails saved = companyRepository.save(entity);

	         // Save logo if provided
	         if (dto.getLogoFiles() != null) {
	             AuditionCompanyConverter.handleCompanyLogoFile(dto, saved, user, mediaFilesService);
	         }

	         return AuditionCompanyConverter.toCompanyDTO(saved);
	     }

	     AuditionCompanyDetails saved = companyRepository.save(entity);
	     return AuditionCompanyConverter.toCompanyDTO(saved);
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
            AuditionCompanyDetailsDTO dto = AuditionCompanyConverter.toCompanyDTO(company);

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
            AuditionCompanyDetailsDTO dto = AuditionCompanyConverter.toCompanyDTO(company);

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
	        AuditionCompanyDetailsDTO dto = AuditionCompanyConverter.toCompanyDTO(company);

	        // Fetch only logo files
	        List<FileOutputWebModel> logoFiles = mediaFilesService
	                .getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, company.getId());

	        if (!logoFiles.isEmpty()) {
	            dto.setLogoFilesOutput(logoFiles);
	        }

	        return dto;
	    }).toList();
	}
    
    
    @Override
    public AuditionCompanyDetails updateVerificationStatus(Integer companyId, boolean approved) {
        try {
            AuditionCompanyDetails company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

            company.setVerificationStatus(
                approved ? AuditionCompanyDetails.VerificationStatus.SUCCESS 
                         : AuditionCompanyDetails.VerificationStatus.FAILED
            );

            return companyRepository.save(company);
        } catch (Exception e) {
            e.printStackTrace(); 
            throw e; 
        }
    }
    
    @Override
    public AuditionCompanyDetailsDTO markCompanyAsContinued(Integer companyId, Integer userId) {
        AuditionCompanyDetails company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

        // ✅ Just change status, nothing else
        company.setStatus(true);

        AuditionCompanyDetails updated = companyRepository.save(company);
        return AuditionCompanyConverter.toCompanyDTO(updated);
    }


    
} 

