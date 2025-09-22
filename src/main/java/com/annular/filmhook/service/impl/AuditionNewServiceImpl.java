package com.annular.filmhook.service.impl;

import java.io.InputStream;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Date;

import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.exception.ResourceNotFoundException;
import com.annular.filmhook.model.AuditionCartItems;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.AuditionNewTeamNeed;
import com.annular.filmhook.model.AuditionPayment;
import com.annular.filmhook.model.AuditionView;
import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.model.PricingConfig;
import com.annular.filmhook.model.ServiceType;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserOffer;
import com.annular.filmhook.repository.AuditionCartItemsRepository;
import com.annular.filmhook.repository.AuditionCompanyRepository;
import com.annular.filmhook.repository.AuditionNewTeamNeedRepository;
import com.annular.filmhook.repository.AuditionPaymentRepository;
import com.annular.filmhook.repository.AuditionProjectRepository;
import com.annular.filmhook.repository.AuditionViewRepository;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.MovieCategoryRepository;
import com.annular.filmhook.repository.MovieSubCategoryRepository;
import com.annular.filmhook.repository.PricingConfigRepository;
import com.annular.filmhook.repository.UserOfferRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionNewService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
import com.annular.filmhook.webmodel.AuditionNewTeamNeedWebModel;
import com.annular.filmhook.webmodel.AuditionPaymentDTO;
import com.annular.filmhook.webmodel.AuditionPaymentWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.FilmProfessionResponseDTO;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;
import org.springframework.beans.factory.annotation.Value;



import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.DeviceRgb;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.time.ZonedDateTime;
import java.time.ZoneId;
@Service
public class AuditionNewServiceImpl implements AuditionNewService {
	private static final Logger logger = LoggerFactory.getLogger(AuditionNewServiceImpl.class);
	@Autowired
	private AuditionProjectRepository projectRepository;

	@Autowired
	private AuditionNewTeamNeedRepository teamNeedRepository;
	@Autowired
	private AuditionCompanyRepository companyRepository;

	@Autowired
	MovieCategoryRepository categoryRepo;

	@Autowired
	InAppNotificationRepository inAppNotificationRepository;

	@Autowired
	MovieSubCategoryRepository subCategoryRepo;
	@Autowired
	PricingConfigRepository pricingConfigRepository;

	@Autowired
	private FilmSubProfessionRepository filmSubProfessionRepository;

	@Autowired
	private AuditionPaymentRepository paymentRepository;

	@Autowired
	S3Util s3Util;
	@Autowired
	private AuditionCartItemsRepository auditionCartItemsRepository;

	@Autowired
	private FilmProfessionRepository filmProfessionRepository;
	@Autowired
	UserOfferRepository userOfferRepository;

	@Autowired
	private AuditionViewRepository auditionViewRepository;
	@Autowired
	private  UserDetails userDetails;
	@Autowired
	private  LikeRepository likesRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MailNotification mailNotification;
	@Autowired
	private MediaFilesService mediaFilesService;

	@Value("${payu.key}")
	private String key;

	@Value("${payu.salt}")
	private String salt;
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
		Integer userId = userDetails.userInfo().getId();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		List<AuditionNewTeamNeed> teamNeeds = teamNeedRepository.findAllBySubProfessionId(subProfessionId);

