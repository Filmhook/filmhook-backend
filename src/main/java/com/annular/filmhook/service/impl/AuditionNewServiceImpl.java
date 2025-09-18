package com.annular.filmhook.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.exception.ResourceNotFoundException;
import com.annular.filmhook.model.AuditionCartItems;
import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.AuditionCartItemsRepository;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.MovieCategoryRepository;
import com.annular.filmhook.repository.MovieSubCategoryRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionNewService;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.FilmProfessionResponseDTO;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;

import java.util.Objects;


import javax.persistence.EntityNotFoundException;

import javax.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.scheduling.annotation.Scheduled;


import com.annular.filmhook.UserDetails;
import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.AuditionNewTeamNeed;
import com.annular.filmhook.model.AuditionView;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MediaFileCategory;

import com.annular.filmhook.repository.AuditionCompanyRepository;
import com.annular.filmhook.repository.AuditionNewTeamNeedRepository;
import com.annular.filmhook.repository.AuditionProjectRepository;
import com.annular.filmhook.repository.AuditionViewRepository;

import com.annular.filmhook.repository.LikeRepository;

import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
import com.annular.filmhook.webmodel.AuditionNewTeamNeedWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service
public class AuditionNewServiceImpl implements AuditionNewService {

	@Autowired
	private AuditionProjectRepository projectRepository;

	@Autowired
	private AuditionNewTeamNeedRepository teamNeedRepository;
	@Autowired
	private AuditionCompanyRepository companyRepository;

	@Autowired
	MovieCategoryRepository categoryRepo;

	@Autowired
	MovieSubCategoryRepository subCategoryRepo;

	@Autowired
	private FilmSubProfessionRepository filmSubProfessionRepository;

	@Autowired
	S3Util s3Util;
	@Autowired
	private AuditionCartItemsRepository auditionCartItemsRepository;

	@Autowired
	private FilmProfessionRepository filmProfessionRepository;

	@Autowired
	private AuditionViewRepository auditionViewRepository;
	@Autowired
	private  UserDetails userDetails;
	@Autowired
	private  LikeRepository likesRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MediaFilesService mediaFilesService;

	@Override
	public AuditionNewProject createProject(AuditionNewProjectWebModel projectDto) {

		// ✅ Get currently logged-in user's ID
		Integer userId = userDetails.userInfo().getId();

		// ✅ Fetch User entity
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		// ✅ Find the company
		AuditionCompanyDetails company = companyRepository.findById(projectDto.getCompanyId())
				.orElseThrow(() -> new RuntimeException("Company not found with ID: " + projectDto.getCompanyId()));

		// ✅ Convert DTO → Entity (with userId)
		AuditionNewProject project = AuditionCompanyConverter.toEntity(projectDto, company, userId);

		// ✅ Save project
		AuditionNewProject savedProject = projectRepository.save(project);

		// ✅ Handle profile picture upload
		AuditionCompanyConverter.handleProjectProfilePictureFile(projectDto, savedProject, user, mediaFilesService);

		return savedProject;
	}

