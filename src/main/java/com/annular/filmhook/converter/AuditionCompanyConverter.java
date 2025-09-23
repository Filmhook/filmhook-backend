package com.annular.filmhook.converter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.annular.filmhook.model.*;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
import com.annular.filmhook.webmodel.AuditionNewTeamNeedWebModel;
import com.annular.filmhook.webmodel.AuditionPaymentWebModel;
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
				.deleted(false)
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


	// ======================
	// PROJECT
	// ======================

	// DTO → Entity
	   public static AuditionNewProject toEntity(
	            AuditionNewProjectWebModel dto,
	            AuditionCompanyDetails company,
	            Integer userId,
	            FilmSubProfessionRepository subProfessionRepo) {

	        AuditionNewProject project = AuditionNewProject.builder()
	                .productionCompanyName(dto.getProductionCompanyName())
	                .projectTitle(dto.getProjectTitle())
	                .country(dto.getCountry())
	                .industries(dto.getIndustries())
	                .dubbedCountry(dto.getDubbedCountry())
	                .dubbedIndustries(dto.getDubbedIndustries())
	                .platforms(dto.getPlatforms())
	                .movieTypes(dto.getMovieTypes())
	                .themeMovieTypes(dto.getThemeMovieTypes())
	                .auditionAddress(dto.getAuditionFullAddress())
	                .locationWebsite(dto.getLocationWebsite())
	                .interNationalShootLocations(dto.getInterNationalShootLocations())
	                .nationalShootLocations(dto.getNationalShootLocations())
	                .shootStartDate(dto.getShootStartDate())
	                .shootEndDate(dto.getShootEndDate())
	                .projectDescription(dto.getProjectDescription())
	                .status(false)
	                .createdBy(userId)
	                .createdOn(LocalDateTime.now())
	                .auditionProfilePicture(dto.getAuditionProfilePicture())
	                .company(company)
	                .build();

	        // Map TeamNeeds
	        if (dto.getTeamNeeds() != null) {
	            List<AuditionNewTeamNeed> teamNeeds = dto.getTeamNeeds().stream()
	                    .map(teamDto -> toEntity(teamDto, project, userId, subProfessionRepo))
	                    .collect(Collectors.toList());
	            project.setTeamNeeds(teamNeeds);
	        }

	        return project;
	    }

	// TeamNeed DTO → Entity
	        public static AuditionNewTeamNeed toEntity(
	                AuditionNewTeamNeedWebModel dto,
	                AuditionNewProject project,
	                Integer userId,
	                FilmSubProfessionRepository subProfessionRepo) {
		AuditionNewTeamNeed entity = AuditionNewTeamNeed.builder()
				.count(dto.getCount())
				.characterName(dto.getCharacterName())
				.gender(dto.getGender())
				.ageFrom(dto.getAgeFrom())
				.ageTo(dto.getAgeTo())
				.ethnicity(dto.getEthnicity())
				.heightMin(dto.getHeightMin())
				.heightMax(dto.getHeightMax())
				.bodyType(dto.getBodyType())
				.regionalDemonyms(dto.getRegionalDemonyms())
				.opportunity(dto.getOpportunity())
				.experienceYears(dto.getExperienceYears())
				.rolesResponsibilities(dto.getRolesResponsibilities())
				.salary(dto.getSalary())
				.salaryType(dto.getSalaryType())
				.paymentMode(dto.getPaymentMode())
				.workDays(dto.getWorkDays())
				.facilitiesProvided(dto.getFacilitiesProvided())
				.status(true)  
				.createdBy(userId)   
				.createdDate(LocalDateTime.now())
				.project(project)
				.dateOfShoot(dto.getDateOfShoot())
				.build();
		// ✅ map professionId → FilmProfession entity
		if (dto.getProfessionId() != null) {
			FilmProfession profession = new FilmProfession();
			profession.setFilmProfessionId(dto.getProfessionId());
			entity.setProfession(profession);
		}

		 if (dto.getSubProfessionId() != null) {
		        FilmSubProfession subProfession = subProfessionRepo
		                .findById(dto.getSubProfessionId())
		                .orElseThrow(() -> new RuntimeException("SubProfession not found"));

		        entity.setSubProfession(subProfession);
		        entity.setRole(subProfession.getSubProfessionName());
		    }

		return entity;
	}

	// Entity → DTO
	public static AuditionNewProjectWebModel toDto(AuditionNewProject entity) {

		AuditionNewProjectWebModel dto = new AuditionNewProjectWebModel();
		dto.setId(entity.getId());
		dto.setProductionCompanyName(entity.getProductionCompanyName());
		dto.setProjectTitle(entity.getProjectTitle());
		dto.setCountry(entity.getCountry());
		dto.setIndustries(entity.getIndustries());
		dto.setDubbedCountry(entity.getDubbedCountry());
		dto.setDubbedIndustries(entity.getDubbedIndustries());
		dto.setPlatforms(entity.getPlatforms());
		dto.setMovieTypes(entity.getMovieTypes());
		dto.setThemeMovieTypes(entity.getThemeMovieTypes());
		dto.setShootStartDate(entity.getShootStartDate());
		dto.setShootEndDate(entity.getShootEndDate());
		dto.setAuditionFullAddress(entity.getAuditionAddress());
		dto.setLocationWebsite(entity.getLocationWebsite());
		dto.setInterNationalShootLocations(entity.getInterNationalShootLocations());
		dto.setNationalShootLocations(entity.getNationalShootLocations());
		dto.setProjectDescription(entity.getProjectDescription());
		dto.setAuditionProfilePicture(entity.getAuditionProfilePicture());
		dto.setCompanyId(entity.getCompany().getId());
		dto.setGstNumber(entity.getCompany().getGstNumber());
		if (entity.getTeamNeeds() != null) {
			List<AuditionNewTeamNeedWebModel> teamDtos = entity.getTeamNeeds().stream()
					.map(AuditionCompanyConverter::toDto)
					.collect(Collectors.toList());
			dto.setTeamNeeds(teamDtos);
		}

		// ✅ Sum all counts from team needs
		int totalCount = entity.getTeamNeeds().stream()
				.mapToInt(tn -> tn.getCount() != null ? tn.getCount() : 0)
				.sum();
		dto.setTotalTeamNeedCount(totalCount);

		return dto;
	}

	// TeamNeed Entity → DTO
	public static AuditionNewTeamNeedWebModel toDto(AuditionNewTeamNeed entity) {
		AuditionNewTeamNeedWebModel dto = new AuditionNewTeamNeedWebModel();
		dto.setId(entity.getId());
		dto.setRole(entity.getRole());
		dto.setCount(entity.getCount());
		dto.setCharacterName(entity.getCharacterName());
		dto.setGender(entity.getGender());
		dto.setAgeFrom(entity.getAgeFrom());
		dto.setAgeTo(entity.getAgeTo());
		dto.setEthnicity(entity.getEthnicity());
		dto.setHeightMin(entity.getHeightMin());
		dto.setHeightMax(entity.getHeightMax());
		dto.setBodyType(entity.getBodyType());
		dto.setRegionalDemonyms(entity.getRegionalDemonyms());
		dto.setOpportunity(entity.getOpportunity());
		dto.setExperienceYears(entity.getExperienceYears());
		dto.setRolesResponsibilities(entity.getRolesResponsibilities());
		dto.setSalary(entity.getSalary());
		dto.setSalaryType(entity.getSalaryType());
		dto.setPaymentMode(entity.getPaymentMode());
		dto.setCreatedDate(entity.getCreatedDate());
		dto.setStatus(entity.getStatus());	
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setWorkDays(entity.getWorkDays());
		dto.setFacilitiesProvided(entity.getFacilitiesProvided());
		dto.setDateOfShoot(entity.getDateOfShoot());
		if (entity.getSubProfession() != null) {
			dto.setSubProfessionId(entity.getSubProfession().getSubProfessionId());
		}
		if (entity.getProfession() != null) {
			dto.setProfessionId(entity.getProfession().getFilmProfessionId());
		}


		return dto;

	}

	// Handle project profile picture file upload
	public static void handleProjectProfilePictureFile(AuditionNewProjectWebModel dto,
			AuditionNewProject savedEntity,
			User user,
			MediaFilesService mediaFilesService) {
		if (dto != null && dto.getProfilePictureFiles() != null && !dto.getProfilePictureFiles().isEmpty()) {
			FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
					.userId(user.getUserId())
					.category(MediaFileCategory.AuditionProfilePicture)
					.categoryRefId(savedEntity.getId())
					.files(dto.getProfilePictureFiles())
					.build();

			mediaFilesService.saveMediaFiles(fileInputWebModel, user);
		}
	}



	// UserCompanyRole 

	// Convert Request DTO → Entity
	public static AuditionUserCompanyRole toEntity(
			AuditionUserCompanyRoleDTO dto,
			User owner,
			User assignedUser,
			AuditionCompanyDetails company,
			String accessKey
			) {
		return AuditionUserCompanyRole.builder()
				.owner(owner)
				.company(company)
				.assignedUser(assignedUser)
				.designation(dto.getDesignation())
				.filmHookCode(dto.getFilmHookCode()) 
				.accessKey(accessKey)
				.status(true)
				.createdBy(dto.getOwnerId())
				.createdDate(java.time.LocalDateTime.now())
				.build();
	}

	// Convert Entity → Response DTO
	public static AuditionUserCompanyRoleDTO toDto(AuditionUserCompanyRole entity) {

		return AuditionUserCompanyRoleDTO.builder()
				.id(entity.getId())
				.ownerId(entity.getOwner() != null ? entity.getOwner().getUserId() : null) // ✅ ownerId
				.assignedUserId(entity.getAssignedUser() != null ? entity.getAssignedUser().getUserId() : null) // ✅ assignedUserId
				.companyId(entity.getCompany() != null ? entity.getCompany().getId() : null)
				.filmHookCode(entity.getFilmHookCode()) // ✅ FH code
				.designation(entity.getDesignation())
				.accessKey(entity.getAccessKey())
				.status(entity.getStatus())
				.isOwner(false)
				.createdDate(entity.getCreatedDate())
				.build();
	}

	public static AuditionUserCompanyRoleDTO toDto(AuditionUserCompanyRole entity, User loggedUser) {
		boolean isOwner = entity.getOwner() != null && entity.getOwner().getUserId().equals(loggedUser.getUserId());

		return AuditionUserCompanyRoleDTO.builder()
				.id(entity.getId())
				.ownerId(entity.getOwner() != null ? entity.getOwner().getUserId() : null)
				.assignedUserId(entity.getAssignedUser() != null ? entity.getAssignedUser().getUserId() : null)
				.companyId(entity.getCompany() != null ? entity.getCompany().getId() : null)
				.filmHookCode(entity.getFilmHookCode())
				.designation(entity.getDesignation())
				.accessKey(entity.getAccessKey())
				.status(entity.getStatus())
				.isOwner(isOwner)
				.createdDate(entity.getCreatedDate())
				.build();
	}

	//	   AuditionPayment

	public static AuditionPayment toEntity(AuditionPaymentWebModel dto, AuditionNewProject project, User user) {
		//		int teamNeedsCount = project.getTeamNeeds().stream()
		//				.mapToInt(AuditionNewTeamNeed::getCount)
		//				.sum();


		//		double totalAmount = teamNeedsCount * dto.getSelectedDays() * 20.0;
		LocalDateTime now = LocalDateTime.now();

		// Success/expiry date based on selectedDays
		LocalDateTime successDate = now;
		LocalDateTime expiryDate = null;
		if (dto.getSelectedDays() != null && dto.getSelectedDays() > 0) {
			expiryDate = now.plusDays(dto.getSelectedDays());
		}

		return AuditionPayment.builder()
				.project(project)
				.user(user)
				.txnid(dto.getTxnid())
				.totalAmount(dto.getTotalAmount())
				.totalTeamNeeds(dto.getTotalTeamNeed())
				.selectedDays(dto.getSelectedDays())
				.paymentStatus("PENDING")
				.createdBy(user.getUserId())
				.createdOn(now)
				.successDateTime(successDate)
				.expiryDateTime(expiryDate)
				.build();
	}

	public static AuditionPaymentWebModel toWebModel(AuditionPayment entity, String key) {
		AuditionPaymentWebModel dto = new AuditionPaymentWebModel();
		dto.setAuditionPaymentId(entity.getAuditionPaymentId());
		dto.setProjectId(entity.getProject().getId());
		dto.setUserId(entity.getUser().getUserId());
		dto.setSelectedDays(entity.getSelectedDays());
		dto.setTxnid(entity.getTxnid());
		dto.setPaymentStatus(entity.getPaymentStatus());
		dto.setReason(entity.getReason());
		dto.setPaymentHash(entity.getPaymentHash());
		dto.setTotalAmount(entity.getTotalAmount());
		dto.setTotalTeamNeed(entity.getTotalTeamNeeds());
dto.setKey(key);
		return dto;
	}

}