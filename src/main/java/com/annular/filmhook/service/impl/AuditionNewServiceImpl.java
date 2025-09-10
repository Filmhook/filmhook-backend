package com.annular.filmhook.service.impl;

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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.AuditionNewTeamNeed;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.AuditionCompanyRepository;
import com.annular.filmhook.repository.AuditionNewTeamNeedRepository;
import com.annular.filmhook.repository.AuditionProjectRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionNewService;
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
	private  UserDetails userDetails;

	@Autowired
	private MediaFilesService mediaFilesService;

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
	UserRepository userRepository;

	@Autowired
	private FilmProfessionRepository filmProfessionRepository;


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
		List<AuditionNewTeamNeed> teamNeeds = teamNeedRepository.findAllBySubProfessionId(subProfessionId);

		return teamNeeds.stream()
				.filter(teamNeed -> Boolean.TRUE.equals(teamNeed.getStatus()))
				.map(AuditionNewTeamNeed::getProject)
				.distinct()
				.map(project -> {
					// Convert entity → DTO
					AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

					// 🔹 Fetch profile pictures for this project
					List<FileOutputWebModel> profilePictures = mediaFilesService
							.getMediaFilesByCategoryAndRefId(
									MediaFileCategory.AuditionProfilePicture,
									project.getId()
									);
					if (profilePictures != null && !profilePictures.isEmpty()) {
						dto.setProfilePictureFilesOutput(profilePictures);
					}

					// 🔹 Fetch company logo for this project’s company
					AuditionCompanyDetails company = project.getCompany();
					if (company != null) {
						List<FileOutputWebModel> companyLogos = mediaFilesService
								.getMediaFilesByCategoryAndRefId(
										MediaFileCategory.Audition,
										company.getId()
										);
						if (companyLogos != null && !companyLogos.isEmpty()) {
							dto.setLogoFiles(companyLogos);
						}
					}

					// ✅ Only include active teamNeeds for this subProfessionId
					List<AuditionNewTeamNeedWebModel> activeTeamNeeds = project.getTeamNeeds().stream()
							.filter(tn -> Boolean.TRUE.equals(tn.getStatus()))
							.filter(tn -> tn.getSubProfession() != null
							&& tn.getSubProfession().getSubProfessionId().equals(subProfessionId))
							.map(AuditionCompanyConverter::toDto)
							.collect(Collectors.toList());

					dto.setTeamNeeds(activeTeamNeeds);

					return dto;
				})
				.collect(Collectors.toList());
	}


	@Override
	public List<AuditionNewProjectWebModel> getProjectsByCompanyId(Integer companyId) {
		// Fetch projects belonging to this company
		List<AuditionNewProject> projects = projectRepository.findAllByCompanyId(companyId);

		// 🔹 Fetch company logo 
		List<FileOutputWebModel> companyLogos = mediaFilesService
				.getMediaFilesByCategoryAndRefId(
						MediaFileCategory.Audition, 
						companyId
						);

		return projects.stream()
				.map(project -> {
					// Fetch teamNeeds for this project where status = true
					List<AuditionNewTeamNeed> activeTeamNeeds = teamNeedRepository.findAllByProjectId(project.getId())
							.stream()
							.filter(teamNeed -> Boolean.TRUE.equals(teamNeed.getStatus()))
							.collect(Collectors.toList());

					// If no active teamNeeds → skip this project
					if (activeTeamNeeds.isEmpty()) {
						return null;
					}

					// Convert entity → DTO
					AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

					// Set only active teamNeeds
					dto.setTeamNeeds(
							activeTeamNeeds.stream()
							.map(AuditionCompanyConverter::toDto)
							.collect(Collectors.toList())
							);

					// 🔹 Set profile pictures for project
					List<FileOutputWebModel> profilePictures = mediaFilesService
							.getMediaFilesByCategoryAndRefId(
									MediaFileCategory.AuditionProfilePicture,
									project.getId()
									);

					if (profilePictures != null && !profilePictures.isEmpty()) {
						dto.setProfilePictureFilesOutput(profilePictures);
					}

					// 🔹 Attach company logo (same for all projects in this company)
					if (companyLogos != null && !companyLogos.isEmpty()) {
						dto.setLogoFiles(companyLogos); // 👈 add this field in DTO
					}

					return dto;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}



	public List<MovieCategory> getAllCategories() {
		return categoryRepo.findAll();
	}


	public List<MovieSubCategory> getSubCategories(Integer categoryId) {
		return subCategoryRepo.findByCategoryId(categoryId);
	}

	@Override
	public List<FilmSubProfessionResponseDTO> getAllSubProfessions() {
		return filmSubProfessionRepository.findAll()
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
				.id(sub.getSubProfessionId())
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
		FilmSubProfession subProfession = filmSubProfessionRepository.findById(subProfessionId)
				.orElseThrow(() -> new ResourceNotFoundException("SubProfession not found with id: " + subProfessionId));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		AuditionCartItems existingItem = auditionCartItemsRepository
				.findByUserAndCompanyIdAndSubProfession(user, companyId, subProfession)
				.orElse(null);

		if (existingItem != null) {
			existingItem.setCount(existingItem.getCount() + count);
			auditionCartItemsRepository.save(existingItem);
		} else {
			AuditionCartItems cartItem = AuditionCartItems.builder()
					.user(user)
					.companyId(companyId)
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
						.id(item.getSubProfession().getSubProfessionId())
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








}
