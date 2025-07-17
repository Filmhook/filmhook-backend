package com.annular.filmhook.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import com.annular.filmhook.util.Utility;

import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;

import com.annular.filmhook.model.AddressList;
import com.annular.filmhook.model.Audition;
import com.annular.filmhook.model.AuditionAcceptanceDetails;
import com.annular.filmhook.model.AuditionDetails;
import com.annular.filmhook.model.AuditionIgnoranceDetails;
import com.annular.filmhook.model.AuditionRoles;
import com.annular.filmhook.model.AuditionSubDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.MediaFileCategory;

import com.annular.filmhook.repository.AddressListRepository;
import com.annular.filmhook.repository.AuditionAcceptanceRepository;
import com.annular.filmhook.repository.AuditionDetailsRepository;
import com.annular.filmhook.repository.AuditionIgnoranceRepository;
import com.annular.filmhook.repository.AuditionRepository;
import com.annular.filmhook.repository.AuditionRolesRepository;
import com.annular.filmhook.repository.AuditionSubDetailsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.webmodel.AddressListWebModel;
import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionDetailsWebModel;
import com.annular.filmhook.webmodel.AuditionIgnoranceWebModel;
import com.annular.filmhook.webmodel.AuditionRolesWebModel;
import com.annular.filmhook.webmodel.AuditionWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class AuditionServiceImpl implements AuditionService {

	public static final Logger logger = LoggerFactory.getLogger(AuditionServiceImpl.class);

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	private AuditionSubDetailsRepository auditionSubDetailsRepository;

	@Autowired
	UserService userService;

	@Autowired
	AuditionRepository auditionRepository;
	
  

	// Define the date formatter for parsing endDate as "yyyy-MM-dd"
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Autowired
	AuditionRolesRepository auditionRolesRepository;

	@Autowired
	AuditionAcceptanceRepository acceptanceRepository;

	@Autowired
	AuditionDetailsRepository auditionDetailsRepository;

	@Autowired
	AddressListRepository addressListRepository;

	@Autowired
	AuditionIgnoranceRepository auditionIgnoranceRepository;

	@Autowired
	UserDetails userDetails;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String senderEmail;

	// @Autowired
	// KafkaProducer kafkaProducer;

	@Override
	public ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("Save audition method start");

			Optional<User> userFromDB = userService.getUser(auditionWebModel.getAuditionCreatedBy());
			if (!userFromDB.isPresent()) {
				return ResponseEntity.ok().body(new Response(-1, "User not found", null));
			}

			Audition audition = new Audition();
			audition.setAuditionTitle(auditionWebModel.getAuditionTitle());
			audition.setUser(userFromDB.get());
			audition.setAuditionExperience(auditionWebModel.getAuditionExperience());
			audition.setAuditionCategory(auditionWebModel.getAuditionCategory());
			audition.setAuditionSubCategory(auditionWebModel.getAuditionSubCategory());
			audition.setAuditionExpireOn(auditionWebModel.getAuditionExpireOn());
			audition.setAuditionPostedBy(userFromDB.get().getFilmHookCode());
			audition.setAuditionCreatedBy(auditionWebModel.getAuditionCreatedBy());
			audition.setAuditionAddress(auditionWebModel.getAuditionAddress());
			audition.setAuditionMessage(auditionWebModel.getAuditionMessage());
			audition.setAuditionLocation(auditionWebModel.getAuditionLocation());
			audition.setCompanyName(auditionWebModel.getCompanyName());
			audition.setUrl(auditionWebModel.getUrl());
			audition.setTermsAndCondition(auditionWebModel.getTermsAndCondition());
			audition.setStartDate(auditionWebModel.getStartDate());
			audition.setEndDate(auditionWebModel.getEndDate());
			audition.setAuditionIsactive(true);
			audition.setPaymentStatus("Created");


			Audition savedAudition = auditionRepository.save(audition);
			List<AuditionRoles> auditionRolesList = new ArrayList<>();

			if (auditionWebModel.getAuditionRoles().length != 0) {
				String[] auditionRolesArray = auditionWebModel.getAuditionRoles();
				for (String role : auditionRolesArray) {
					AuditionRoles auditionRoles = new AuditionRoles(); // Create a new instance inside the loop
					auditionRoles.setAuditionRoleDesc(role);
					auditionRoles.setAudition(savedAudition);
					auditionRoles.setAuditionRoleCreatedBy(savedAudition.getAuditionCreatedBy());
					auditionRoles.setAuditionRoleIsactive(true);

					auditionRolesList.add(auditionRolesRepository.save(auditionRoles));
				}
			}

			auditionWebModel.getFileInputWebModel().setCategory(MediaFileCategory.Audition);
			auditionWebModel.getFileInputWebModel().setCategoryRefId(savedAudition.getAuditionId()); // adding the story																										
			List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
					.saveMediaFiles(auditionWebModel.getFileInputWebModel(), userFromDB.get());

			response.put("Audition details", savedAudition);
			response.put("Audition roles", auditionRolesList);
			response.put("Media files", fileOutputWebModelList);

		} catch (Exception e) {
			logger.error("Save audition Method Exception -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(1, "Audition details saved successfully", response));
	}

	@Override
	public ResponseEntity<?> getAuditionByCategory(Integer categoryId) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("get audition by category method start");

			Integer userId = userDetails.userInfo().getId();
			// Fetch the list of ignored auditions for the given user
			List<Integer> ignoredAuditionIds = auditionIgnoranceRepository.findIgnoredAuditionIdsByUserId(userId);

			// Fetch the list of auditions by category and exclude the ignored ones
			List<Audition> auditions = auditionRepository.findByAuditionCategory(categoryId).stream()
					.filter(audition -> 
					!ignoredAuditionIds.contains(audition.getAuditionId()) &&
					"SUCCESS".equalsIgnoreCase(audition.getPaymentStatus())
							)
					.collect(Collectors.toList());

			if (!auditions.isEmpty()) {
				auditions.sort(Comparator.comparing(Audition::getAuditionCreatedOn).reversed());

				List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();

				for (Audition audition : auditions) {
					AuditionWebModel auditionWebModel = new AuditionWebModel();

					auditionWebModel.setAuditionId(audition.getAuditionId());
					auditionWebModel.setAuditionTitle(audition.getAuditionTitle());
					auditionWebModel.setAuditionExperience(audition.getAuditionExperience());
					// auditionWebModel.setUserId(audition.getUser().getUserId());
					auditionWebModel.setAuditionCategory(audition.getAuditionCategory());
					auditionWebModel.setAuditionExpireOn(audition.getAuditionExpireOn());
					auditionWebModel.setAuditionPostedBy(audition.getAuditionPostedBy());
					auditionWebModel.setAuditionAddress(audition.getAuditionAddress());
					auditionWebModel.setStartDate(audition.getStartDate());
					auditionWebModel.setEndDate(audition.getEndDate());
					auditionWebModel.setCompanyName(audition.getCompanyName());

					auditionWebModel.setUrl(audition.getUrl());
					auditionWebModel.setTermsAndCondition(audition.getTermsAndCondition());
					auditionWebModel.setAuditionMessage(audition.getAuditionMessage());
					auditionWebModel.setAuditionCreatedOn(audition.getAuditionCreatedOn());
					auditionWebModel.setAuditionLocation(audition.getAuditionLocation());
					auditionWebModel
					.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
					auditionWebModel
					.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));
					// Check if the current user has accepted this audition
					// Check if the current user has accepted this audition
					boolean isAccepted = acceptanceRepository.existsByAuditionAcceptanceUserAndAuditionRefId(userId,
							audition.getAuditionId());
					auditionWebModel.setAuditionAttendanceStatus(isAccepted); // true if exists, false otherwise

					// Fetch additional user details
					Optional<User> userOptional = userService.getUser(audition.getUser().getUserId());
					userOptional.ifPresent(user -> {
						auditionWebModel.setFilmHookCode(user.getFilmHookCode());
						auditionWebModel.setName(user.getName());
						auditionWebModel.setAdminReview(user.getAdminReview());
						auditionWebModel.setUserType(user.getUserType());
						auditionWebModel.setUserId(user.getUserId());
						auditionWebModel.setProfilePic(userService.getProfilePicUrl(userId));
					});
					if (!audition.getAuditionRoles().isEmpty()) {
						List<AuditionRolesWebModel> auditionRolesWebModelsList = new ArrayList<>();
						for (AuditionRoles auditionRoles : audition.getAuditionRoles()) {
							AuditionRolesWebModel auditionRolesWebModel = new AuditionRolesWebModel();
							auditionRolesWebModel.setAuditionRoleId(auditionRoles.getAuditionRoleId());
							auditionRolesWebModel.setAuditionRoleDesc(auditionRoles.getAuditionRoleDesc());
							auditionRolesWebModelsList.add(auditionRolesWebModel);
						}
						auditionWebModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
					}

					List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
					if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
						auditionWebModel.setFileOutputWebModel(fileOutputWebModelList);
					}
					auditionWebModelsList.add(auditionWebModel);
				}
				response.put("Audition List", auditionWebModelsList);
			} else {
				response.put("No auditions found", "");
			}
		} catch (Exception e) {
			logger.error("get audition by category Method Exception -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(1, "Audition details fetched successfully", response));
	}

	//    @Override
	//    public ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel) {
	//        HashMap<String, Object> response = new HashMap<>();
	//        try {
	//            logger.info("Save audition acceptance method start");
	//            Optional<Audition> audition = auditionRepository.findById(acceptanceWebModel.getAuditionRefId());
	//            if (audition.isPresent()) {
	//                AuditionAcceptanceDetails acceptanceDetails = new AuditionAcceptanceDetails();
	//                acceptanceDetails.setAuditionAccepted(acceptanceWebModel.isAuditionAccepted());
	//                acceptanceDetails.setAuditionAcceptanceUser(acceptanceWebModel.getAuditionAcceptanceUser());
	//                acceptanceDetails.setAuditionRefId(acceptanceWebModel.getAuditionRefId());
	//                acceptanceDetails.setAuditionAcceptanceCreatedBy(acceptanceWebModel.getAuditionAcceptanceUser());
	//                acceptanceDetails = acceptanceRepository.save(acceptanceDetails);
	//                response.put("Audition acceptance", acceptanceDetails);
	//            }
	//        } catch (Exception e) {
	//            logger.error("Save audition acceptance Method Exception -> {}", e.getMessage());
	//            e.printStackTrace();
	//            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
	//        }
	//        return ResponseEntity.ok().body(new Response(1, "Audition acceptance details saved successfully", response));
	//    }

	// Main method for audition acceptance
	public ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("Save audition acceptance method start");

			// Fetch the audition using the auditionRefId
			Optional<Audition> audition = auditionRepository.findById(acceptanceWebModel.getAuditionRefId());
			if (audition.isPresent()) {
				// Save audition acceptance details

				// Fetch associated audition roles
				//List<AuditionRoles> auditionRoles = foundAudition.getAuditionRoles();
				AuditionAcceptanceDetails acceptanceDetails = new AuditionAcceptanceDetails();
				acceptanceDetails.setAuditionAccepted(acceptanceWebModel.isAuditionAccepted());
				acceptanceDetails.setAuditionAcceptanceUser(acceptanceWebModel.getAuditionAcceptanceUser());
				acceptanceDetails.setAuditionRefId(acceptanceWebModel.getAuditionRefId());
				acceptanceDetails.setAuditionAcceptanceCreatedBy(acceptanceWebModel.getAuditionAcceptanceUser());
				acceptanceDetails = acceptanceRepository.save(acceptanceDetails);
				response.put("Audition acceptance", acceptanceDetails);



				// Send email if audition is accepted
				if (acceptanceWebModel.isAuditionAccepted()) {
					// Retrieve email details of the post's owner
					Optional<User> user = userRepository.findByUserId(audition.get().getUser().getUserId());
					if (user.isPresent()) {
						String recipientEmail = user.get().getEmail();
						String userName = user.get().getName();

						// Retrieve the email address of the user accepting the audition
						Optional<User> acceptingUser = userRepository
								.findById(acceptanceWebModel.getAuditionAcceptanceUser());
						if (acceptingUser.isPresent()) {
							String replyToEmail = acceptingUser.get().getEmail();
							String userName2 = acceptingUser.get().getName();

							// Prepare email content
							String subject = "Interest in Audition Post";
							String mailContent = "<p>I hope this message finds you well.</p>"
									+ "<p>I am writing to express my interest in the audition opportunity for the role of " + audition.get().getAuditionTitle() 
									+ " as posted on the Film-hook app. After reviewing the details of the audition, I believe my experience and skills align well with the requirements of the role. I would be thrilled to be considered for this opportunity.</p>"
									+ "<p>Please let me know if there are any further steps I need to take in the process or if additional information is required. I look forward to the possibility of working with your team.</p>"
									+ "<p>Thank you for your time and consideration.</p>";
							// Send email notification
							boolean emailSent = sendEmail(userName, recipientEmail, replyToEmail, subject, mailContent,
									userName2);
							if (!emailSent) {
								logger.warn("Failed to send email notification to {}", recipientEmail);
							}
						} else {
							logger.warn("Audition acceptance user not found for userId {}",
									acceptanceWebModel.getAuditionAcceptanceUser());
						}
					} else {
						logger.warn("User not found for auditionRefId {}", acceptanceWebModel.getAuditionRefId());
					}
				}
			} else {
				logger.warn("Audition not found for auditionRefId {}", acceptanceWebModel.getAuditionRefId());
				return ResponseEntity.badRequest().body(new Response(-1, "Audition not found", ""));
			}
		} catch (Exception e) {
			logger.error("Save audition acceptance Method Exception -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(1, "Audition acceptance details saved successfully", response));
	}

	// Email sending helper method
	public boolean sendEmail(String userName, String mailId, String replyToEmail, String subject, String mailContent,
			String userName2) {
		try {
			String senderName = "Film-hook IT-Support";
			String finalMailContent = "<div style='font-family:Verdana;font-size:12px;'>";
			finalMailContent += "<p>Dear <b>" + userName + "</b>,</p>";
			finalMailContent += mailContent;
			finalMailContent += "<p>Kind regards,<br>" + userName2 + "</p></div>";

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setFrom("${spring.mail.username}", senderName); // replace with actual email or property placeholder
			helper.setTo(mailId);
			//            helper.setTo(replyToEmail);
			//            helper.setReplyTo(mailId);
			helper.setReplyTo(replyToEmail); // Set the Reply-To address
			helper.setSubject(subject);
			helper.setText(finalMailContent, true);
			javaMailSender.send(message);
			return true;
		} catch (Exception e) {
			logger.error("Error sending email -> {}", e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ResponseEntity<?> auditionIgnorance(AuditionIgnoranceWebModel auditionIgnoranceWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("Save audition ignorance method start");

			Optional<Audition> audition = auditionRepository.findById(auditionIgnoranceWebModel.getAuditionRefId());

			if (audition.isPresent()) {
				Optional<AuditionIgnoranceDetails> existingDetailsOptional = auditionIgnoranceRepository
						.findByAuditionRefIdAndAuditionIgnoranceUser(auditionIgnoranceWebModel.getAuditionRefId(),
								auditionIgnoranceWebModel.getAuditionIgnoranceUser());

				AuditionIgnoranceDetails ignoranceDetails;
				if (existingDetailsOptional.isPresent()) {
					// Update the existing record
					ignoranceDetails = existingDetailsOptional.get();
					ignoranceDetails.setIgnoranceAccepted(auditionIgnoranceWebModel.isIgnoranceAccepted());
					ignoranceDetails
					.setAuditionIgnoranceUpdatedBy(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
				} else {
					// Create a new record
					ignoranceDetails = new AuditionIgnoranceDetails();
					ignoranceDetails.setIgnoranceAccepted(auditionIgnoranceWebModel.isIgnoranceAccepted());
					ignoranceDetails.setAuditionIgnoranceUser(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
					ignoranceDetails.setAuditionRefId(auditionIgnoranceWebModel.getAuditionRefId());
					ignoranceDetails
					.setAuditionIgnoranceCreatedBy(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
				}

				ignoranceDetails = auditionIgnoranceRepository.save(ignoranceDetails);
				response.put("Audition ignorance details", ignoranceDetails);
			} else {
				return ResponseEntity.ok().body(new Response(-1, "Audition not found", null));
			}
		} catch (Exception e) {
			logger.error("Save audition ignorance method exception -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(1, "Audition ignorance details saved successfully", response));
	}

	public ResponseEntity<?> getAuditionDetails(AuditionDetailsWebModel auditionDetailsWebModel) {
		// Fetch all AuditionDetails
		List<AuditionDetails> auditionDetailsList = auditionDetailsRepository.findAll();

		if (auditionDetailsList.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		// Create a list to hold the response data
		List<Map<String, Object>> responseList = auditionDetailsList.stream().map(auditionDetails -> {
			Map<String, Object> response = new HashMap<>();
			response.put("auditionDetailsId", auditionDetails.getAuditionDetailsId());
			response.put("auditionDetailsName", auditionDetails.getAuditionDetailsName());
			return response;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(responseList);
	}

	@Override
	public ResponseEntity<?> getAllAddressList() {
		List<AddressList> addressLists = addressListRepository.findAll().parallelStream()
				.filter(address -> address.getStatus().equals(true)
						&& !Utility.isNullOrBlankWithTrim(address.getAuditionAddress()))
				.collect(Collectors.toList());
		List<AddressListWebModel> result = addressLists.stream().map(addr -> AddressListWebModel.builder()
				.id(addr.getId()).address(addr.getAuditionAddress()).status(addr.getStatus()).build())
				.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getAddressList(String address) {
		List<AddressList> addressLists = addressListRepository.findByAuditionAddressContainingIgnoreCase(address)
				.parallelStream().filter(addressList -> addressList.getStatus().equals(true))
				.collect(Collectors.toList());
		List<AddressListWebModel> result = addressLists.stream().map(addr -> AddressListWebModel.builder()
				.id(addr.getId()).address(addr.getAuditionAddress()).status(addr.getStatus()).build())
				.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getAuditionByFilterAddress(Integer categoryId, String searchKey) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("get audition by category and address method start");

			Integer userId = userDetails.userInfo().getId();
			// Fetch the list of ignored auditions for the given user
			List<Integer> ignoredAuditionIds = auditionIgnoranceRepository.findIgnoredAuditionIdsByUserId(userId);

			// Fetch the list of auditions by category
			List<Audition> auditionsByCategory = auditionRepository.findByAuditionCategory(categoryId).stream()
					.filter(audition -> !ignoredAuditionIds.contains(audition.getAuditionId()))
					.collect(Collectors.toList());

			// Filter auditions by search key
			List<Audition> auditionsWithSearchKey = new ArrayList<>();
			List<Audition> auditionsWithoutSearchKey = new ArrayList<>();
			for (Audition audition : auditionsByCategory) {
				if (auditionContainsSearchKey(audition, searchKey)) {
					auditionsWithSearchKey.add(audition);
				} else {
					auditionsWithoutSearchKey.add(audition);
				}
			}

			// Combine auditions with search key and without
			List<Audition> combinedAuditions = new ArrayList<>();
			combinedAuditions.addAll(auditionsWithSearchKey);
			combinedAuditions.addAll(auditionsWithoutSearchKey);

			if (!combinedAuditions.isEmpty()) {
				combinedAuditions.sort(Comparator.comparing(Audition::getAuditionCreatedOn).reversed());
				List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();

				for (Audition audition : combinedAuditions) {
					AuditionWebModel auditionWebModel = new AuditionWebModel();

					auditionWebModel.setAuditionId(audition.getAuditionId());
					auditionWebModel.setAuditionTitle(audition.getAuditionTitle());
					auditionWebModel.setAuditionExperience(audition.getAuditionExperience());
					auditionWebModel.setAuditionCategory(audition.getAuditionCategory());
					auditionWebModel.setAuditionExpireOn(audition.getAuditionExpireOn());
					auditionWebModel.setAuditionPostedBy(audition.getAuditionPostedBy());
					auditionWebModel.setAuditionAddress(audition.getAuditionAddress());
					auditionWebModel.setAuditionLocation(audition.getAuditionLocation());
					auditionWebModel.setAuditionMessage(audition.getAuditionMessage());
					auditionWebModel
					.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
					auditionWebModel
					.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));
					boolean isAccepted = acceptanceRepository.existsByAuditionAcceptanceUserAndAuditionRefId(userId,
							audition.getAuditionId());
					auditionWebModel.setAuditionAttendanceStatus(isAccepted); // true if exists, false otherwise

					// Fetch additional user details
					Optional<User> userOptional = userService.getUser(userId);
					userOptional.ifPresent(user -> {
						auditionWebModel.setFilmHookCode(user.getFilmHookCode());
						auditionWebModel.setName(user.getName());
					});
					if (!audition.getAuditionRoles().isEmpty()) {
						List<AuditionRolesWebModel> auditionRolesWebModelsList = new ArrayList<>();
						for (AuditionRoles auditionRoles : audition.getAuditionRoles()) {
							AuditionRolesWebModel auditionRolesWebModel = new AuditionRolesWebModel();
							auditionRolesWebModel.setAuditionRoleId(auditionRoles.getAuditionRoleId());
							auditionRolesWebModel.setAuditionRoleDesc(auditionRoles.getAuditionRoleDesc());

							auditionRolesWebModelsList.add(auditionRolesWebModel);
						}
						auditionWebModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
					}

					List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
					if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
						auditionWebModel.setFileOutputWebModel(fileOutputWebModelList);
					}

					auditionWebModelsList.add(auditionWebModel);
				}
				response.put("Audition List", auditionWebModelsList);
			} else {
				response.put("No auditions found", "");
			}

		} catch (Exception e) {
			logger.error("get audition by category and address Method Exception -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(1, "Audition details fetched successfully", response));
	}

	private boolean auditionContainsSearchKey(Audition audition, String searchKey) {
		// Implement your logic to check if the search key is present in the audition
		// Example: Check if the audition address contains the search key
		return audition.getAuditionAddress().toLowerCase().contains(searchKey.toLowerCase());
	}

	@Transactional
	public ResponseEntity<?> deleteAuditionById(Integer auditionId, Integer userId) {
		try {
			Optional<Audition> auditionData = auditionRepository.findById(auditionId);
			if (auditionData.isPresent()) {
				Audition audition = auditionData.get();
				logger.info("User ID: {}", userId);
				logger.info("Audition Created By: {}", audition.getAuditionCreatedBy());
				if (audition.getAuditionCreatedBy().equals(userId)) {
					auditionRolesRepository.deleteByAuditionId(auditionId);
					auditionRepository.deleteById(auditionId);
					// auditionRolesRepository.deleteByAuditionId(auditionId);
					acceptanceRepository.deleteByAuditionRefId(auditionId); // Delete related AuditionAcceptanceDetails
					auditionIgnoranceRepository.deleteByAuditionRefId(auditionId);
					return ResponseEntity.ok(new Response(1, "Audition deleted successfully.", null));
				} else {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(-1,
							"Unauthorized: You do not have permission to delete this audition.", null));
				}
			} else {
				return ResponseEntity.ok().body(new Response(-1, "Audition not found.", null));
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> updateAudition(AuditionWebModel auditionWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("Update audition method start");

			// Check if the auditionId is provided
			if (auditionWebModel.getAuditionId() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new Response(-1, "Audition ID is required for updating.", null));
			}

			// Check if the audition exists
			Optional<Audition> existingAuditionOptional = auditionRepository.findById(auditionWebModel.getAuditionId());
			if (existingAuditionOptional.isEmpty()) {
				return ResponseEntity.ok().body(new Response(-1, "Audition not found.", null));
			}

			Audition existingAudition = existingAuditionOptional.get();

			// Update audition details
			existingAudition.setAuditionTitle(auditionWebModel.getAuditionTitle());
			existingAudition.setAuditionExperience(auditionWebModel.getAuditionExperience());
			existingAudition.setAuditionCategory(auditionWebModel.getAuditionCategory());
			existingAudition.setAuditionExpireOn(auditionWebModel.getAuditionExpireOn());
			existingAudition.setAuditionAddress(auditionWebModel.getAuditionAddress());
			existingAudition.setAuditionMessage(auditionWebModel.getAuditionMessage());
			existingAudition.setAuditionLocation(auditionWebModel.getAuditionLocation());
			existingAudition.setUrl(auditionWebModel.getUrl());
			existingAudition.setTermsAndCondition(auditionWebModel.getTermsAndCondition());
			existingAudition.setStartDate(auditionWebModel.getStartDate());
			existingAudition.setEndDate(auditionWebModel.getEndDate());
			existingAudition.setAuditionIsactive(true);

			// Update the audition
			Audition savedAudition = auditionRepository.save(existingAudition);

			auditionWebModel.getFileInputWebModel().setCategory(MediaFileCategory.Audition);
			auditionWebModel.getFileInputWebModel().setCategoryRefId(savedAudition.getAuditionId()); // adding the story
			List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
					.saveMediaFiles(auditionWebModel.getFileInputWebModel(), existingAudition.getUser());

			mediaFilesService.deleteMediaFilesByUserIdAndCategoryAndRefIds(savedAudition.getUser().getUserId(),
					MediaFileCategory.Audition, auditionWebModel.getMediaFilesIds());

			List<AuditionRoles> auditionRolesList = new ArrayList<>();

			// Update existing roles if auditionRolesId is provided
			if (!Utility.isNullOrEmptyList(auditionWebModel.getAuditionRolesWebModels())) {
				for (AuditionRolesWebModel role : auditionWebModel.getAuditionRolesWebModels()) {
					if (role.getAuditionRoleId() != null) {
						Optional<AuditionRoles> existingRoleOptional = auditionRolesRepository
								.findById(role.getAuditionRoleId());
						if (existingRoleOptional.isPresent()) {
							AuditionRoles existingRole = existingRoleOptional.get();
							existingRole.setAuditionRoleDesc(role.getAuditionRoleDesc());
							auditionRolesList.add(auditionRolesRepository.save(existingRole));
						}
					}
				}
			}

			// Create new roles if auditionRolesId is not provided
			if (auditionWebModel.getAuditionRoles() != null) {
				for (String roleDesc : auditionWebModel.getAuditionRoles()) {
					AuditionRoles newRole = new AuditionRoles();
					newRole.setAudition(savedAudition);
					newRole.setAuditionRoleDesc(roleDesc);
					newRole.setAuditionRoleCreatedBy(auditionWebModel.getAuditionCreatedBy());
					newRole.setAuditionRoleIsactive(true);
					auditionRolesList.add(auditionRolesRepository.save(newRole));
				}
			}

			response.put("Audition details", savedAudition);
		} catch (Exception e) {
			logger.error("Update audition Method Exception -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(1, "Audition details updated successfully", response));
	}

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void deactivateExpiredAuditions() {
		LocalDate today = LocalDate.now();

		// Fetch all active auditions to check for expiration
		List<Audition> activeAuditions = auditionRepository.findByAuditionIsactiveTrue();

		for (Audition audition : activeAuditions) {
			try {
				LocalDate endDate = LocalDate.parse(audition.getEndDate(), DATE_FORMATTER);
				if (endDate.isBefore(today) || endDate.isEqual(today)) {
					audition.setAuditionIsactive(false);
				}
			} catch (Exception e) {
				// Handle any parsing errors (e.g., log the error if date format is incorrect)
				System.err.println("Invalid date format for audition ID " + audition.getAuditionId());
			}
		}

		// Save all updated auditions in batch
		auditionRepository.saveAll(activeAuditions);
	}

	@Override
	public ResponseEntity<?> getAcceptanceDetailsByUserId(AuditionWebModel auditionWebModel) {
		// Fetch acceptance user list based on AuditionRefId
		List<AuditionAcceptanceDetails> acceptanceUserList = acceptanceRepository
				.findByAuditionRefId(auditionWebModel.getAuditionRefId());

		// Check if the list is empty
		if (acceptanceUserList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("No acceptance details found for the provided AuditionRefId.");
		}

		// Create a List to store acceptance details (acceptanceId, username,
		// profilePic)
		List<Map<String, Object>> acceptanceDetailsList = new ArrayList<>();

		// Iterate over the acceptance user list and populate the details
		for (AuditionAcceptanceDetails acceptance : acceptanceUserList) {
			Integer acceptanceId = acceptance.getAuditionAcceptanceUser();

			// Fetch the user details based on the user ID associated with the acceptance
			Optional<User> userOptional = userRepository.findById(acceptance.getAuditionAcceptanceUser());

			if (userOptional.isPresent()) {
				User user = userOptional.get();
				Map<String, Object> userDetails = new HashMap<>();
				userDetails.put("acceptanceId", acceptanceId);
				userDetails.put("username", user.getName());
				userDetails.put("userProfilePic", userService.getProfilePicUrl(user.getUserId())); // Assuming you have
				// a method to get
				// the profile pic
				// URL

				// Add the user details map to the list
				acceptanceDetailsList.add(userDetails);
			}
		}

		// Return the list of acceptance details
		return ResponseEntity.ok(acceptanceDetailsList);
	}

	@Override
	public ResponseEntity<?> getAuditionByUserId(AuditionWebModel auditionWebModel) {

		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("get audition by category method start");

			Integer userId = userDetails.userInfo().getId();

			// Fetch the list of auditions by category and exclude the ignored ones
			List<Audition> auditions = auditionRepository.findByUserId(userId);

			if (!auditions.isEmpty()) {
				List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();

				for (Audition audition : auditions) {
					AuditionWebModel webModel = new AuditionWebModel();

					// Map basic audition details
					webModel.setAuditionId(audition.getAuditionId());
					webModel.setAuditionTitle(audition.getAuditionTitle());
					webModel.setAuditionCreatedOn(audition.getAuditionCreatedOn());
					webModel.setAuditionExperience(audition.getAuditionExperience());
					webModel.setAuditionCategory(audition.getAuditionCategory());
					webModel.setAuditionExpireOn(audition.getAuditionExpireOn());
					webModel.setAuditionPostedBy(audition.getAuditionPostedBy());
					webModel.setUserId(audition.getUser().getUserId());
					webModel.setStartDate(audition.getStartDate());
					webModel.setEndDate(audition.getEndDate());
					webModel.setUrl(audition.getUrl());
					webModel.setTermsAndCondition(audition.getTermsAndCondition());
					webModel.setAuditionAddress(audition.getAuditionAddress());
					webModel.setAuditionMessage(audition.getAuditionMessage());
					webModel.setAuditionLocation(audition.getAuditionLocation());
					webModel.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
					webModel.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));

					// Check if the current user has accepted this audition
					boolean isAccepted = acceptanceRepository.existsByAuditionAcceptanceUserAndAuditionRefId(userId,
							audition.getAuditionId());
					webModel.setAuditionAttendanceStatus(isAccepted);

					// Fetch additional user details
					userService.getUser(userId).ifPresent(user -> {
						webModel.setFilmHookCode(user.getFilmHookCode());
						webModel.setName(user.getName());
					});

					// Map audition roles
					if (!audition.getAuditionRoles().isEmpty()) {
						List<AuditionRolesWebModel> auditionRolesWebModelsList = audition.getAuditionRoles().stream()
								.map(role -> {
									AuditionRolesWebModel roleModel = new AuditionRolesWebModel();
									roleModel.setAuditionRoleId(role.getAuditionRoleId());
									roleModel.setAuditionRoleDesc(role.getAuditionRoleDesc());
									return roleModel;
								}).collect(Collectors.toList());
						webModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
					}

					// Attach media files
					List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
					if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
						webModel.setFileOutputWebModel(fileOutputWebModelList);
					}

					auditionWebModelsList.add(webModel);
				}

				response.put("Audition List", auditionWebModelsList);
			} else {
				response.put("No auditions found", "");
			}

		} catch (Exception e) {
			logger.error("get audition by category Method Exception -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(1, "Audition details fetched successfully", response));
	}

	@Override
	public ResponseEntity<?> getAuditionAcceptanceListByUserId(AuditionWebModel auditionWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			Integer currentUserId = userDetails.userInfo().getId();
			Optional<User> userData = userRepository.findById(currentUserId);

			// Fetch the acceptance data for the current user
			List<AuditionAcceptanceDetails> acceptanceData = acceptanceRepository.findByUserId(currentUserId);

			// List to store audition response data
			List<AuditionWebModel> acceptedAuditions = new ArrayList<>();

			for (AuditionAcceptanceDetails acceptanceDetail : acceptanceData) {
				Integer auditionRefId = acceptanceDetail.getAuditionRefId(); // Get the reference ID of the audition
				Audition audition = auditionRepository.findById(auditionRefId).orElse(null);

				if (audition != null) {
					AuditionWebModel webModel = new AuditionWebModel();

					// Set basic audition details
					webModel.setAuditionId(audition.getAuditionId());
					webModel.setCompanyName(audition.getCompanyName());
					webModel.setAuditionTitle(audition.getAuditionTitle());
					webModel.setAuditionExperience(audition.getAuditionExperience());
					webModel.setAuditionCategory(audition.getAuditionCategory());
					webModel.setAuditionExpireOn(audition.getAuditionExpireOn());
					webModel.setAuditionCreatedBy(audition.getAuditionCreatedBy());
					webModel.setAuditionCreatedOn(audition.getAuditionCreatedOn());
					webModel.setAuditionPostedBy(audition.getAuditionPostedBy());
					webModel.setAuditionAddress(audition.getAuditionAddress());
					webModel.setAuditionMessage(audition.getAuditionMessage());
					webModel.setAuditionLocation(audition.getAuditionLocation());
					webModel.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
					webModel.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));
					webModel.setAuditionAttendanceStatus(true); // Already accepted
					webModel.setStartDate(audition.getStartDate());
					webModel.setEndDate(audition.getEndDate());
					webModel.setUserId(audition.getUser().getUserId());
					webModel.setName(audition.getUser().getName());

					// Get the list of roles and convert to String[]
					List<AuditionRoles> auditionRolesList = audition.getAuditionRoles(); // Fetch audition roles list here
					String[] rolesArray = new String[auditionRolesList.size()];
					for (int i = 0; i < auditionRolesList.size(); i++) {
						rolesArray[i] = auditionRolesList.get(i).toString(); // Adjust if a different method is needed
					}
					webModel.setAuditionRoles(rolesArray);

					webModel.setAdminReview(audition.getUser().getAdminReview());
					webModel.setUserType(audition.getUser().getUserType());
					webModel.setProfilePic(userService.getProfilePicUrl(audition.getUser().getUserId()));
					webModel.setUrl(audition.getUrl());
					webModel.setTermsAndCondition(audition.getTermsAndCondition());

					// Attach media files
					List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
					if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
						webModel.setFileOutputWebModel(fileOutputWebModelList);
					}

					acceptedAuditions.add(webModel);
				}
			}

			response.put("Accepted Audition List", acceptedAuditions);

		} catch (Exception e) {
			logger.error("Exception in getAuditionAcceptanceListByUserId -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
		}

		return ResponseEntity.ok().body(new Response(1, "Accepted audition details fetched successfully", response));
	}	

	@Override
	public void updatePaymentStatus(String txnid, String status, String mihpayid, String amount) {
	    Integer txnids = Integer.parseInt(txnid);
	    Audition audition = auditionRepository.findById(txnids)
	            .orElseThrow(() -> new RuntimeException("Audition not found"));

	    audition.setPaymentStatus(status);
	    audition.setPaymentTransactionId(mihpayid);
	    audition.setAuditionUpdatedOn(LocalDateTime.now());
	    auditionRepository.save(audition);
	    String paymentRetryLink = "https://filmhookapps.com/retry-payment?auditionId=" + audition.getAuditionId();


	    User user = audition.getUser();
	    if (user == null) throw new RuntimeException("User not found for audition");

	    String email = user.getEmail();
	    String name = user.getName();
	    String capitalizedName = (name != null && name.length() > 0)
	            ? name.substring(0, 1).toUpperCase() + name.substring(1)
	            : "";

	    String subject;
	    StringBuilder content = new StringBuilder();
	    content.append("<!DOCTYPE html><html><head>")
	        .append("<meta charset='UTF-8'>")
	        .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
	        .append("<style>")
	        .append("@media only screen and (max-width: 600px) {")
	        .append("  .email-container { width: 100% !important; padding: 10px !important; }")
	        .append("  .email-content td { display: block !important; width: 100% !important; }")
	        .append("  img { max-width: 100% !important; height: auto !important; }")
	        .append("}")
	        .append("</style></head>")
	        .append("<body style='margin:0;padding:0;background:#f6f6f6;'>")
	        .append("<table cellpadding='0' cellspacing='0' width='100%' style='background:#f6f6f6;'>")
	        .append("<tr><td align='center'>")
	        .append("<table class='email-container' cellpadding='0' cellspacing='0' style='max-width:600px;width:100%;background:#ffffff;border-radius:8px;padding:20px;font-family:Arial,sans-serif;'>")

	        // Logo
	        .append("<tr><td align='center' style='padding-bottom:20px;'>")
	        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' alt='FilmHook Logo' style='width:180px;height:auto;'>")
	        .append("</td></tr>");

	    // Success Email
	    if ("SUCCESS".equalsIgnoreCase(status)) {
	        subject = "Audition Registration Successful";

	        content.append("<tr><td style='color:#333;font-size:12px;'>")
	            .append("<h3 style='color:#28a745;'>Audition Posted Successfully</h3>")
	            .append("<p>Hi <strong>").append(capitalizedName).append("</strong>,</p>")
	            .append("<p>Your audition has been successfully posted and payment received.</p>")
	            .append("<p><b>Audition Title:</b> ").append(audition.getAuditionTitle()).append("<br>")
	            .append("<b>Category:</b> ").append(audition.getAuditionCategory()).append("<br>")
	            .append("<b>Company:</b> ").append(audition.getCompanyName()).append("<br>")
	            .append("<b>Expires On:</b> ").append(audition.getAuditionExpireOn()).append("</p>")
	            .append("<p><b>Transaction ID:</b> ").append(mihpayid).append("<br>")
	            .append("<b>Amount Paid:</b> ₹").append(amount).append("</p>")
	            .append("<p>Thank you for using <strong>FilmHook</strong> to publish your audition!</p>");
	    } else {
	        // Failure Email
	        subject = "Payment Failed - Audition Not Published";

	        content.append("<tr><td style='color:#333;font-size:12px;'>")
	            .append("<h3 style='color:#dc3545;'>Payment Failed</h3>")
	            .append("<p>Hi <strong>").append(capitalizedName).append("</strong>,</p>")
	            .append("<p>Unfortunately, your payment failed and your audition was not published.</p>")
	            .append("<p><b>Audition Title:</b> ").append(audition.getAuditionTitle()).append("</p>")
	            .append("<p><b>Transaction ID:</b> ").append(mihpayid).append("<br>")
	            .append("<b>Attempted Amount:</b> ₹").append(amount).append("</p>")
	            .append("<p>🔄 <a href='").append(paymentRetryLink).append("' style='color:#007bff;'>Retry Payment</a></p>")
	            .append("<p>Please retry payment from your dashboard or contact our support team.</p>");
	    }

	    // Footer
	    content.append("<p style='margin-top:30px;'>Regards,<br><strong>FilmHook Team</strong><br>")
	        .append("📧 <a href='mailto:support@filmhook.com'>support@filmhook.com</a><br>")
	        .append("🌐 <a href='https://filmhook.com'>www.filmhook.com</a></p>")

	        .append("<hr style='border:0;border-top:1px solid #ccc;margin:30px 0;'>")
	        .append("<p>Download Our App:</p>")
	        .append("<p>")
	        .append("<a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' width='30'></a> ")
	        .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' width='30'></a>")
	        .append("</p>")
	        .append("<p>🔗 Follow Us:</p>")
	        .append("<p>")
	        .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='20'></a> ")
	        .append("<a href='https://x.com/Filmhook_Apps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='20'></a> ")
	        .append("<a href='https://www.threads.net/@filmhookapps/'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='20'></a> ")
	        .append("<a href='https://www.instagram.com/filmhookapps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='20'></a> ")
	        .append("<a href='https://youtube.com/@film-hookapps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='20'></a> ")
	        .append("<a href='https://www.linkedin.com/in/film-hook-68666a353'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linked.png' width='30'></a>")
	        .append("</p>")

	        .append("</td></tr></table></td></tr></table></body></html>");

	    try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        helper.setTo(email);
	        helper.setSubject(subject);
	        helper.setText(content.toString(), true);

	        if ("SUCCESS".equalsIgnoreCase(status)) {
	            byte[] pdf = generateAuditionInvoicePdf(audition, amount);
	            helper.addAttachment("AuditionInvoice_" + txnid + ".pdf",
	                    new ByteArrayDataSource(pdf, "application/pdf"));
	        }

	        javaMailSender.send(message);
	    } catch (Exception e) {
	        e.printStackTrace(); // log properly in production
	    }
	}

	private byte[] generateAuditionInvoicePdf(Audition audition, String amount) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);
			Document doc = new Document(pdf, PageSize.A4);
			doc.setMargins(36, 36, 36, 36);

			DeviceRgb blue = new DeviceRgb(41, 86, 184);
			final int fontSize = 10;

			double base = Double.parseDouble(amount);       
			double gst = base * 0.18;                        
			double total = base + gst;    
			String originalName = audition.getUser().getName();
			String capitalizedName = (originalName != null && !originalName.isEmpty())
					? originalName.substring(0, 1).toUpperCase() + originalName.substring(1)
					: "";

			// --- Logo ---
			   InputStream logoStream = new URL("https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png").openStream();
		        if (logoStream == null) throw new RuntimeException("Logo image not found in classpath");
		        Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()))
		                .scaleToFit(120, 60)
		                .setHorizontalAlignment(HorizontalAlignment.CENTER)
		                .setMarginBottom(8);
		        doc.add(logo);

			// --- Title ---
			doc.add(new Paragraph("TAX INVOICE")
					.setTextAlignment(TextAlignment.CENTER)
					.setFontSize(14)
					.setBold()
					.setFontColor(blue)
					.setMarginBottom(10));

			// --- Company Info ---
			Table header = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
					.setWidth(UnitValue.createPercentValue(100));
			header.addCell(new Cell()
					.add(new Paragraph("FilmHook Pvt. Ltd.")
							.setBold().setFontSize(13).setFontColor(blue))
					.add(new Paragraph("Bangalore\nGSTIN: 29ABCDE1234F2Z5\nEmail: support@filmhook.com\nPhone: +91-9876543210")
							.setFontSize(fontSize))
					.setBorder(Border.NO_BORDER));
			header.addCell(new Cell().setBorder(Border.NO_BORDER));
			doc.add(header);

			// --- Order Info ---
			Table orderInfo = new Table(UnitValue.createPercentArray(new float[]{33, 33, 33}))
					.setWidth(UnitValue.createPercentValue(100))
					.setMarginTop(15);
			orderInfo.addCell(getLightCell("Invoice No"));
			orderInfo.addCell(getLightCell("Date"));
			orderInfo.addCell(getLightCell("Candidate Email"));
			orderInfo.addCell(getPlainCell("INV-" + audition.getAuditionId()));
			orderInfo.addCell(getPlainCell(LocalDate.now().toString()));
			orderInfo.addCell(getPlainCell(audition.getUser().getEmail()));
			doc.add(orderInfo);

			// --- Bill To ---
			doc.add(new Paragraph("\nBill To")
					.setFontSize(fontSize)
					.setBold()
					.setMarginTop(8));
			doc.add(new Paragraph("Name: " + capitalizedName)
					.setFontSize(fontSize)
					.setMarginBottom(10));


			// --- Audition Info ---
			doc.add(new Paragraph("Audition Details")
					.setFontSize(fontSize)
					.setBold()
					.setMarginTop(5));
			doc.add(new Paragraph("Title: " + audition.getAuditionTitle())
					.setFontSize(fontSize));
			doc.add(new Paragraph("Category: " + audition.getAuditionCategory())
					.setFontSize(fontSize));
			doc.add(new Paragraph("Company: " + audition.getCompanyName())
					.setFontSize(fontSize));
			doc.add(new Paragraph("Audition Date: " + audition.getAuditionExpireOn())
					.setFontSize(fontSize)
					.setMarginBottom(10));

			// --- Charges Table ---
			Table charges = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
					.setWidth(UnitValue.createPercentValue(100))
					.setMarginTop(10);

			charges.addHeaderCell(getStyledBottomBorderHeader("Description"));
			Cell totalHeader = getStyledBottomBorderHeader("Total Amount");
			totalHeader.setTextAlignment(TextAlignment.RIGHT);
			charges.addHeaderCell(totalHeader);

			charges.addCell(getStyledBottomBorderCell("Audition Registration Fee"));
			Cell baseCell = getStyledBottomBorderCell("₹ " + String.format("%.2f", base));
			baseCell.setTextAlignment(TextAlignment.RIGHT);
			charges.addCell(baseCell);

			// GST Info
			Cell taxLabel = new Cell(1, 1)
					.add(new Paragraph("\nApplied Tax").setBold().setUnderline().setFontSize(9))
					.add(new Paragraph("(18% GST Included)").setFontSize(8))
					.setBorder(Border.NO_BORDER);
			Cell taxValue = new Cell()
					.add(new Paragraph("₹ " + String.format("%.2f", gst))
							.setTextAlignment(TextAlignment.RIGHT).setFontSize(9))
					.setBorder(Border.NO_BORDER);

			charges.addCell(taxLabel);
			charges.addCell(taxValue);

			// Total
			Cell totalLabel = new Cell(1, 1)
					.add(new Paragraph("Total Invoice Value")
							.setFontColor(blue)
							.setBold().setFontSize(10))
					.setBorderTop(new SolidBorder(ColorConstants.GRAY, 0.5f))
					.setBorder(Border.NO_BORDER);
			Cell totalAmount = new Cell()
					.add(new Paragraph("₹ " + String.format("%.2f", total))
							.setFontSize(10)
							.setBold()
							.setFontColor(blue)
							.setTextAlignment(TextAlignment.RIGHT))
					.setBorderTop(new SolidBorder(ColorConstants.GRAY, 0.5f))
					.setBorder(Border.NO_BORDER);

			charges.addCell(totalLabel);
			charges.addCell(totalAmount);
			doc.add(charges);

			// --- Declaration ---
			doc.add(new Paragraph("\nDeclaration")
					.setBold()
					.setFontSize(12)
					.setMarginTop(20));
			doc.add(new Paragraph("We declare that this invoice shows the actual price of the services provided and that all particulars are true and correct.")
					.setFontSize(fontSize));

			// --- Signature Section ---
			  InputStream signStream = new URL("https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png").openStream();
		        if (signStream == null) throw new RuntimeException("Signature image not found in classpath");
		        Image sign = new Image(ImageDataFactory.create(signStream.readAllBytes()))
		                .scaleToFit(80, 30);
		        Paragraph signText = new Paragraph("For FilmHook Pvt. Ltd\n(Authorized Signatory)")
		                .setFontSize(9)
		                .setTextAlignment(TextAlignment.RIGHT);
		        Paragraph signBlock = new Paragraph().add(sign).add("\n").add(signText);
		        Table signTable = new Table(1).setWidth(UnitValue.createPercentValue(100)).setMarginTop(30);
		        signTable.addCell(new Cell().add(signBlock)
		                .setBorder(Border.NO_BORDER)
		                .setTextAlignment(TextAlignment.RIGHT));
		        doc.add(signTable);


			doc.close();
			return baos.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to generate audition invoice PDF", e);
		}
	}

	private Cell getLightCell(String text) {
		return new Cell().add(new Paragraph(text).setBold().setFontSize(9))
				.setBackgroundColor(new DeviceRgb(245, 245, 245))
				.setPadding(4);
	}

	private Cell getPlainCell(String text) {
		return new Cell().add(new Paragraph(text).setFontSize(9)).setPadding(5);
	}

	private Cell getStyledBottomBorderHeader(String text) {
		return new Cell()
				.add(new Paragraph(text).setBold().setFontSize(10))
				.setBorderTop(Border.NO_BORDER)
				.setBorderLeft(Border.NO_BORDER)
				.setBorderRight(Border.NO_BORDER)
				.setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
	}

	private Cell getStyledBottomBorderCell(String text) {
		return new Cell()
				.add(new Paragraph(text).setFontSize(9))
				.setBorderTop(Border.NO_BORDER)
				.setBorderLeft(Border.NO_BORDER)
				.setBorderRight(Border.NO_BORDER)
				.setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
	}


@Override
	 public ResponseEntity<?> getSubDetailsByAuditionDetailsId(Integer auditionDetailsId) {
	        logger.info("Fetching audition sub-details for detailsId: {}", auditionDetailsId);

	        try {
	            List<AuditionSubDetails> subDetailsList =
	                    auditionSubDetailsRepository.findByAuditionDetails_AuditionDetailsId(auditionDetailsId);

	            if (subDetailsList.isEmpty()) {
	                logger.warn("No sub-details found for detailsId: {}", auditionDetailsId);
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body(new Response(0, "No sub-categories found for ID: " + auditionDetailsId, null));
	            }

	            logger.info("Successfully fetched {} sub-details for detailsId: {}", subDetailsList.size(), auditionDetailsId);
	            return ResponseEntity.ok(new Response(1, "Sub-categories fetched successfully", subDetailsList));

	        } catch (Exception e) {
	            logger.error("Exception while fetching sub-details for ID {}: {}", auditionDetailsId, e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(new Response(-1, "Error fetching sub-categories", e.getMessage()));
	        }
	    }

@Override
public ResponseEntity<?> getAuditionBySubCategory(Integer subCategoryId) {
    HashMap<String, Object> response = new HashMap<>();
    try {
        logger.info("get audition by sub-category method start");

        Integer userId = userDetails.userInfo().getId();
        List<Integer> ignoredAuditionIds = auditionIgnoranceRepository.findIgnoredAuditionIdsByUserId(userId);

        List<Audition> auditions = auditionRepository.findByAuditionSubCategory(subCategoryId).stream()
                .filter(audition ->
                        !ignoredAuditionIds.contains(audition.getAuditionId()) &&
                        "SUCCESS".equalsIgnoreCase(audition.getPaymentStatus()))
                .collect(Collectors.toList());

        if (!auditions.isEmpty()) {
            auditions.sort(Comparator.comparing(Audition::getAuditionCreatedOn).reversed());

            List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();
            for (Audition audition : auditions) {
                AuditionWebModel auditionWebModel = new AuditionWebModel();
                auditionWebModel.setAuditionId(audition.getAuditionId());
                auditionWebModel.setAuditionTitle(audition.getAuditionTitle());
                auditionWebModel.setAuditionExperience(audition.getAuditionExperience());
                auditionWebModel.setAuditionCategory(audition.getAuditionCategory());
                auditionWebModel.setAuditionSubCategory(audition.getAuditionSubCategory());
                auditionWebModel.setAuditionExpireOn(audition.getAuditionExpireOn());
                auditionWebModel.setAuditionPostedBy(audition.getAuditionPostedBy());
                auditionWebModel.setAuditionAddress(audition.getAuditionAddress());
                auditionWebModel.setStartDate(audition.getStartDate());
                auditionWebModel.setEndDate(audition.getEndDate());
                auditionWebModel.setCompanyName(audition.getCompanyName());
                auditionWebModel.setUrl(audition.getUrl());
                auditionWebModel.setTermsAndCondition(audition.getTermsAndCondition());
                auditionWebModel.setAuditionMessage(audition.getAuditionMessage());
                auditionWebModel.setAuditionCreatedOn(audition.getAuditionCreatedOn());
                auditionWebModel.setAuditionLocation(audition.getAuditionLocation());
                auditionWebModel.setAuditionAttendedCount(
                        acceptanceRepository.getAttendedCount(audition.getAuditionId()));
                auditionWebModel.setAuditionIgnoredCount(
                        acceptanceRepository.getIgnoredCount(audition.getAuditionId()));

                boolean isAccepted = acceptanceRepository
                        .existsByAuditionAcceptanceUserAndAuditionRefId(userId, audition.getAuditionId());
                auditionWebModel.setAuditionAttendanceStatus(isAccepted);

                Optional<User> userOptional = userService.getUser(audition.getUser().getUserId());
                userOptional.ifPresent(user -> {
                    auditionWebModel.setFilmHookCode(user.getFilmHookCode());
                    auditionWebModel.setName(user.getName());
                    auditionWebModel.setAdminReview(user.getAdminReview());
                    auditionWebModel.setUserType(user.getUserType());
                    auditionWebModel.setUserId(user.getUserId());
                    auditionWebModel.setProfilePic(userService.getProfilePicUrl(userId));
                });

                if (!audition.getAuditionRoles().isEmpty()) {
                    List<AuditionRolesWebModel> auditionRolesWebModelsList = new ArrayList<>();
                    for (AuditionRoles auditionRoles : audition.getAuditionRoles()) {
                        AuditionRolesWebModel auditionRolesWebModel = new AuditionRolesWebModel();
                        auditionRolesWebModel.setAuditionRoleId(auditionRoles.getAuditionRoleId());
                        auditionRolesWebModel.setAuditionRoleDesc(auditionRoles.getAuditionRoleDesc());
                        auditionRolesWebModelsList.add(auditionRolesWebModel);
                    }
                    auditionWebModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
                }

                List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
                        .getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
                if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
                    auditionWebModel.setFileOutputWebModel(fileOutputWebModelList);
                }

                auditionWebModelsList.add(auditionWebModel);
            }
            response.put("Audition List", auditionWebModelsList);
        } else {
            response.put("No auditions found", "");
        }
    } catch (Exception e) {
        logger.error("get audition by sub-category Exception -> {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
    }
    return ResponseEntity.ok().body(new Response(1, "Auditions fetched by sub-category", response));
}


}