	@Override
	public List<AuditionNewProjectWebModel> getProjectsBySubProfession(Integer subProfessionId) {
		// ✅ Get currently logged-in user's ID
		Integer userId = userDetails.userInfo().getId();

		// ✅ Fetch User entity
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		List<AuditionNewTeamNeed> teamNeeds = teamNeedRepository.findAllBySubProfessionId(subProfessionId);

		return teamNeeds.stream()
				.filter(teamNeed -> Boolean.TRUE.equals(teamNeed.getStatus()))
				.map(AuditionNewTeamNeed::getProject)
				.distinct()
				.map(project -> {
					// Convert entity → DTO
					AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

					// 🔹 Fetch profile pictures
					List<FileOutputWebModel> profilePictures = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.AuditionProfilePicture, project.getId());
					if (profilePictures != null && !profilePictures.isEmpty()) {
						dto.setProfilePictureFilesOutput(profilePictures);
					}

					// 🔹 Fetch company logo
					AuditionCompanyDetails company = project.getCompany();
					if (company != null) {
						List<FileOutputWebModel> companyLogos = mediaFilesService
								.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, company.getId());
						if (companyLogos != null && !companyLogos.isEmpty()) {
							dto.setLogoFiles(companyLogos);
						}
					}

					// ✅ Active teamNeeds for this subProfessionId
					List<AuditionNewTeamNeedWebModel> activeTeamNeeds = project.getTeamNeeds().stream()
							.filter(tn -> Boolean.TRUE.equals(tn.getStatus()))
							.filter(tn -> tn.getSubProfession() != null
							&& tn.getSubProfession().getSubProfessionId().equals(subProfessionId))
							.map(tn -> {
								AuditionNewTeamNeedWebModel tnDto = AuditionCompanyConverter.toDto(tn);

								// 🔹 Check if current user liked this teamNeed
								boolean liked = likesRepository
										.findByCategoryAndAuditionIdAndLikedBy("TEAM_NEED", tn.getId(), user.getUserId())
										.map(Likes::getStatus)
										.orElse(false);
								tnDto.setLiked(liked);

								// 🔹 Count total likes only
								int totalLikes = likesRepository.countByCategoryAndAuditionIdAndStatus(
										"TEAM_NEED", tn.getId(), true
										);
								tnDto.setLikeCount(totalLikes);

								// 🔹 Count views

								int totalViews = auditionViewRepository.countByTeamNeedId(tn.getId());
								tnDto.setTotalViews(totalViews);

								return tnDto;
							})
							.collect(Collectors.toList());

					dto.setTeamNeeds(activeTeamNeeds);
					return dto;
				})
				.collect(Collectors.toList());
	}


	@Override
	public List<AuditionNewProjectWebModel> getProjectsByCompanyIdAndTeamNeed(Integer companyId, Integer teamNeedId) {
		// ✅ Get current user
		Integer userId = userDetails.userInfo().getId();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		// ✅ Fetch all projects for this company
		List<AuditionNewProject> projects = projectRepository.findAllByCompanyId(companyId);

		// ✅ Fetch company logos (once)
		List<FileOutputWebModel> companyLogos = mediaFilesService
				.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, companyId);

		// ✅ Convert projects to DTOs (but filter only active teamNeeds)
		List<AuditionNewProjectWebModel> projectDtos = projects.stream()
				.map(project -> {
					List<AuditionNewTeamNeed> activeTeamNeeds = project.getTeamNeeds().stream()
							.filter(tn -> Boolean.TRUE.equals(tn.getStatus()))
							.collect(Collectors.toList());

					if (activeTeamNeeds.isEmpty()) {
						return null; // skip project without active teamNeeds
					}

					AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

					// ✅ Convert teamNeeds to DTOs with likes & views
					List<AuditionNewTeamNeedWebModel> teamNeedDtos = activeTeamNeeds.stream()
							.map(tn -> {
								AuditionNewTeamNeedWebModel tnDto = AuditionCompanyConverter.toDto(tn);

								// 🔹 Liked by current user?
								boolean liked = likesRepository
										.findByCategoryAndAuditionIdAndLikedBy("TEAM_NEED", tn.getId(), userId)
										.map(Likes::getStatus)
										.orElse(false);
								tnDto.setLiked(liked);

								// 🔹 Total likes
								int totalLikes = likesRepository.countByCategoryAndAuditionIdAndStatus(
										"TEAM_NEED", tn.getId(), true
										);
								tnDto.setLikeCount(totalLikes);

								// 🔹 Total views
								int totalViews = auditionViewRepository.countByTeamNeedId(tn.getId());
								tnDto.setTotalViews(totalViews);

								return tnDto;
							})
							.collect(Collectors.toList());

					dto.setTeamNeeds(teamNeedDtos);

					// ✅ Add profile picture
					List<FileOutputWebModel> profilePictures = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.AuditionProfilePicture, project.getId());
					if (profilePictures != null && !profilePictures.isEmpty()) {
						dto.setProfilePictureFilesOutput(profilePictures);
					}

					// ✅ Add company logo
					if (companyLogos != null && !companyLogos.isEmpty()) {
						dto.setLogoFiles(companyLogos);
					}

					return dto;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		// ✅ Reorder so that the project containing given teamNeedId comes first
		if (teamNeedId != null) {
			projectDtos.sort((p1, p2) -> {
				boolean p1HasTeamNeed = p1.getTeamNeeds().stream().anyMatch(tn -> tn.getId().equals(teamNeedId));
				boolean p2HasTeamNeed = p2.getTeamNeeds().stream().anyMatch(tn -> tn.getId().equals(teamNeedId));

				if (p1HasTeamNeed && !p2HasTeamNeed) return -1;
				if (!p1HasTeamNeed && p2HasTeamNeed) return 1;
				return 0;
			});
		}

		return projectDtos;
	}

	public List<MovieCategory> getAllCategories() {
		return categoryRepo.findAll();
	}


	public List<MovieSubCategory> getSubCategories(Integer categoryId) {
		return subCategoryRepo.findByCategoryId(categoryId);
	}

	@Override
	public List<FilmSubProfessionResponseDTO> getAllSubProfessions() {
	    List<Integer> excludedIds = Arrays.asList(1); // exclude Producer, Director, etc.
	    return filmSubProfessionRepository.findByProfession_FilmProfessionIdNotIn(excludedIds)
	            .stream()
	            .map(this::mapToDTO)
	            .collect(Collectors.toList());
	}


	@Override
	public List<FilmSubProfessionResponseDTO> getSubProfessionsByProfessionId(Integer professionId) {
		FilmProfession profession = new FilmProfession();
		profession.setFilmProfessionId(professionId);

		return filmSubProfessionRepository.findByProfession(profession)
				.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	private FilmSubProfessionResponseDTO mapToDTO(FilmSubProfession sub) {

		List<AuditionNewTeamNeed> activeNeeds = teamNeedRepository.findActiveBySubProfessionId(sub.getSubProfessionId());

		return FilmSubProfessionResponseDTO.builder()
				.subProfessionId(sub.getSubProfessionId())
				.subProfessionName(sub.getSubProfessionName())
				.professionName(sub.getProfession().getProfessionName())
				.filmProfessionId(sub.getProfession().getFilmProfessionId())
				.iconFilePath(  !Utility.isNullOrBlankWithTrim(sub.getProfession().getFilePath()) 
						? s3Util.generateS3FilePath(sub.getProfession().getFilePath()) 
								: "")
				.shortCharacters(generateShortCharacters(sub.getProfession().getProfessionName()))
				.count(activeNeeds.size()) 
				.build();
	}

	private String generateShortCharacters(String professionName) {
		if (professionName == null || professionName.isEmpty()) return "";
		return professionName.length() <= 3
				? professionName.toUpperCase()
						: professionName.substring(0, 3).toUpperCase();
	}

	@Override
	public void addToCart(Integer userId, Integer companyId, Integer subProfessionId, Integer count) {
	    // ✅ Check SubProfession
	    FilmSubProfession subProfession = filmSubProfessionRepository.findById(subProfessionId)
	            .orElseThrow(() -> new ResourceNotFoundException("SubProfession not found with id: " + subProfessionId));

	    // ✅ Check User
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		// ✅ Find the company
		AuditionCompanyDetails company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

	    // ✅ Check existing cart item
	    AuditionCartItems existingItem = auditionCartItemsRepository
	            .findByUserAndCompanyIdAndSubProfession(user, companyId, subProfession)
	            .orElse(null);

	    if (existingItem != null) {
	        existingItem.setCount(count);
	        auditionCartItemsRepository.save(existingItem);
	    } else {
	        AuditionCartItems cartItem = AuditionCartItems.builder()
	                .user(user)
	                .companyId(company.getId()) // safer to set from entity
	                .subProfession(subProfession)
	                .count(count)
	                .build();
	        auditionCartItemsRepository.save(cartItem);
	    }
	}




	@Override
	public List<FilmSubProfessionResponseDTO> getCart(Integer userId, Integer companyId) {
		// ✅ Check user from DB
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		// ✅ Fetch cart items for user + company
		List<AuditionCartItems> cartItems = auditionCartItemsRepository.findByUserAndCompanyId(user, companyId);

		// (Optional) handle empty cart gracefully
		if (cartItems.isEmpty()) {
			throw new ResourceNotFoundException("No cart items found for user " + userId + " and company " + companyId);
		}

		return cartItems.stream()
				.map(item -> FilmSubProfessionResponseDTO.builder()
						.subProfessionId(item.getSubProfession().getSubProfessionId())
						.subProfessionName(item.getSubProfession().getSubProfessionName())
						.professionName(item.getSubProfession().getProfession().getProfessionName())
						.filmProfessionId(item.getSubProfession().getProfession().getFilmProfessionId())
						.iconFilePath(
								!Utility.isNullOrBlankWithTrim(item.getSubProfession().getProfession().getFilePath())
								? s3Util.generateS3FilePath(item.getSubProfession().getProfession().getFilePath())
										: ""
								)
						.shortCharacters(generateShortCharacters(item.getSubProfession().getProfession().getProfessionName()))
						.count(item.getCount())
						.build()
						)
				.collect(Collectors.toList());
	}

	@Override
	public List<FilmProfessionResponseDTO> getAllProfessions() {
		List<FilmProfession> professions = filmProfessionRepository.findAll(); // make type explicit

		return professions.stream()
				.map((FilmProfession profession) -> {
					List<AuditionNewTeamNeed> activeNeeds =
							teamNeedRepository.findActiveByProfessionId(profession.getFilmProfessionId());

					Long activeCount = (activeNeeds != null) ? (long) activeNeeds.size() : 0;

					return FilmProfessionResponseDTO.builder()
							.id(profession.getFilmProfessionId())
							.professionName(profession.getProfessionName())
							.iconFilePath(
									!Utility.isNullOrBlankWithTrim(profession.getFilePath())
									? s3Util.generateS3FilePath(profession.getFilePath())
											: ""
									)
							.count(activeCount)
							.build();
				})
				.collect(Collectors.toList());
	}

	@Scheduled(cron = "0 42 17 * * *") // At 00:00 daily
	@Transactional
	public void deactivateExpiredTeamNeeds() {
		LocalDate today = LocalDate.now();

		// Fetch all expired team needs
		List<AuditionNewTeamNeed> expiredTeamNeeds = teamNeedRepository.findExpiredTeamNeeds(today);

		if (!expiredTeamNeeds.isEmpty()) {
			expiredTeamNeeds.forEach(tn -> {
				tn.setStatus(false); // deactivate
				tn.setUpdatedDate(LocalDateTime.now());
			});

			teamNeedRepository.saveAll(expiredTeamNeeds);

			System.out.println("✅ Deactivated " + expiredTeamNeeds.size() + " expired team needs at midnight");
		}
	}
	@Override
	public String toggleTeamNeedLike(Integer teamNeedId, Integer userId) {
		// ✅ Validate inputs
		if (teamNeedId == null || userId == null) {
			throw new IllegalArgumentException("teamNeedId and userId must not be null");
		}

		// ✅ Fetch teamNeed and check status
		AuditionNewTeamNeed teamNeed = teamNeedRepository.findById(teamNeedId)
				.orElseThrow(() -> new EntityNotFoundException("TeamNeed not found with ID: " + teamNeedId));

		if (!Boolean.TRUE.equals(teamNeed.getStatus())) {
			throw new IllegalStateException("Cannot like. TeamNeed is not active.");
		}

		// ✅ Ensure user exists
		if (!userRepository.existsById(userId)) {
			throw new EntityNotFoundException("User not found with ID: " + userId);
		}

		try {
			return likesRepository.findByCategoryAndAuditionIdAndLikedBy("TEAM_NEED", teamNeedId, userId)
					.map(existing -> {
						existing.setStatus(!existing.getStatus());
						existing.setReactionType(existing.getStatus() ? "LIKE" : "UNLIKE");
						likesRepository.save(existing);
						return existing.getStatus() ? "Liked" : "Unliked";
					})
					.orElseGet(() -> {
						Likes newLike = Likes.builder()
								.category("TEAM_NEED")
								.auditionId(teamNeedId)
								.likedBy(userId)
								.status(true)
								.reactionType("LIKE")
								.createdBy(userId)
								.build();
						likesRepository.save(newLike);
						return "Liked";
					});
		} catch (DataIntegrityViolationException ex) {
			throw new RuntimeException("Failed to save like due to database constraint violation", ex);
		} catch (Exception ex) {
			throw new RuntimeException("Unexpected error while toggling like", ex);
		}
	}
	@Override
	public void addView(Integer teamNeedId, Integer userId) {
		boolean alreadyViewed = auditionViewRepository.existsByTeamNeedId_IdAndUser_UserId(teamNeedId, userId);
		if (alreadyViewed) {
			return; // Prevent duplicate views from same user
		}

		AuditionNewTeamNeed teamNeed = teamNeedRepository.findById(teamNeedId)
				.orElseThrow(() -> new RuntimeException("Audition not found"));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		AuditionView view = AuditionView.builder()
				.teamNeedId(teamNeed)
				.user(user)
				.ViewedOn(LocalDateTime.now())
				.build();

		auditionViewRepository.save(view);
	}
	@Override
	public int getViewCount(Integer teamNeedId) {
		return auditionViewRepository.countByTeamNeedId(teamNeedId);
	}
}