		return teamNeeds.stream()
				// ✅ Only include teamNeeds where status = true
				.filter(teamNeed -> Boolean.TRUE.equals(teamNeed.getStatus()))
				// ✅ Project must be active
				.filter(teamNeed -> teamNeed.getProject() != null && Boolean.TRUE.equals(teamNeed.getProject().getStatus()))
				.map(AuditionNewTeamNeed::getProject)
				.distinct()
				.map(project -> {
					// 🔹 Convert entity → DTO
					AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

					if (user.getFilmHookCode() != null) {
						dto.setFilmHookCode(user.getFilmHookCode());
					}

					// ✅ Attach project expiry date
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

					Optional<AuditionPayment> paymentOpt = paymentRepository
							.findTopByProjectIdOrderByExpiryDateTimeDesc(project.getId());

					paymentOpt.ifPresent(payment -> {
						LocalDateTime expiry = payment.getExpiryDateTime();
						if (expiry != null) {
							dto.setExpiryDate(expiry.format(dateFormatter));
							dto.setExpiryTime(expiry.format(timeFormatter));
						}
					});

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

					// ✅ Only active teamNeeds for this project + subProfession
					List<AuditionNewTeamNeedWebModel> activeTeamNeeds = project.getTeamNeeds().stream()
							.filter(tn -> Boolean.TRUE.equals(tn.getStatus()))
							.filter(tn -> tn.getSubProfession() != null
							&& tn.getSubProfession().getSubProfessionId().equals(subProfessionId))
							.map(tn -> {
								AuditionNewTeamNeedWebModel tnDto = AuditionCompanyConverter.toDto(tn);


								// 🔹 Check if user liked this teamNeed
								boolean liked = likesRepository
										.findByCategoryAndAuditionIdAndLikedBy("TEAM_NEED", tn.getId(), user.getUserId())
										.map(Likes::getStatus)
										.orElse(false);
								tnDto.setLiked(liked);

								// 🔹 Count total likes
								int totalLikes = likesRepository.countByCategoryAndAuditionIdAndStatus(
										"TEAM_NEED", tn.getId(), true);
								tnDto.setLikeCount(totalLikes);

								// 🔹 Count views
								int totalViews = auditionViewRepository.countByTeamNeedId(tn.getId());
								tnDto.setTotalViews(totalViews);

								return tnDto;
							})
							.collect(Collectors.toList());

					// ❌ Skip this project if it has no active teamNeeds
					if (activeTeamNeeds.isEmpty()) {
						return null;
					}

					dto.setTeamNeeds(activeTeamNeeds);
					return dto;
				})
				// Remove nulls (projects without active posts)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}


	@Override
	public List<AuditionNewProjectWebModel> getProjectsByCompanyIdAndTeamNeed(Integer companyId, Integer teamNeedId,Integer professionId) {

		Integer userId = userDetails.userInfo().getId();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


		List<AuditionNewProject> projects = projectRepository.findAllByCompanyId(companyId);

		// ✅ Fetch company logos (once)
		List<FileOutputWebModel> companyLogos = mediaFilesService
				.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, companyId);

		// ✅ Convert projects to DTOs
		List<AuditionNewProjectWebModel> projectDtos = projects.stream()

