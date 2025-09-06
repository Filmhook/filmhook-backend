package com.annular.filmhook.converter;


import java.util.List;

import com.annular.filmhook.model.*;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;
import com.annular.filmhook.webmodel.AuditionUserCompanyRoleDTO;
import com.annular.filmhook.webmodel.FileInputWebModel;

public class AuditionCompanyConverter {

    // Company → DTO
	  public static AuditionCompanyDetails toCompanyEntity(AuditionCompanyDetailsDTO dto, User user) {
	        if (dto == null) return null;

	        return AuditionCompanyDetails.builder()
	                .id(dto.getId())
	                .companyName(dto.getCompanyName())
	                .location(dto.getLocation())
	                .companyType(dto.getCompanyType())
	                .gstRegistered(dto.isGstRegistered())
	                .businessCertificate(dto.isBusinessCertificate())
	                .businessCertificateNumber(dto.getBusinessCertificateNumber())
	                .gstNumber(dto.getGstNumber())
	                .state(dto.getState())
	                .houseNumber(dto.getHouseNumber())
	                .landMark(dto.getLandMark())
	                .pinCode(dto.getPinCode())
	                .govtVerified(dto.isGovtVerified())
	                .govtVerificationLink(dto.getGovtVerificationLink())
	                .accessCode(dto.getAccessCode()) // will be null at creation
	                .verificationStatus(dto.getVerificationStatus() != null 
                    ? dto.getVerificationStatus() 
                    : AuditionCompanyDetails.VerificationStatus.PENDING) 
	                .user(user)
	              .status(false)
	              .accessCode(null)
	                .build();
	    }

	    // Entity -> DTO
	    public static AuditionCompanyDetailsDTO toCompanyDTO(AuditionCompanyDetails entity) {
	        if (entity == null) return null;

	        return AuditionCompanyDetailsDTO.builder()
	                .id(entity.getId())
	                .companyName(entity.getCompanyName())
	                .location(entity.getLocation())
	                .companyType(entity.getCompanyType())
	                .gstRegistered(entity.isGstRegistered())
	                .businessCertificate(entity.isBusinessCertificate())
	                .businessCertificateNumber(entity.getBusinessCertificateNumber())
	                .gstNumber(entity.getGstNumber())
	                .state(entity.getState())
	                .houseNumber(entity.getHouseNumber())
	                .landMark(entity.getLandMark())
	                .pinCode(entity.getPinCode())
	                .govtVerified(entity.isGovtVerified())
	                .govtVerificationLink(entity.getGovtVerificationLink())
	                .accessCode(entity.getAccessCode())
	                .verificationStatus(entity.getVerificationStatus())
	                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
	                .status(entity.getStatus())
	                .createdBy(entity.getCreatedBy())
	                .createdDate(entity.getCreatedDate())
	                .updatedBy(entity.getUpdatedBy())
	                .updatedDate(entity.getUpdatedDate())
	                .build();
	    }
	  
	    public static void handleCompanyLogoFile(AuditionCompanyDetailsDTO dto, AuditionCompanyDetails savedEntity, User user, MediaFilesService mediaFilesService) {
	        if (dto != null && dto.getLogoFiles() != null && !dto.getLogoFiles().isEmpty()) {
	            FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
	                    .userId(user.getUserId())
	                    .category(MediaFileCategory.Audition)
	                    .categoryRefId(savedEntity.getId())
	                    .files(dto.getLogoFiles()) 
	                   
	                    .build();

	            mediaFilesService.saveMediaFiles(fileInputWebModel, user);
	        }
	    }

    // UserCompanyRole → DTO
    public static AuditionUserCompanyRoleDTO toRoleDTO(AuditionUserCompanyRole entity) {
        if (entity == null) return null;
        return AuditionUserCompanyRoleDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .companyId(entity.getCompanyId())
                .designation(entity.getDesignation())
                .build();
    }

    // DTO → UserCompanyRole
    public static AuditionUserCompanyRole toRoleEntity(AuditionUserCompanyRoleDTO dto) {
        if (dto == null) return null;
        return AuditionUserCompanyRole.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .companyId(dto.getCompanyId())
                .designation(dto.getDesignation())
                .build();
    }
}
