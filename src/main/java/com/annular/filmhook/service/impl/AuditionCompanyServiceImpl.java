package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.AuditionCompanyDetails.VerificationStatus;
import com.annular.filmhook.model.AuditionUserCompanyRole;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;

import com.annular.filmhook.repository.AuditionCompanyRepository;
import com.annular.filmhook.repository.AuditionUserCompanyRoleRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.security.UserDetailsImpl;
import com.annular.filmhook.service.AuditionCompanyService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.webmodel.AuditionCompanyDetailsDTO;
import com.annular.filmhook.webmodel.AuditionUserCompanyRoleDTO;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service

public class AuditionCompanyServiceImpl implements AuditionCompanyService {

	@Autowired
	private AuditionCompanyRepository companyRepository;
	@Autowired
	private MediaFilesService mediaFilesService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MailNotification mailNotification; 
	@Autowired
	private  UserDetails userDetails;
	@Autowired
	private AuditionUserCompanyRoleRepository roleRepository;
	@Autowired
	  private UserService userService;
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
			entity.setStatus(false);
			entity.setVerificationStatus(VerificationStatus.PENDING);

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

	    // Fetch all companies that are not deleted
	    List<AuditionCompanyDetails> companies = companyRepository.findAllByUserAndDeletedFalse(user);