				.filter(project -> Boolean.TRUE.equals(project.getStatus()))
				.map(project -> {

					List<AuditionNewTeamNeed> activeTeamNeeds = project.getTeamNeeds().stream()
							.filter(tn -> Boolean.TRUE.equals(tn.getStatus()))
							.filter(tn -> tn.getProfession().getFilmProfessionId().equals(professionId))
							.collect(Collectors.toList());


					if (activeTeamNeeds.isEmpty()) {
						return null;
					}

					AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

					if (user.getFilmHookCode() != null) {
						dto.setFilmHookCode(user.getFilmHookCode());
					}
					// ✅ Attach project expiry date



					Optional<AuditionPayment> paymentOpt = paymentRepository
							.findTopByProjectIdOrderByExpiryDateTimeDesc(project.getId());


					paymentOpt.ifPresent(payment -> {
						LocalDateTime expiry = payment.getExpiryDateTime();
						if (expiry != null) {
							ZonedDateTime istTime = expiry.atZone(ZoneId.of("UTC"))
									.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
							dto.setExpiryTime(istTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
							dto.setExpiryDate(istTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
						}
					});



					//  Convert teamNeeds to DTOs with likes & views
					List<AuditionNewTeamNeedWebModel> teamNeedDtos = activeTeamNeeds.stream()
							.map(tn -> {
								AuditionNewTeamNeedWebModel tnDto = AuditionCompanyConverter.toDto(tn);


								boolean liked = likesRepository
										.findByCategoryAndAuditionIdAndLikedBy("TEAM_NEED", tn.getId(), userId)
										.map(Likes::getStatus)
										.orElse(false);
								tnDto.setLiked(liked);


								int totalLikes = likesRepository.countByCategoryAndAuditionIdAndStatus(
										"TEAM_NEED", tn.getId(), true
										);
								tnDto.setLikeCount(totalLikes);


								int totalViews = auditionViewRepository.countByTeamNeedId(tn.getId());
								tnDto.setTotalViews(totalViews);
								Optional<FilmProfession> professionOpt = filmProfessionRepository.findById(tn.getProfession().getFilmProfessionId());
								Optional<FilmSubProfession> subProfessionOpt = filmSubProfessionRepository.findById(tn.getSubProfession().getSubProfessionId());

								String professionName = professionOpt.map(FilmProfession::getProfessionName)
										.orElse("Unknown Profession");

								String subProfessionName = subProfessionOpt.map(FilmSubProfession::getSubProfessionName)
										.orElse("Unknown SubProfession");

								tnDto.setProfessionName(professionName);
								tnDto.setSubProfessionName(subProfessionName);

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


					if (companyLogos != null && !companyLogos.isEmpty()) {
						dto.setLogoFiles(companyLogos);
					}

					return dto;
				})
				//  Remove skipped projects
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

			// 🔹 Also reorder teamNeeds inside each project
			projectDtos.forEach(project -> {
				project.setTeamNeeds(
						project.getTeamNeeds().stream()
						.sorted((tn1, tn2) -> {
							if (tn1.getId().equals(teamNeedId)) return -1;
							if (tn2.getId().equals(teamNeedId)) return 1;
							return 0;
						})
						.collect(Collectors.toList())
						);
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

		List<AuditionNewTeamNeed> activeNeeds = teamNeedRepository.findActiveBySubProfessionIdAndProjectStatus(sub.getSubProfessionId());

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
					.companyId(company.getId()) 
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
		// ✅ Find the company
		AuditionCompanyDetails company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

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
		List<Integer> excludedIds = Arrays.asList(1); // exclude Producer, Director, etc.
		List<FilmProfession> professions = filmProfessionRepository.findByFilmProfessionIdNotIn(excludedIds); 

		return professions.stream()
				.map((FilmProfession profession) -> {
					List<AuditionNewTeamNeed> activeNeeds =
							teamNeedRepository.findActiveByProfessionIdAndProjectStatus(profession.getFilmProfessionId());

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
						existing.setReactionType(existing.getStatus() ? "like" : "unlike");
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

	@Override
	public AuditionPayment createPayment(AuditionPaymentWebModel webModel) {
		// 1️⃣ Fetch project
		AuditionNewProject project = projectRepository.findById(webModel.getProjectId())
				.orElseThrow(() -> new RuntimeException("Project not found"));

		// 2️⃣ Fetch user
		User user = userRepository.findById(webModel.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		// 3️⃣ Generate txnid if not passed
		if (webModel.getTxnid() == null || webModel.getTxnid().isEmpty()) {
			String txnid;
			do {
				txnid = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
			} while (paymentRepository.existsByTxnid(txnid)); // Ensure unique
			webModel.setTxnid(txnid);
		} else if (paymentRepository.existsByTxnid(webModel.getTxnid())) {
			throw new IllegalArgumentException("Duplicate transaction ID: " + webModel.getTxnid());
		}

		// 4️⃣ Convert to entity
		AuditionPayment payment = AuditionCompanyConverter.toEntity(webModel, project, user);
		String amountStr = String.format("%.2f", payment.getTotalAmount());
		// 5️⃣ Generate payment hash
		String hash = HashGenerator.generateHash(
				key,
				payment.getTxnid(),
				amountStr,
				project.getProjectTitle().trim(),
				user.getFirstName().trim(),
				user.getEmail().trim(),
				salt
				);
		payment.setPaymentHash(hash);

		// 6️⃣ Save payment
		return paymentRepository.save(payment);
	}
	@Override
	public ResponseEntity<?> paymentSuccess(String txnid) {
		try {
			// 1️⃣ Fetch payment
			AuditionPayment payment = paymentRepository.findByTxnid(txnid)
					.orElseThrow(() -> new RuntimeException("Payment not found"));

			// 2️⃣ Update payment status
			payment.setPaymentStatus("SUCCESS");
			LocalDateTime now = LocalDateTime.now();
			payment.setSuccessDateTime(now);
			if (payment.getSelectedDays() != null) {
				payment.setExpiryDateTime(now.plusDays(payment.getSelectedDays()));
			}

			// 3️⃣ Update project status
			AuditionNewProject project = payment.getProject();
			project.setStatus(true); 
			projectRepository.save(project);

			// 4️⃣ Save payment
			paymentRepository.save(payment);

			// 5️⃣ Generate PDF invoice
			byte[] invoicePdf = generateAuditionInvoicePdf(payment);

			// 6️⃣ Send email with PDF
			String subject = "Audition Payment Successful!";
			String content = "<html><body style='font-family:Verdana;font-size:12px;'>"
					+ "<h2>Payment Successful!</h2>"
					+ "<p>Dear <b>" + payment.getUser().getName() + "</b>,</p>"
					+ "<p>Your payment for the project <b>'" + project.getProjectTitle() + "'</b> has been received successfully.</p>"
					+ "<h3>Payment & Project Details:</h3>"
					+ "<table cellpadding='5' cellspacing='0' border='1' style='border-collapse:collapse;'>"
					+ "<tr><td><b>Transaction ID</b></td><td>" + payment.getTxnid() + "</td></tr>"
					+ "<tr><td><b>Total Amount Paid</b></td><td>₹ " + String.format("%.2f", payment.getTotalAmount()) + "</td></tr>"
					+ "<tr><td><b>Selected Days</b></td><td>" + (payment.getSelectedDays() != null ? payment.getSelectedDays() : "N/A") + "</td></tr>"
					+ "<tr><td><b>Expiry Date</b></td><td>" + (payment.getExpiryDateTime() != null ? payment.getExpiryDateTime().toLocalDate() : "N/A") + "</td></tr>"
					+ "<tr><td><b>Team Needs</b></td><td>" + payment.getTotalTeamNeeds() + "</td></tr>"
					+ "<tr><td><b>Payment Status</b></td><td>" + payment.getPaymentStatus() + "</td></tr>"
					+ "</table>"
					+ "<p>Please keep this information for your records.</p>"
					+ "</body></html>";

			mailNotification.sendEmailWithAttachment(
					payment.getUser().getName(),
					payment.getUser().getEmail(),
					subject,
					content,
					invoicePdf,
					"AuditionInvoice.pdf"
					);


			// 7️⃣ Send in-app notification
			sendInAppNotification(payment,
					"Audition Payment Successful",
					"Your audition payment was successful!");

			// 8️⃣ Return response
			AuditionPaymentWebModel responseWebModel = AuditionCompanyConverter.toWebModel(payment);
			return ResponseEntity.ok(new Response(1, "Payment successful", responseWebModel));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new Response(-1, e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Something went wrong: " + e.getMessage(), null));
		}
	}

	// --- In-App Notification ---
	private void sendInAppNotification(AuditionPayment payment, String title, String message)  {
		User user = payment.getUser();

		InAppNotification notification = InAppNotification.builder()
				.senderId(0)
				.receiverId(user.getUserId())
				.title(title)
				.message(message)
				.createdOn(new Date())
				.isRead(false)
				.adminReview(user.getAdminReview())
				.Profession(user.getUserType())
				.isDeleted(false)
				.createdBy(user.getUserId())
				.userType("Payment")
				.build();

		inAppNotificationRepository.save(notification);
	}

	// --- Generate PDF Invoice ---
	private byte[] generateAuditionInvoicePdf(AuditionPayment payment) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);
			pdf.setDefaultPageSize(PageSize.A4);
			Document doc = new Document(pdf);
			doc.setMargins(36, 36, 36, 36);

			DeviceRgb blue = new DeviceRgb(41, 86, 184);
			DeviceRgb gray = new DeviceRgb(200, 200, 200);

			// Logo
			InputStream logoStream = new URL("https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png").openStream();
			Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()))
					.scaleToFit(120, 60)
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			doc.add(logo);

			// Title
			doc.add(new Paragraph("TAX INVOICE")
					.setTextAlignment(TextAlignment.CENTER)
					.setFontSize(18)
					.setBold()
					.setFontColor(blue)
					.setMarginBottom(10));

			// Issuer & Invoice Info Table
			Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
					.setWidth(UnitValue.createPercentValue(100))
					.setMarginTop(10);

			infoTable.addCell(getPlainCell("Invoice To:\n" + payment.getUser().getName() + "\n" + payment.getUser().getEmail()));
			infoTable.addCell(getPlainCell("Issued by: FilmHook Pvt. Ltd.\nGSTIN: 29ABCDE1234F2Z5\nBangalore\n+91-9876543210"));

			infoTable.addCell(getPlainCell("Transaction ID: " + payment.getTxnid()));
			infoTable.addCell(getPlainCell("Invoice Date: " + LocalDate.now()));

			infoTable.addCell(getPlainCell("Service: Audition Project\n" + payment.getProject().getProjectTitle()));
			infoTable.addCell(getPlainCell("Team Needs: " + payment.getTotalTeamNeeds() +
					"\nSelected Days: " + (payment.getSelectedDays() != null ? payment.getSelectedDays() : "N/A") +
					"\nExpiry Date: " + (payment.getExpiryDateTime() != null ? payment.getExpiryDateTime().toLocalDate() : "N/A")));

			doc.add(infoTable);

			// Divider
			doc.add(new Paragraph("\n").setFontColor(gray));

			// Amount Table
			Table amountTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
					.setWidth(UnitValue.createPercentValue(100))
					.setMarginTop(20);

			amountTable.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
			amountTable.addHeaderCell(new Cell().add(new Paragraph("Amount (₹)").setBold()).setTextAlignment(TextAlignment.RIGHT));

			amountTable.addCell(new Cell().add(new Paragraph("Audition Project Fee")));
			amountTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", payment.getTotalAmount()))).setTextAlignment(TextAlignment.RIGHT));

			// Total Row
			amountTable.addCell(new Cell().add(new Paragraph("Total").setBold()));
			amountTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", payment.getTotalAmount())).setBold())
					.setTextAlignment(TextAlignment.RIGHT));

			doc.add(amountTable);

			// Payment Status & Notes
			doc.add(new Paragraph("\nPayment Status: " + payment.getPaymentStatus())
					.setBold()
					.setMarginTop(15));

			doc.add(new Paragraph("Notes:")
					.setBold()
					.setMarginTop(10));
			doc.add(new Paragraph("1. This invoice is generated automatically by FilmHook.\n" +
					"2. Please contact support for any issues regarding payment or service.\n" +
					"3. Keep this invoice for your records."));

			// Footer
			doc.add(new Paragraph("\nFilmHook Pvt. Ltd., Bangalore - www.filmhook.com")
					.setTextAlignment(TextAlignment.CENTER)
					.setFontSize(10)
					.setFontColor(gray)
					.setMarginTop(50));

			doc.close();
			return baos.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to generate audition invoice PDF", e);
		}
	}

	// --- Helper Cell ---
	private Cell getPlainCell(String text) {
		return new Cell().add(new Paragraph(text).setFontSize(10)).setPadding(5);
	}


	@Override
	public ResponseEntity<?> paymentFailure(String txnid, String errorMessage) {
		try {
			// 1️⃣ Fetch payment
			AuditionPayment payment = paymentRepository.findByTxnid(txnid)
					.orElseThrow(() -> new RuntimeException("Payment not found"));

			// 2️⃣ Update payment status
			payment.setPaymentStatus("FAILED");
			payment.setCreatedOn(LocalDateTime.now());
			payment.setReason(errorMessage);

			// 3️⃣ Save payment
			paymentRepository.save(payment);

			// 4️⃣ Send email notification
			String subject = "Audition Payment Failed!";
			String content = "<html><body style='font-family:Verdana;font-size:12px;'>"
					+ "<p>We regret to inform you that your payment for the project <b>'"
					+ payment.getProject().getProjectTitle() + "'</b> could not be processed.</p>"
					+ "<h3>Details:</h3>"
					+ "<table cellpadding='5' cellspacing='0' border='1' style='border-collapse:collapse;'>"
					+ "<tr><td><b>Transaction ID</b></td><td>" + payment.getTxnid() + "</td></tr>"
					+ "<tr><td><b>Total Amount</b></td><td>₹ " + String.format("%.2f", payment.getTotalAmount()) + "</td></tr>"
					+ "<tr><td><b>Status</b></td><td>FAILED ❌</td></tr>"
					+ "<tr><td><b>Reason</b></td><td>" + errorMessage + "</td></tr>"
					+ "</table>"
					+ "<p>You may retry the payment by clicking the link below:</p>"
					+ "<p><a href='https://filmhookapps.com/retry-payment/" + payment.getTxnid() + "' "
					+ "style='background:#007bff;color:#fff;padding:8px 12px;text-decoration:none;"
					+ "border-radius:4px;'>🔄 Retry Payment</a></p>"
					+ "<p>If the amount was deducted, it will be refunded by your bank.</p>"
					+ "</body></html>";

			mailNotification.sendEmail(
					payment.getUser().getName(),
					payment.getUser().getEmail(),
					subject,
					content
					);

			// 5️⃣ Send in-app notification
			sendInAppNotification(payment,
					"Audition Payment Failed",
					"Your audition payment has failed. Reason: " + errorMessage);

			// 6️⃣ Return response
			AuditionPaymentWebModel responseWebModel = AuditionCompanyConverter.toWebModel(payment);
			return ResponseEntity.ok(new Response(-1, "Payment failed", responseWebModel));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new Response(-1, e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Something went wrong: " + e.getMessage(), null));
		}
	}

	@Override
	public ResponseEntity<?> getPaymentByTxnid(String txnid) {
		try {
			// Fetch payment
			AuditionPayment payment = paymentRepository.findByTxnid(txnid)
					.orElseThrow(() -> new RuntimeException("Payment not found"));

			// Convert to WebModel / DTO
			AuditionPaymentWebModel responseWebModel = AuditionCompanyConverter.toWebModel(payment);

			//  Return success response
			return ResponseEntity.ok(new Response(1, "Payment details fetched successfully", responseWebModel));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new Response(-1, e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Something went wrong: " + e.getMessage(), null));
		}
	}

	@Override
	public AuditionPaymentDTO calculateAuditionPayment(Integer projectId, Integer userId, Integer selectedDays) {
		// Fetch pricing config
		PricingConfig config = pricingConfigRepository.findActiveConfigByService(ServiceType.AUDITION)
				.orElseThrow(() -> new RuntimeException("No pricing config found for AUDITION"));

		double baseRate = config.getBaseRate() != null ? config.getBaseRate() : 0.0;
		int gstPercentage = config.getGstPercentage() != null ? config.getGstPercentage() : 0;

		// Fetch project
		AuditionNewProject project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		int totalTeamNeed = project.getTeamNeeds().stream()
				.mapToInt(AuditionNewTeamNeed::getCount)
				.sum();

		//Offer-related fields (
		Double finalRatePerPost = null;
		Double discountedAmount = null;
		Double discountPercentage = null;

		// Apply user-specific offer
		Optional<UserOffer> offerOpt = userOfferRepository
				.findFirstByUserIdAndServiceTypeAndActiveIsTrueAndValidTillAfterOrderByValidTillDesc(
						userId, ServiceType.AUDITION, LocalDateTime.now());

		if (offerOpt.isPresent()) {
			UserOffer offer = offerOpt.get();
			finalRatePerPost = baseRate;

			// Custom rate (if defined)
			if (offer.getCustomRate() != null) {
				finalRatePerPost = offer.getCustomRate();
			}

			// Discount 
			if (offer.getDiscountPercentage() != null) {
				discountPercentage = offer.getDiscountPercentage();
				finalRatePerPost = finalRatePerPost - (finalRatePerPost * (discountPercentage / 100.0));
			}

			discountedAmount = totalTeamNeed * selectedDays * finalRatePerPost;
		}

		// Base amount
		double baseAmount = totalTeamNeed * selectedDays * baseRate;

		// Total amount including GST
		double amountForCalculation = discountedAmount != null ? discountedAmount : baseAmount;
		double totalAmount = amountForCalculation + (amountForCalculation * gstPercentage / 100.0);

		// Role breakdown
		Map<String, Integer> roleBreakdown = project.getTeamNeeds().stream()
				.collect(Collectors.toMap(
						tn -> tn.getRole() != null ? tn.getRole() : "Unknown",
								AuditionNewTeamNeed::getCount,
								Integer::sum
						));

		// Build response DTO
		return AuditionPaymentDTO.builder()
				.projectId(projectId)
				.selectedDays(selectedDays)
				.totalTeamNeed(totalTeamNeed)
				.amountPerPost(baseRate)
				.finalRatePerPost(finalRatePerPost)
				.baseAmount(baseAmount)
				.discountedAmount(discountedAmount) 
				.discountPercentage(discountPercentage) 
				.gstPercentage(gstPercentage)
				.totalAmount(totalAmount)
				.roleBreakdown(roleBreakdown)
				.build();
	}
	@Override
	public void softDeleteTeamNeed(Integer teamNeedId, Integer userId, Integer companyId) {
		AuditionNewTeamNeed teamNeed = teamNeedRepository.findByIdAndStatusTrue(teamNeedId)
				.orElseThrow(() -> new RuntimeException("Audition not found or already deleted"));


		Integer ownerCompanyId = teamNeed.getProject().getCompany().getId();
		if (!ownerCompanyId.equals(companyId)) {
			throw new RuntimeException("You are not authorized to delete this Audition");
		}


		teamNeed.setStatus(false);
		teamNeed.setUpdatedBy(userId);
		teamNeed.setUpdatedDate(LocalDateTime.now());

		teamNeedRepository.save(teamNeed);
	}

	@Override
	@Scheduled(fixedRate = 300000) // every 5 minutes
	public void updateExpiredPaymentsAndProjects() {
		LocalDateTime now = LocalDateTime.now();

		List<AuditionPayment> expiredPayments =
				paymentRepository.findByPaymentStatusAndExpiryDateTimeBefore("SUCCESS", now);

		logger.info("Found {} Audition expired ", expiredPayments.size());

		for (AuditionPayment payment : expiredPayments) {
			if ("EXPIRED".equals(payment.getPaymentStatus())) {
				logger.debug("Skipping payment {} (already expired)", payment.getAuditionPaymentId());
				continue;
			}

			payment.setPaymentStatus("EXPIRED");
			paymentRepository.save(payment);
			logger.info("Marked payment {} as EXPIRED", payment.getAuditionPaymentId());

			AuditionNewProject project = payment.getProject();

			boolean hasActivePayment = paymentRepository
					.existsByProjectAndPaymentStatusAndExpiryDateTimeAfter(project, "SUCCESS", now);

			if (!hasActivePayment) {
				project.setStatus(false);
				projectRepository.save(project);
				logger.info("Project {} marked as inactive (no active payments)", project.getId());


				sendExpiryEmail(payment);

				sendInAppNotification(
						payment,
						"Project Expired",
						"Your project '" + project.getProjectTitle() + "' has expired. Please renew."
						);
				logger.info("Notifications sent for expired project '{}'", project.getProjectTitle());
			}
		}
	}


	private void sendExpiryEmail(AuditionPayment payment) {
		AuditionNewProject project = payment.getProject();
		String subject = "Audition Project Expired " + project.getProjectTitle();

		String content = "<html><body style='font-family:Arial, sans-serif; font-size:14px; color:#333;'>"
				+ "<div style='max-width:600px; margin:auto; padding:20px; border:1px solid #e0e0e0; border-radius:8px;'>"
				+ "<h2 style='color:#2E86C1;'>Audition Project Expired</h2>"
				+ "<p>Dear <b>" + payment.getUser().getName() + "</b>,</p>"
				+ "<p>We would like to inform you that your subscription for the audition project <b>'" 
				+ project.getProjectTitle() + "'</b> has expired on <b>" 
				+ payment.getExpiryDateTime().toLocalDate() + "</b>.</p>"
				+ "<p>To continue participating in this audition and accessing related opportunities, please renew your subscription at your earliest convenience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to contact our support team.</p>"

            + "</body></html>";

		try {
			mailNotification.sendEmail(
					payment.getUser().getName(),
					payment.getUser().getEmail(),
					subject,
					content
					);
			logger.info("Expiry email sent to {} for project {}", payment.getUser().getEmail(), project.getProjectTitle());
		} catch (Exception e) {
			logger.error("Failed to send expiry email to {} for project {}",
					payment.getUser().getEmail(), project.getProjectTitle(), e);
		}
	}
	@PostConstruct
	public void testProperties() {
		System.out.println(">>> payment.key = " + key);
		System.out.println(">>> payment.salt = " + salt);
	}


}
