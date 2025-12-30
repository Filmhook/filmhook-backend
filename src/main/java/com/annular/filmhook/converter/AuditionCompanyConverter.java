
package com.annular.filmhook.converter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.annular.filmhook.model.*;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
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
		dto.setStatus(entity.getStatus());
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
			dto.setSubProfessionName(entity.getSubProfession().getSubProfessionName());
			;
		}
		if (entity.getProfession() != null) {
			dto.setProfessionId(entity.getProfession().getFilmProfessionId());
			dto.setProfessionName(entity.getProfession().getProfessionName());
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

	public static void handleProjectProfilePictureFile1(
			AuditionNewProjectWebModel dto,
			AuditionNewProject savedEntity,
			User user,
			MediaFilesService mediaFilesService) {

		if (dto != null && dto.getProfilePictureFiles() != null && !dto.getProfilePictureFiles().isEmpty()) {

			// ✅ Clear old files 
			mediaFilesService.deleteMediaFilesByCategoryAndRefIds(
					MediaFileCategory.AuditionProfilePicture,
					List.of(savedEntity.getId()) // wrap single ID in a list
					);
			// ✅ Save new files
			FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
					.userId(user.getUserId())
					.category(MediaFileCategory.AuditionProfilePicture)
					.categoryRefId(savedEntity.getId())
					.files(dto.getProfilePictureFiles())
					.build();

			mediaFilesService.saveMediaFiles(fileInputWebModel, user);
		}
	}





	// ✅ Update Project Entity from DTO (no overwrite of audit fields)
	public static void updateEntityFromDto(
			AuditionNewProject entity,
			AuditionNewProjectWebModel dto,
			AuditionCompanyDetails company,
			Integer userId,
			FilmSubProfessionRepository subProfessionRepo) {

		entity.setProductionCompanyName(dto.getProductionCompanyName());
		entity.setProjectTitle(dto.getProjectTitle());
		entity.setCountry(dto.getCountry());
		entity.setIndustries(dto.getIndustries());
		entity.setDubbedCountry(dto.getDubbedCountry());
		entity.setDubbedIndustries(dto.getDubbedIndustries());
		entity.setPlatforms(dto.getPlatforms());
		entity.setMovieTypes(dto.getMovieTypes());
		entity.setThemeMovieTypes(dto.getThemeMovieTypes());
		entity.setAuditionAddress(dto.getAuditionFullAddress());
		entity.setLocationWebsite(dto.getLocationWebsite());
		entity.setInterNationalShootLocations(dto.getInterNationalShootLocations());
		entity.setNationalShootLocations(dto.getNationalShootLocations());
		entity.setShootStartDate(dto.getShootStartDate());
		entity.setShootEndDate(dto.getShootEndDate());
		entity.setProjectDescription(dto.getProjectDescription());
		entity.setAuditionProfilePicture(dto.getAuditionProfilePicture());
		entity.setCompany(company);

		// Do NOT reset createdBy/createdOn
		entity.setUpdatedBy(userId);
		entity.setUpdatedDate(LocalDateTime.now());
	}
	public static void updateTeamNeedEntity(
			AuditionNewTeamNeed entity,
			AuditionNewTeamNeedWebModel dto,
			FilmSubProfessionRepository subProfessionRepo) {

		entity.setCount(dto.getCount());
		entity.setCharacterName(dto.getCharacterName());
		entity.setGender(dto.getGender());
		entity.setAgeFrom(dto.getAgeFrom());
		entity.setAgeTo(dto.getAgeTo());
		entity.setEthnicity(dto.getEthnicity());
		entity.setHeightMin(dto.getHeightMin());
		entity.setHeightMax(dto.getHeightMax());
		entity.setBodyType(dto.getBodyType());
		entity.setRegionalDemonyms(dto.getRegionalDemonyms());
		entity.setOpportunity(dto.getOpportunity());
		entity.setExperienceYears(dto.getExperienceYears());
		entity.setRolesResponsibilities(dto.getRolesResponsibilities());
		entity.setSalary(dto.getSalary());
		entity.setSalaryType(dto.getSalaryType());
		entity.setPaymentMode(dto.getPaymentMode());
		entity.setWorkDays(dto.getWorkDays());
		entity.setFacilitiesProvided(dto.getFacilitiesProvided());
		entity.setDateOfShoot(dto.getDateOfShoot());

		// update profession/subProfession
		if (dto.getProfessionId() != null) {
			FilmProfession profession = new FilmProfession();
			profession.setFilmProfessionId(dto.getProfessionId());
			entity.setProfession(profession);
		}

		if (dto.getSubProfessionId() != null) {
			FilmSubProfession subProfession = subProfessionRepo.findById(dto.getSubProfessionId())
					.orElseThrow(() -> new RuntimeException("SubProfession not found"));
			entity.setSubProfession(subProfession);
			entity.setRole(subProfession.getSubProfessionName());
		}

		entity.setStatus(true); // mark active on update
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
				.assignedUserName(entity.getAssignedUser() != null ? entity.getAssignedUser().getFirstName() + " " + entity.getAssignedUser().getLastName() : null)
				.assignedUserEmail(entity.getAssignedUser() != null ? entity.getAssignedUser().getEmail() : null)
				.ownerName(entity.getOwner() != null ? entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName() : null)
				.ownerEmail(entity.getOwner() != null ? entity.getOwner().getEmail() : null)
				.build();

	}

	public static AuditionUserCompanyRoleDTO toDto(
			AuditionUserCompanyRole entity,
			UserService userService) {

		AuditionUserCompanyRoleDTO dto = AuditionUserCompanyRoleDTO.builder()
				.id(entity.getId())
				.ownerId(entity.getOwner() != null ? entity.getOwner().getUserId() : null)
				.assignedUserId(entity.getAssignedUser() != null ? entity.getAssignedUser().getUserId() : null)
				.companyId(entity.getCompany() != null ? entity.getCompany().getId() : null)
				.filmHookCode(entity.getFilmHookCode())
				.designation(entity.getDesignation())
				.accessKey(entity.getAccessKey())
				.status(entity.getStatus())
				.isOwner(false)
				.createdDate(entity.getCreatedDate())
				.assignedUserName(entity.getAssignedUser() != null ? 
						entity.getAssignedUser().getFirstName() + " " + entity.getAssignedUser().getLastName() : null)
				.assignedUserEmail(entity.getAssignedUser() != null ? entity.getAssignedUser().getEmail() : null)
				.ownerName(entity.getOwner() != null ? 
						entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName() : null)
				.ownerEmail(entity.getOwner() != null ? entity.getOwner().getEmail() : null)
				.build();

		// ✅ Attach assigned user profile picture using userService
		if (entity.getAssignedUser() != null) {
			String profilePicUrl = userService.getRecieverProfilePicUrl(entity.getAssignedUser().getUserId());
			dto.setAssignedUserProfilePicture(profilePicUrl);
		}

		return dto;
	}
	// DTO → Entity for User-Company Role
	public static AuditionUserCompanyRole toEntity(
	        User owner,
	        User assignedUser,
	        AuditionCompanyDetails company,
	        String accessKey) {

	    AuditionUserCompanyRole role = new AuditionUserCompanyRole();
	    role.setOwner(owner);
	    role.setAssignedUser(assignedUser);
	    role.setFilmHookCode(assignedUser.getFilmHookCode());
	    role.setCompany(company);
	    role.setAccessKey(accessKey);
	    role.setStatus(true);
	    role.setDeleted(false);
	    role.setCreatedDate(LocalDateTime.now());
	    role.setCreatedBy(owner.getUserId());
	    return role;
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
		dto.setFirstname(entity.getUser().getFirstName());
		dto.setEmail(entity.getUser().getEmail());
		dto.setProductinfo("Audition Booking");
		dto.setPhoneNumber(entity.getUser().getPhoneNumber());
		return dto;
	}



}