	    // Sort: SUCCESS first, then others
	    companies.sort((c1, c2) -> {
	        if (c1.getVerificationStatus() == AuditionCompanyDetails.VerificationStatus.SUCCESS &&
	            c2.getVerificationStatus() != AuditionCompanyDetails.VerificationStatus.SUCCESS) {
	            return -1;
	        } else if (c1.getVerificationStatus() != AuditionCompanyDetails.VerificationStatus.SUCCESS &&
	                   c2.getVerificationStatus() == AuditionCompanyDetails.VerificationStatus.SUCCESS) {
	            return 1; 
	        } else {
	            return 0;
	        }
	    });

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
	public List<AuditionCompanyDetailsDTO> getCompaniesByVerificationStatus(
	        AuditionCompanyDetails.VerificationStatus verificationStatus) {

	    List<AuditionCompanyDetails> companies;

	    if (verificationStatus == AuditionCompanyDetails.VerificationStatus.SUCCESS) {
	        // Success → status = true
	        companies = companyRepository.findByVerificationStatusAndStatusAndDeletedFalse(
	                AuditionCompanyDetails.VerificationStatus.SUCCESS, true);
	    } else if (verificationStatus == AuditionCompanyDetails.VerificationStatus.PENDING ||
	               verificationStatus == AuditionCompanyDetails.VerificationStatus.FAILED) {
	        // Pending/Failed → status = false
	        companies = companyRepository.findByVerificationStatusAndStatusAndDeletedFalse(
	                verificationStatus, false);
	    } else {
	        companies = new ArrayList<>();
	    }

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

	@Override
	public AuditionUserCompanyRoleDTO assignAccess(AuditionUserCompanyRoleDTO request) {

	    // Validate owner
	    User owner = userRepository.findById(request.getOwnerId())
	            .orElseThrow(() -> new RuntimeException("Owner not found"));

	    // Validate company
	    AuditionCompanyDetails company = companyRepository.findById(request.getCompanyId())
	            .orElseThrow(() -> new RuntimeException("Company not found"));

	    //  Validate assigned user
	    User assignedUser = userRepository.findByFilmHookCode(request.getFilmHookCode())
	            .orElseThrow(() -> new RuntimeException(
	                    "Assigned user not found with FilmHook Code: " + request.getFilmHookCode()
	            ));

	    //  Check if role exists (even soft-deleted)
	    Optional<AuditionUserCompanyRole> existingRoleOpt =
	            roleRepository.findByCompanyAndAssignedUser(company, assignedUser);

	    if (existingRoleOpt.isPresent()) {
	        AuditionUserCompanyRole existingRole = existingRoleOpt.get();

	        // Case A: Active & not deleted → block
	        if (Boolean.TRUE.equals(existingRole.getStatus()) && Boolean.FALSE.equals(existingRole.getDeleted())) {
	            throw new RuntimeException("Access already assigned to this user for the given company");
	        }

	        // Case B: Inactive & not deleted → reactivate
	        if (Boolean.FALSE.equals(existingRole.getStatus()) && Boolean.FALSE.equals(existingRole.getDeleted())) {
	            existingRole.setStatus(true);
	            existingRole.setDesignation(request.getDesignation());
	            existingRole.setAccessKey(
	                    (request.getAccessKey() != null && !request.getAccessKey().isBlank())
	                            ? request.getAccessKey()
	                            : UUID.randomUUID().toString().substring(0, 8).toUpperCase()
	            );
	            existingRole.setUpdatedBy(request.getOwnerId());
	            existingRole.setUpdatedDate(LocalDateTime.now());

	            AuditionUserCompanyRole reactivated = roleRepository.save(existingRole);
	            sendAssignAccessEmails(owner, assignedUser, company, reactivated.getAccessKey(), reactivated.getDesignation());

	            return AuditionCompanyConverter.toDto(reactivated);
	        }

	        // Case C: Deleted → skip reactivation and create new instead
	        if (Boolean.TRUE.equals(existingRole.getDeleted())) {
	           
	        }
	    }

	    // Create new role if none exists OR deleted = true
	    String accessKey = (request.getAccessKey() != null && !request.getAccessKey().isBlank())
	            ? request.getAccessKey()
	            : UUID.randomUUID().toString().substring(0, 8).toUpperCase();

	    AuditionUserCompanyRole newRole = AuditionCompanyConverter
	            .toEntity(request, owner, assignedUser, company, accessKey);

	    newRole.setDeleted(false);
	    newRole.setStatus(true);

	    newRole = roleRepository.save(newRole);

	    sendAssignAccessEmails(owner, assignedUser, company, accessKey, request.getDesignation());

	    return AuditionCompanyConverter.toDto(newRole);
	}


	@Async
	public void sendAssignAccessEmails(User owner, User assignedUser, AuditionCompanyDetails company, String accessKey,String designation) {
		// Email to Assigned User
		String assignedSubject = "Access Granted for Company: " + company.getCompanyName();
		String assignedContent = "<p>You have been granted access to the company <b>" + company.getCompanyName() + "</b>.</p>"
				+ "<p><b>Designation:</b> " + designation + "</p>"
				+ "<p><b>Access Key:</b> " + accessKey + "</p>"
				+ "<p>Use this key to log in and manage your assigned company roles.</p>";
		mailNotification.sendEmail(assignedUser.getName(), assignedUser.getEmail(), assignedSubject, assignedContent);

		// Email to Owner
		String ownerSubject = "Access Assigned Successfully";
		String ownerContent = "<p>You have successfully assigned access to user <b>" + assignedUser.getName() + "</b> "
				+ "for your company <b>" + company.getCompanyName() + "</b>.</p>"
				+ "<p><b>Designation:</b> " + designation + "</p>"
				+ "<p><b>Access Key:</b> " + accessKey + "</p>";
		mailNotification.sendEmail(owner.getName(), owner.getEmail(), ownerSubject, ownerContent);
	}



	public AuditionUserCompanyRoleDTO validateCompanyAccessByFilmHookId(
			Integer userId,
			String filmHookCode,
			String designation,
			String accessCode,
			User loggedUser) {

	    AuditionUserCompanyRole role = roleRepository
	            .findByFilmHookCodeAndDesignationAndAccessKeyIgnoreCaseAndStatusTrueAndDeletedFalse(filmHookCode, designation, accessCode)
	            .orElseThrow(() -> new RuntimeException("No active role found matching FilmHookCode, designation, and access code"));
		if (role.getStatus() == null || !role.getStatus()) {
			throw new RuntimeException("Access revoked or inactive for this company");
		}

		// Owner bypass
		if (role.getOwner() != null && role.getOwner().getUserId().equals(loggedUser.getUserId())) {
			AuditionUserCompanyRoleDTO dto = AuditionCompanyConverter.toDto(role);
			dto.setIsOwner(true);
			return dto;
		}

		// Assigned user validation
		if (!role.getAssignedUser().getUserId().equals(loggedUser.getUserId())) {
			throw new RuntimeException("You don’t have access to this company");
		}

		if (!role.getDesignation().equals(designation)) {
			throw new RuntimeException("Designation mismatch");
		}

		if (!role.getAccessKey().equalsIgnoreCase(accessCode)) {
			throw new RuntimeException("Access code mismatch");
		}

		AuditionUserCompanyRoleDTO dto = AuditionCompanyConverter.toDto(role);
		dto.setIsOwner(false);
		return dto;
	}

	// ✅ Get all companies created by user (PENDING or SUCCESS verification)
	public List<AuditionCompanyDetailsDTO> getAllCompaniesByOwner(Integer ownerId) {
		User owner = userRepository.findById(ownerId)
				.orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));


		List<AuditionCompanyDetails> companies = companyRepository.findByUserAndVerificationStatusIn(
				owner, List.of(AuditionCompanyDetails.VerificationStatus.PENDING, 
						AuditionCompanyDetails.VerificationStatus.SUCCESS)
				);

		return companies.stream()
				.map(company -> {
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

	// ✅ Combined method for login / access
	@Override
	public Object handleAuditionAccess(User loggedUser, String filmHookCode, String designation, String accessCode) {

		// 1️⃣ If filmHookCode, designation, accessCode are provided → validate role/company login
		if (filmHookCode != null && designation != null && accessCode != null) {
			// This will return only the company the user has access to via role or owner
			return validateCompanyAccessByFilmHookId(
					loggedUser.getUserId(),
					filmHookCode,
					designation,
					accessCode,
					loggedUser
					);
		}

		// 2️⃣ Otherwise → fetch all companies owned by the user (PENDING or SUCCESS verification)
		List<AuditionCompanyDetailsDTO> ownedCompanies = getAllCompaniesByOwner(loggedUser.getUserId());

		if (!ownedCompanies.isEmpty()) {
			return ownedCompanies;
		}

		return "No companies found. Please provide FilmHookCode, designation, and access code.";
	}


	@Override
	public AuditionCompanyDetailsDTO getCompanyById(Integer companyId) {
		AuditionCompanyDetails company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

		AuditionCompanyDetailsDTO dto = AuditionCompanyConverter.toCompanyDTO(company);

		// Attach logo files
		List<FileOutputWebModel> logoFiles = mediaFilesService
				.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, company.getId());

		if (!logoFiles.isEmpty()) {
			dto.setLogoFilesOutput(logoFiles);
		}

		return dto;
	}

	@Override
	public void removeAccess(List<Integer> roleIds) {
		  Integer userId = userDetails.userInfo().getId(); // currently logged-in user

		    for (Integer roleId : roleIds) {
		        AuditionUserCompanyRole role = roleRepository.findById(roleId)
		                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

	
		    if (Boolean.TRUE.equals(role.getDeleted())) {
		        throw new RuntimeException("Access is already deleted for this user.");
		    }

		    if (!role.getOwner().getUserId().equals(userId)) {
		        throw new RuntimeException("You are not authorized to delete this access.");
		    }

		role.setStatus(false);
		roleRepository.save(role);
	}}

	@Override
	public void softDeleteCompany(Integer companyId) {
		
		Integer userId = userDetails.userInfo().getId();

		// ✅ Fetch User entity
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		// ✅ Fetch company
		AuditionCompanyDetails company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

		// ✅ Ensure only creator can delete
		if (!company.getUser().getUserId().equals(user.getUserId())) {
			throw new RuntimeException("You are not authorized to delete this company");
		}

		// ✅ Soft delete
	    company.setDeleted(true);
	    company.setUpdatedBy(user.getUserId());
	    company.setUpdatedDate(LocalDateTime.now());

		companyRepository.save(company);
	}
@Override
	public List<AuditionUserCompanyRoleDTO> getAssignedUsersByOwnerAndCompany(Integer ownerId, Integer companyId) {
	    List<AuditionUserCompanyRole> roles =
	            roleRepository.findByOwner_UserIdAndCompany_IdAndDeletedFalse(ownerId, companyId);

	    return roles.stream()
	            .map(role -> AuditionCompanyConverter.toDto(role, userService))
	            .collect(Collectors.toList());
	}

	
	
	@Transactional
	@Override
	public void deleteUserAccess(List<Integer> roleIds) {
		  Integer userId = userDetails.userInfo().getId(); // currently logged-in user

		    for (Integer roleId : roleIds) {
		        AuditionUserCompanyRole role = roleRepository.findById(roleId)
		                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

	
		    if (Boolean.TRUE.equals(role.getDeleted())) {
		        throw new RuntimeException("Access is already deleted for this user.");
		    }

		    if (!role.getOwner().getUserId().equals(userId)) {
		        throw new RuntimeException("You are not authorized to delete this access.");
		    }

	    role.setDeleted(true);      
	    role.setStatus(false);       
	    role.setUpdatedDate(LocalDateTime.now());

	    roleRepository.save(role);
		    }
	}


	 @Override
	    public List<AuditionCompanyDetailsDTO> getCompaniesForLoggedInUser(Integer userId) {
	        List<AuditionCompanyDetails> companies = companyRepository.findCompaniesForUser(userId);

	        return companies.stream().map(company -> {
	            List<FileOutputWebModel> logoFiles =
	                    mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, company.getId());

	            return AuditionCompanyDetailsDTO.builder()
	                    .id(company.getId())
	                    .companyName(company.getCompanyName())
	                    .logoFilesOutput(logoFiles)
	                    .build();
	        }).collect(Collectors.toList());
	    }


} 

