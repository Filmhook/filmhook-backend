package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Comparator;
import java.util.Objects;
import java.util.Date;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.IndustryDetails;
import com.annular.filmhook.model.IndustryTemporaryDetails;
import com.annular.filmhook.model.IndustryUserPermanentDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.PlatformDetails;
import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmProfessionDetails;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.FilmSubProfessionDetails;
import com.annular.filmhook.model.FilmSubProfessionPermanentDetail;
import com.annular.filmhook.model.User;

import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.repository.IndustryDetailRepository;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.IndustryTemporaryDetailRepository;
import com.annular.filmhook.repository.IndustryUserPermanentDetailsRepository;
import com.annular.filmhook.repository.PlatformDetailRepository;
import com.annular.filmhook.repository.PlatformPermanentDetailRepository;
import com.annular.filmhook.repository.PlatformRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.FilmProfessionDetailRepository;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.FilmSubProfessionDetailRepository;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.FilmSubProfessionPermanentDetailsRepository;

import com.annular.filmhook.service.DetailService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;
import com.annular.filmhook.webmodel.PlatformDetailDTO;
import com.annular.filmhook.webmodel.ProfessionDetailDTO;
import com.annular.filmhook.webmodel.SubProfessionsWebModel;
import com.annular.filmhook.webmodel.UserWebModel;
import com.annular.filmhook.webmodel.PlatformDetailsWebModel;

@Service
public class DetailServiceImpl implements DetailService {

    @Autowired
    IndustryRepository industryRepository;

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    UserMediaFilesService userMediaFilesService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformRepository platformRepository;

    @Autowired
    PlatformPermanentDetailRepository platformPermanentDetailRepository;

    @Autowired
    IndustryUserPermanentDetailsRepository industryUserPermanentDetailsRepository;

    @Autowired
    IndustryTemporaryDetailRepository industryTemporaryDetailsRepository;

    @Autowired
    FileUtil fileUtil;

    @Autowired
    UserService userService;

    @Autowired
    UserMediaFilesService userMediaFileService;

    @Autowired
    PlatformDetailRepository platformDetailsRepository;

    @Autowired
    FilmProfessionDetailRepository filmProfessionDetailRepository;

    @Autowired
    FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

    @Autowired
    IndustryDetailRepository industryDetailsRepository;

    @Autowired
    FilmSubProfessionDetailRepository filmSubProfessionDetailRepository;

    @Autowired
    UserDetails userDetails;

    @Autowired
    FilmProfessionRepository filmProfessionRepository;

    @Autowired
    S3Util s3Util;

    @Autowired
    FilmSubProfessionRepository filmSubProfessionRepository;

    @Autowired
    FilmSubProfessionPermanentDetailsRepository filmSubProfessionPermanentDetailsRepository;

    public static final Logger logger = LoggerFactory.getLogger(DetailServiceImpl.class);

    @Override
    public ResponseEntity<?> getDetails(DetailRequest detailRequest) {
        try {
            Map<String, List<?>> details = new HashMap<>();

            if (detailRequest.isIndustries()) {
                List<Map<String, Object>> industryDetails = new ArrayList<>();
                List<Industry> industries = industryRepository.findAll();
                for (Industry industry : industries) {
                    Map<String, Object> industryMap = new HashMap<>();
                    industryMap.put("industryId", industry.getIndustryId());
                    industryMap.put("industryName", industry.getIndustryName());
                    // industryMap.put("industryImage", industry.getImage());
                    industryMap.put("logo_file_path", industry.getFilePath());
                    industryDetails.add(industryMap);
                }
                details.put("industries", industryDetails);
            }

            if (detailRequest.isPlatforms()) {
                List<Map<String, Object>> platformDetails = new ArrayList<>();
                List<Platform> platforms = platformRepository.findAll();
                for (Platform platform : platforms) {
                    Map<String, Object> platformMap = new HashMap<>();
                    platformMap.put("platformId", platform.getPlatformId());
                    platformMap.put("platformName", platform.getPlatformName());
                    platformMap.put("logo_file_path", platform.getFilePath());
                    platformDetails.add(platformMap);
                }
                details.put("platform", platformDetails);
            }

            if (detailRequest.isProfessions()) {
                List<Map<String, Object>> professionDetails = new ArrayList<>();
                List<FilmProfession> professions = filmProfessionRepository.findAll();
                for (FilmProfession profession : professions) {
                    Map<String, Object> professionMap = new HashMap<>();
                    professionMap.put("professionId", profession.getFilmProfessionId());
                    professionMap.put("professionName", profession.getProfessionName());
                    professionMap.put("logo_file_path", profession.getFilePath());
                    professionDetails.add(professionMap);
                }
                details.put("professions", professionDetails);
            }

            if (detailRequest.isSubProfessions()) {
                List<Map<String, Object>> subProfessionDetails = new ArrayList<>();
                List<FilmSubProfession> subProfessions = filmSubProfessionRepository.findAll();
                for (FilmSubProfession subProfession : subProfessions) {
                    Map<String, Object> subProfessionMap = new HashMap<>();
                    subProfessionMap.put("subProfessionId", subProfession.getSubProfessionId());
                    subProfessionMap.put("subProfessionName", subProfession.getSubProfessionName());
                    subProfessionDetails.add(subProfessionMap);
                }
                details.put("subProfessions", subProfessionDetails);
            }

            return ResponseEntity.ok(details);
        } catch (Exception e) {
            logger.error("getDetails Service Method Exception: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @Override
    public ResponseEntity<?> addTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            // Extract data from the IndustryTemporaryWebModel object
            List<String> industriesName = industryTemporaryWebModel.getIndustriesName();
            List<String> platformName = industryTemporaryWebModel.getPlatformName();
            List<String> professionName = industryTemporaryWebModel.getProfessionName();
            List<String> subProfessionName = industryTemporaryWebModel.getSubProfessionName();
            Integer userId = industryTemporaryWebModel.getUserId();

            // Save details to IndustryTemporaryDetails
            IndustryTemporaryDetails tempDetails = new IndustryTemporaryDetails();
            tempDetails.setIndustriesname(String.join(",", industriesName));
            tempDetails.setPlatformname(String.join(",", platformName));
            tempDetails.setProfessionname(String.join(",", professionName));
            tempDetails.setSubProfessionname(String.join(",", subProfessionName));
            tempDetails.setUserId(userId);
            IndustryTemporaryDetails savedTempDetails = industryTemporaryDetailsRepository.save(tempDetails);

            // Save details to PlatformDetails
            for (String platform : platformName) {
                PlatformDetails platformDetails = new PlatformDetails();
                platformDetails.setIndustryTemporaryDetailId(savedTempDetails.getItId());
                platformDetails.setPlatformName(platform);
                platformDetails.setUserId(userId);
                platformDetailsRepository.save(platformDetails);
            }

            // Save details to ProfessionDetails
            for (String prof : professionName) {
                FilmProfessionDetails filmProfessionDetails = new FilmProfessionDetails();
                filmProfessionDetails.setProfessionTemporaryDetailId(savedTempDetails.getItId());
                filmProfessionDetails.setProfessionName(prof);
                filmProfessionDetails.setUserId(userId);
                filmProfessionDetailRepository.save(filmProfessionDetails);
            }

            // Save details to IndustryDetails
            for (String industry : industriesName) {
                IndustryDetails industryDetails = new IndustryDetails();
                industryDetails.setIndustryTemporaryDetailId(savedTempDetails.getItId());
                industryDetails.setIndustry_name(industry);
                industryDetails.setUserId(userId);
                industryDetailsRepository.save(industryDetails);
            }

            // Save details to SubProfessionDetails
            for (String subProf : subProfessionName) {
                FilmSubProfessionDetails filmSubProfessionDetails = new FilmSubProfessionDetails();
                filmSubProfessionDetails.setIndustryTemporaryDetailId(savedTempDetails.getItId());
                filmSubProfessionDetails.setSubProfessionName(subProf);
                filmSubProfessionDetails.setUserId(userId);
                filmSubProfessionDetailRepository.save(filmSubProfessionDetails);
            }

            // Log the received data
            logger.info("Received request with industries: {}, platforms: {}, professions: {}, subProfessions: {}, userId: {}", industriesName, platformName, professionName, subProfessionName, userId);

            return ResponseEntity.ok("Temporary details added successfully");
        } catch (Exception e) {
            // Handle any exceptions that occur during processing
            logger.error("addTemporaryDetails Service Method Exception: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @Override
    public ResponseEntity<?> getTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            List<IndustryTemporaryDetails> temporaryDetailsList = industryTemporaryDetailsRepository.findByUserId(industryTemporaryWebModel.getUserId());
            Map<String, Object> response = new HashMap<>();

            for (IndustryTemporaryDetails tempDetails : temporaryDetailsList) {
                // Create a separate industry map for each industry
                Map<String, Object> industryMap = new HashMap<>();
                List<String> industriesName = Arrays.asList(tempDetails.getIndustriesname().split(","));

                for (String industryName : industriesName) {
                    // Create a separate platform list for each industry
                    List<Map<String, Object>> platformList = new ArrayList<>();
                    List<PlatformDetails> platformDetailsList = platformDetailsRepository.findByIndustryTemporaryDetailId(tempDetails.getItId());

                    for (PlatformDetails platformDetails : platformDetailsList) {
                        Map<String, Object> platformMap = new HashMap<>();
                        platformMap.put("platformName", platformDetails.getPlatformName());

                        // Add professions for the platform
                        List<FilmProfessionDetails> filmProfessionDetailsList = filmProfessionDetailRepository.findByProfessionTemporaryDetailId(tempDetails.getItId());
                        List<String> professions = new ArrayList<>();
                        for (FilmProfessionDetails filmProfessionDetails : filmProfessionDetailsList) {
                            professions.add(filmProfessionDetails.getProfessionName());
                        }
                        platformMap.put("professions", professions);

                        // Add sub-professions for the platform
                        List<FilmSubProfessionDetails> filmSubProfessionDetailsList = filmSubProfessionDetailRepository.findByIndustryTemporaryDetailId(tempDetails.getItId());
                        List<String> subProfessions = new ArrayList<>();
                        for (FilmSubProfessionDetails filmSubProfessionDetails : filmSubProfessionDetailsList) {
                            subProfessions.add(filmSubProfessionDetails.getSubProfessionName());
                        }
                        platformMap.put("subProfessions", subProfessions);

                        platformList.add(platformMap);
                    }

                    // Add the platform list to the industry map
                    industryMap.put("platforms", platformList);

                    // Add the industry map to the response using the industry name as the key
                    response.put(industryName, industryMap);
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching temporary details.");
        }
    }

//    @Override
//    public ResponseEntity<?> addIndustryUserPermanentDetails(Integer userId, List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
//        try {
//            if(userDetails.userInfo() != null && userDetails.userInfo().getId().equals(userId)) {
//                Map<String, String> responseMap = new HashMap<>();
//                StringBuilder unknownIndustries = new StringBuilder();
//                for (IndustryUserPermanentDetailWebModel industryUserPermanentDetailWebModel : industryUserPermanentDetailWebModels) {
//                    // Find the industry by name
//                    Industry industry = industryRepository.findByIndustryName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase()).orElse(null);
//                    if (industry == null) {
//                        unknownIndustries.append(industryUserPermanentDetailWebModel.getIndustriesName()).append(" ");
//                        responseMap.put("error", "Unknown industry(s) found -> [ " + unknownIndustries + " ].\nThese industry and its details are not available in the master data. Please add a valid industry...");
//                        continue;
//                    }
//                    // Create IndustryPermanentDetails object
//                    IndustryUserPermanentDetails industryPermanentDetails = new IndustryUserPermanentDetails();
//                    industryPermanentDetails.setIndustriesName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase());
//                    industryPermanentDetails.setUserId(userId); // Set userId from method parameter
//                    industryPermanentDetails.setIndustry(industry);
//                    industryPermanentDetails.setCreatedBy(userId);
//                    industryPermanentDetails.setCreatedOn(new Date());
//                    industryPermanentDetails.setStatus(true);
//                    // Save the IndustryPermanentDetails object
//                    IndustryUserPermanentDetails savedIndustryUserPermanentDetails = industryUserPermanentDetailsRepository.saveAndFlush(industryPermanentDetails);
//
//                    // Iterate over platform details
//                    for (PlatformDetailsWebModel platformDetail : industryUserPermanentDetailWebModel.getPlatformDetails()) {
//                        // Find the platform by name
//                        Platform platform = platformRepository.findByPlatformName(platformDetail.getPlatformName().toUpperCase()).orElse(null);
//                        if (platform == null) continue;
//                        // Create PlatformPermanentDetail object
//                        PlatformPermanentDetail platformPermanentDetail = new PlatformPermanentDetail();
//                        platformPermanentDetail.setPlatformName(platformDetail.getPlatformName().toUpperCase());
//                        platformPermanentDetail.setUserId(userId);
//                        platformPermanentDetail.setIndustryUserPermanentDetails(savedIndustryUserPermanentDetails);
//                        platformPermanentDetail.setPlatform(platform);
//                        // Save the PlatformPermanentDetail object
//                        PlatformPermanentDetail savedPlatformPermanentDetail = platformPermanentDetailRepository.saveAndFlush(platformPermanentDetail);
//
//                        // Iterate over profession details for this platform
//                        for (ProfessionDetailDTO professionDetail : platformDetail.getProfessionDetails()) {
//                            // Find the profession by name
//                            FilmProfession profession = filmProfessionRepository.findByProfessionName(professionDetail.getProfessionName().toUpperCase()).orElse(null);
//                            if (profession == null) continue;
//                            // Create ProfessionPermanentDetail object
//                            FilmProfessionPermanentDetail filmProfessionPermanentDetail = new FilmProfessionPermanentDetail();
//                            filmProfessionPermanentDetail.setProfessionName(professionDetail.getProfessionName().toUpperCase());
//                            filmProfessionPermanentDetail.setProfessionName(professionDetail.getProfessionName());
//                            filmProfessionPermanentDetail.setIndustryUserPermanentDetails(savedIndustryUserPermanentDetails);
//                            filmProfessionPermanentDetail.setPlatformPermanentDetail(savedPlatformPermanentDetail);
//                            filmProfessionPermanentDetail.setFilmProfession(profession);
//                            filmProfessionPermanentDetail.setUserId(userId);
//                            // Save the ProfessionPermanentDetail object
//                            FilmProfessionPermanentDetail savedFilmProfessionPermanentDetail = filmProfessionPermanentDetailRepository.saveAndFlush(filmProfessionPermanentDetail);
//
//                            // Iterate over sub profession details for this profession
//                            for (String subProfessionInput : professionDetail.getSubProfessionName()) {
//                                // Find the sub-profession by name
//                                FilmSubProfession subProfession = filmSubProfessionRepository.findBySubProfessionName(subProfessionInput.toUpperCase()).orElse(null);
//                                if (subProfession == null) continue;
//                                // sub-profession
//                                FilmSubProfessionPermanentDetail subProfessionPermanentDetails = FilmSubProfessionPermanentDetail.builder()
//                                        .professionName(subProfessionInput.toUpperCase())
//                                        .userId(userId)
//                                        .industryUserPermanentDetails(savedIndustryUserPermanentDetails)
//                                        .platformPermanentDetail(savedPlatformPermanentDetail)
//                                        .filmProfessionPermanentDetail(savedFilmProfessionPermanentDetail)
//                                        .ppdProfessionId(0)
//                                        .filmSubProfession(subProfession)
//                                        .build();
//                                filmSubProfessionPermanentDetailsRepository.saveAndFlush(subProfessionPermanentDetails);
//                            }
//                        }
//                    }
//                }
//                industryTemporaryDetailsRepository.deleteByUserId(userId);
//                industryDetailsRepository.deleteByUserId(userId);
//                platformDetailsRepository.deleteByUserId(userId);
//                filmProfessionDetailRepository.deleteByUserId(userId);
//                filmSubProfessionDetailRepository.deleteByUserId(userId);
//
//                if (responseMap.isEmpty()) {
//                    // Return a success response
//                    return ResponseEntity.ok("Industry user permanent details added successfully.");
//                } else {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap.get("error"));
//                }
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provided user is not a valid user...");
//            }
//        } catch (Exception e) {
//            // Return an error response if an exception occurs
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add industry user permanent details.");
//        }
//    }
    @Override
    public ResponseEntity<?> addIndustryUserPermanentDetails(Integer userId, List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
        try {
            Map<String, String> responseMap = new HashMap<>();
            StringBuilder unknownIndustries = new StringBuilder();
            for (IndustryUserPermanentDetailWebModel industryUserPermanentDetailWebModel : industryUserPermanentDetailWebModels) {
                // Find the industry by name
                Industry industry = industryRepository.findByIndustryName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase()).orElse(null);
                if (industry == null) {
                    unknownIndustries.append(industryUserPermanentDetailWebModel.getIndustriesName()).append(" ");
                    responseMap.put("error", "Unknown industry(s) found -> [ " + unknownIndustries + " ].\nThese industry and its details are not available in the master data. Please add a valid industry...");
                    continue;
                }
                // Create IndustryPermanentDetails object
                IndustryUserPermanentDetails industryPermanentDetails = new IndustryUserPermanentDetails();
                industryPermanentDetails.setIndustriesName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase());
                industryPermanentDetails.setUserId(userId); // Set userId from method parameter
                industryPermanentDetails.setIndustry(industry);
                industryPermanentDetails.setCreatedBy(userId);
                industryPermanentDetails.setCreatedOn(new Date());
                industryPermanentDetails.setStatus(true);
                // Save the IndustryPermanentDetails object
                IndustryUserPermanentDetails savedIndustryUserPermanentDetails = industryUserPermanentDetailsRepository.saveAndFlush(industryPermanentDetails);

                // Iterate over platform details
                for (PlatformDetailsWebModel platformDetail : industryUserPermanentDetailWebModel.getPlatformDetails()) {
                    // Find the platform by name
                    Platform platform = platformRepository.findByPlatformName(platformDetail.getPlatformName().toUpperCase()).orElse(null);
                    if (platform == null) continue;
                    // Create PlatformPermanentDetail object
                    PlatformPermanentDetail platformPermanentDetail = new PlatformPermanentDetail();
                    platformPermanentDetail.setPlatformName(platformDetail.getPlatformName().toUpperCase());
                    platformPermanentDetail.setUserId(userId);
                    platformPermanentDetail.setIndustryUserPermanentDetails(savedIndustryUserPermanentDetails);
                    platformPermanentDetail.setPlatform(platform);
                    // Save the PlatformPermanentDetail object
                    PlatformPermanentDetail savedPlatformPermanentDetail = platformPermanentDetailRepository.saveAndFlush(platformPermanentDetail);

                    // Iterate over profession details for this platform
                    for (ProfessionDetailDTO professionDetail : platformDetail.getProfessionDetails()) {
                        // Find the profession by name
                        FilmProfession profession = filmProfessionRepository.findByProfessionName(professionDetail.getProfessionName().toUpperCase()).orElse(null);
                        if (profession == null) continue;
                        // Create ProfessionPermanentDetail object
                        FilmProfessionPermanentDetail filmProfessionPermanentDetail = new FilmProfessionPermanentDetail();
                        filmProfessionPermanentDetail.setProfessionName(professionDetail.getProfessionName().toUpperCase());
                        filmProfessionPermanentDetail.setProfessionName(professionDetail.getProfessionName());
                        filmProfessionPermanentDetail.setIndustryUserPermanentDetails(savedIndustryUserPermanentDetails);
                        filmProfessionPermanentDetail.setPlatformPermanentDetail(savedPlatformPermanentDetail);
                        filmProfessionPermanentDetail.setFilmProfession(profession);
                        filmProfessionPermanentDetail.setUserId(userId);
                        // Save the ProfessionPermanentDetail object
                        FilmProfessionPermanentDetail savedFilmProfessionPermanentDetail = filmProfessionPermanentDetailRepository.saveAndFlush(filmProfessionPermanentDetail);

                        // Iterate over sub profession details for this profession
                        for (String subProfessionInput : professionDetail.getSubProfessionName()) {
                            // Find the sub-profession by name
                            FilmSubProfession subProfession = filmSubProfessionRepository.findBySubProfessionName(subProfessionInput.toUpperCase()).orElse(null);
                            if (subProfession == null) continue;
                            // sub-profession
                            FilmSubProfessionPermanentDetail subProfessionPermanentDetails = FilmSubProfessionPermanentDetail.builder()
                                    .professionName(subProfessionInput.toUpperCase())
                                    .userId(userId)
                                    .industryUserPermanentDetails(savedIndustryUserPermanentDetails)
                                    .platformPermanentDetail(savedPlatformPermanentDetail)
                                    .filmProfessionPermanentDetail(savedFilmProfessionPermanentDetail)
                                    .ppdProfessionId(0)
                                    .filmSubProfession(subProfession)
                                    .build();
                            filmSubProfessionPermanentDetailsRepository.saveAndFlush(subProfessionPermanentDetails);
                        }
                    }
                }
            }
            industryTemporaryDetailsRepository.deleteByUserId(userId);
            industryDetailsRepository.deleteByUserId(userId);
            platformDetailsRepository.deleteByUserId(userId);
            filmProfessionDetailRepository.deleteByUserId(userId);
            filmSubProfessionDetailRepository.deleteByUserId(userId);

            if (responseMap.isEmpty()) {
                // Return a success response
                return ResponseEntity.ok("Industry user permanent details added successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap.get("error"));
            }
        } catch (Exception e) {
            // Return an error response if an exception occurs
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add industry user permanent details.");
        }
    }


    @Override
    public List<FileOutputWebModel> saveIndustryUserFiles(IndustryFileInputWebModel inputFileData) {
        List<FileOutputWebModel> fileOutputWebModelList = null;
        try {
            Optional<User> userFromDB = userService.getUser(inputFileData.getUserId());
            if (userFromDB.isPresent()) {
                logger.info("User found: {}", userFromDB.get().getName());
                fileOutputWebModelList = userMediaFileService.saveMediaFiles(inputFileData, userFromDB.get()); // Save
                // media files in MySQL
                fileOutputWebModelList.sort(Comparator.comparing(FileOutputWebModel::getId));
            }
        } catch (Exception e) {
            logger.error("Error at saveIndustryUserFiles(): ", e);
            e.printStackTrace();
        }
        return fileOutputWebModelList;
    }

    @Override
    public ResponseEntity<?> updateTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            // Extract data from the IndustryTemporaryWebModel object
            List<String> industriesName = industryTemporaryWebModel.getIndustriesName();
            List<String> platformName = industryTemporaryWebModel.getPlatformName();
            List<String> professionName = industryTemporaryWebModel.getProfessionName();
            List<String> subProfessionName = industryTemporaryWebModel.getSubProfessionName();
            Integer userId = industryTemporaryWebModel.getUserId();
            Integer temporaryId = industryTemporaryWebModel.getItId(); // Assuming you have a method to get temporary ID

            // Delete existing temporary details
            industryTemporaryDetailsRepository.deleteById(temporaryId);
            filmProfessionDetailRepository.deleteByProfessionTemporaryDetailId(temporaryId);
            filmSubProfessionDetailRepository.deleteByIndustryTemporaryDetailId(temporaryId);
            industryDetailsRepository.deleteByIndustryTemporaryDetailId(temporaryId);
            platformDetailsRepository.deleteByIndustryTemporaryDetailId(temporaryId);

            // Save new details
            IndustryTemporaryDetails tempDetails = new IndustryTemporaryDetails();
            tempDetails.setIndustriesname(String.join(",", industriesName));
            tempDetails.setPlatformname(String.join(",", platformName));
            tempDetails.setProfessionname(String.join(",", professionName));
            tempDetails.setSubProfessionname(String.join(",", subProfessionName));
            tempDetails.setUserId(userId);
            IndustryTemporaryDetails savedTempDetails = industryTemporaryDetailsRepository.save(tempDetails);

            // Save details to PlatformDetails
            for (String platform : platformName) {
                PlatformDetails platformDetails = new PlatformDetails();
                platformDetails.setIndustryTemporaryDetailId(savedTempDetails.getItId());
                platformDetails.setPlatformName(platform);
                platformDetailsRepository.save(platformDetails);
            }

            // Save details to ProfessionDetails
            for (String prof : professionName) {
                FilmProfessionDetails filmProfessionDetails = new FilmProfessionDetails();
                filmProfessionDetails.setProfessionTemporaryDetailId(savedTempDetails.getItId());
                filmProfessionDetails.setProfessionName(prof);
                filmProfessionDetailRepository.save(filmProfessionDetails);
            }

            // Save details to IndustryDetails
            for (String industry : industriesName) {
                IndustryDetails industryDetails = new IndustryDetails();
                industryDetails.setIndustryTemporaryDetailId(savedTempDetails.getItId());
                industryDetails.setIndustry_name(industry);
                industryDetailsRepository.save(industryDetails);
            }

            // Save details to SubProfessionDetails
            for (String subProf : subProfessionName) {
                FilmSubProfessionDetails filmSubProfessionDetails = new FilmSubProfessionDetails();
                filmSubProfessionDetails.setIndustryTemporaryDetailId(savedTempDetails.getItId());
                filmSubProfessionDetails.setSubProfessionName(subProf);
                filmSubProfessionDetailRepository.save(filmSubProfessionDetails);
            }

            // Log the received data
            logger.info("Updated temporary details with temporaryId: {}, industries: {}, platforms: {}, professions: {}, subProfessions: {}, userId: {}", temporaryId, industriesName, platformName, professionName, subProfessionName, userId);
            return ResponseEntity.ok("Temporary details updated successfully");
        } catch (Exception e) {
            // Handle any exceptions that occur during processing
            logger.error("updateTemporaryDetails Service Method Exception: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @Override
    public ResponseEntity<?> getTemporaryDuplicateDetails(IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            List<IndustryTemporaryDetails> temporaryDetailsList = industryTemporaryDetailsRepository.findByUserId(industryTemporaryWebModel.getUserId());
            Map<String, Object> response = new HashMap<>();

            for (IndustryTemporaryDetails tempDetails : temporaryDetailsList) {
                // Create a separate industry map for each industry
                Map<String, Object> industryMap = new HashMap<>();
                List<String> industriesName = Arrays.asList(tempDetails.getIndustriesname().split(","));

                for (String industryName : industriesName) {
                    // Create a separate platform list for each industry
                    List<Map<String, Object>> platformList = new ArrayList<>();
                    List<PlatformDetails> platformDetailsList = platformDetailsRepository.findByIndustryTemporaryDetailId(tempDetails.getItId());

                    for (PlatformDetails platformDetails : platformDetailsList) {
                        Map<String, Object> platformMap = new HashMap<>();
                        platformMap.put("platformName", platformDetails.getPlatformName());

                        // Add professions for the platform
                        List<Map<String, Object>> professionsList = new ArrayList<>();
                        List<FilmProfessionDetails> professionDetailsList = filmProfessionDetailRepository.findByProfessionTemporaryDetailId(tempDetails.getItId());

                        // Retrieve all distinct profession names for this platform
                        Set<String> distinctProfessions = professionDetailsList.stream().map(FilmProfessionDetails::getProfessionName).collect(Collectors.toSet());
                        for (String professionName : distinctProfessions) {
                            // Retrieve SubProfessionDetails matching the professionName and integerTemporaryDetailId
                            List<FilmSubProfessionDetails> subProfessionDetailsList = filmSubProfessionDetailRepository.findByIndustryTemporaryDetailIdAndProfessionName(tempDetails.getItId());
                            List<String> subProfessions = new ArrayList<>();
                            // Add sub-professions
                            for (FilmSubProfessionDetails subProfessionDetails : subProfessionDetailsList) {
                                subProfessions.add(subProfessionDetails.getSubProfessionName());
                            }

                            // Check if professionName exists in FilmProfession table
                            FilmProfession filmProfession = filmProfessionRepository.findByProfessionName(professionName).orElse(null);
                            if (filmProfession != null) {
                                // Get sub-professions associated with the profession from FilmProfession table
                                List<String> filmSubProfessions = filmProfession.getFilmSubProfessionCollection().stream().map(FilmSubProfession::getSubProfessionName).collect(Collectors.toList());

                                // Filter sub-professions based on those associated with the profession
                                List<String> filteredSubProfessions = subProfessions.stream().filter(filmSubProfessions::contains).collect(Collectors.toList());

                                // Create professionMap only if there are filtered sub-professions
                                if (!filteredSubProfessions.isEmpty()) {
                                    Map<String, Object> professionMap = new HashMap<>();
                                    professionMap.put("professionName", professionName);
                                    professionMap.put("subProfessionName", filteredSubProfessions);
                                    professionsList.add(professionMap);
                                }
                            } else {
                                // Create professionMap for the profession even if it's not found in
                                // FilmProfession table
                                Map<String, Object> professionMap = new HashMap<>();
                                professionMap.put("professionName", professionName);
                                professionMap.put("subProfessionName", subProfessions);
                                professionsList.add(professionMap);
                            }
                        }

                        platformMap.put("professions", professionsList); // Add professions list to platformMap
                        platformList.add(platformMap); // Add platformMap to platformList
                    }

                    // Add the platform list to the industry map
                    industryMap.put("platforms", platformList);

                    // Add the industry map to the response using the industry name as the key
                    response.put(industryName, industryMap);
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching temporary details.");
        }
    }

    @Override
    public List<FileOutputWebModel> getIndustryFiles(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            outputWebModelList = userMediaFilesService.getMediaFilesByUserAndCategory(userId);
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public Resource getIndustryFile(Integer userId, String category, String fileId) {
        try {
            Optional<User> userFromDB = userService.getUser(userId);
            if (userFromDB.isPresent()) {
                String filePath = FileUtil.generateFilePath(userFromDB.get(), category, fileId);
                return new ByteArrayResource(fileUtil.downloadFile(filePath));
            }
        } catch (Exception e) {
            logger.error("Error at getIndustryFile()...", e);
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public ResponseEntity<?> getIndustryUserPermanentDetails(Integer userId) {
        try {
            // Fetch platform details
            List<PlatformPermanentDetail> platformDetails = platformPermanentDetailRepository.findByUserId(userId);

            List<Map<String, Object>> responseList = new ArrayList<>();
            Set<String> processedPlatforms = new HashSet<>(); // To store processed platform names

            for (PlatformPermanentDetail detail : platformDetails) {
                String platformName = detail.getPlatformName();

                // Skip processing if the platform has already been processed
                if (processedPlatforms.contains(platformName)) {
                    continue;
                }

                // Mark the platform as processed
                processedPlatforms.add(platformName);

                // Fetch platform image
                Optional<Platform> platformOptional = platformRepository.findByPlatformName(platformName);
                if (platformOptional.isPresent()) {
                    Platform platform = platformOptional.get();
                    Map<String, Object> platformMap = new HashMap<>();
                    platformMap.put("platformName", platformName);
                    /*if (platform.getImage() != null) {
                        String base64Image = Base64.getEncoder().encodeToString(platform.getImage());
                        platformMap.put("platformImage", base64Image);
                    } else {
                        // Handle case when platform image is not found
                        platformMap.put("image", "default_image_url");
                    }*/

                    // Fetch industries for the platform
                    Set<Map<String, String>> industries = new HashSet<>();
                    for (PlatformPermanentDetail platformDetail : platformDetails) {
                        if (platformDetail.getPlatformName().equals(platformName)) {
                            Map<String, String> industryMap = new HashMap<>();
                            String industryName = platformDetail.getIndustryUserPermanentDetails().getIndustriesName();

                            // Fetch industry image
                            Optional<Industry> industryOptional = industryRepository.findByIndustryName(industryName);
                            /*if (industryOptional.isPresent()) {
                                Industry industry = industryOptional.get();
                                String base64Image = Base64.getEncoder().encodeToString(industry.getImage());
                                industryMap.put("industryimage", base64Image);
                            } else {
                                // Handle case when industry is not found
                                industryMap.put("image", "default_image_url");
                            }*/
                            industryMap.put("industryName", industryName);
                            industries.add(industryMap);
                        }
                    }
                    platformMap.put("industries", industries);

                    // Add other platform details
                    platformMap.put("platformPermanentId", detail.getPlatformPermanentId());
                    platformMap.put("filmCount", detail.getFilmCount());
                    platformMap.put("netWorth", detail.getNetWorth());
                    platformMap.put("dailySalary", detail.getDailySalary());

                    // Fetch media files
                    List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByUserIdAndCategoryAndRefId(userId, MediaFileCategory.Project, detail.getPlatformPermanentId());
                    platformMap.put("outputWebModelList", outputWebModelList);

                    // Fetch professions
                    List<Map<String, Object>> professionsList = new ArrayList<>();
                    for (FilmProfessionPermanentDetail professionDetail : detail.getProfessionDetails()) {
                        Map<String, Object> professionMap = new HashMap<>();
                        String professionName = professionDetail.getProfessionName();

//                        List<String> subProfessions = new ArrayList<>();
//                        for (FilmSubProfessionPermanentDetail subProfession : professionDetail.getFilmSubProfessionPermanentDetails()) {
//                            subProfessions.add(subProfession.getFilmSubProfession().getSubProfessionName());
//                        }

                        List<Map<String, Object>> subProfessionsList = new ArrayList<>();
                        for (FilmSubProfessionPermanentDetail subProfession : professionDetail.getFilmSubProfessionPermanentDetails()) {
                            Map<String, Object> subProfessionMap = new HashMap<>();
                            subProfessionMap.put("subProfessionName", subProfession.getFilmSubProfession().getSubProfessionName());
                            subProfessionMap.put("subProfessionId", subProfession.getProfessionPermanentId());
                            subProfessionMap.put("startingYear", subProfession.getStartingYear());
                            subProfessionMap.put("endingYear", subProfession.getEndingYear());
                            subProfessionsList.add(subProfessionMap);
                        }

                        professionMap.put("professionName", professionName);
                        professionMap.put("subProfessions", subProfessionsList);

                        professionsList.add(professionMap);
                    }

                    platformMap.put("professions", professionsList);
                    responseList.add(platformMap);
                }
            }

            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve platform details.");
        }
    }

//    @Override
//    public ResponseEntity<?> updateIndustryUserPermanentDetails(PlatformDetailDTO platformDetailDTO) {
//        try {
//            Optional<PlatformPermanentDetail> optionalPermanentDetails = platformPermanentDetailRepository.findById(platformDetailDTO.getPlatformPermanentId());
//            if (optionalPermanentDetails.isPresent()) {
//                PlatformPermanentDetail permanentDb = optionalPermanentDetails.get();
//                permanentDb.setDailySalary(platformDetailDTO.getDailySalary());
//                permanentDb.setFilmCount(platformDetailDTO.getFilmCount());
//                permanentDb.setNetWorth(platformDetailDTO.getNetWorth());
//                platformPermanentDetailRepository.save(permanentDb);
//            }
//            return ResponseEntity.ok("Industry user permanent details updated successfully.");
//        } catch (Exception e) {
//            logger.error("Error in updating industry user permanent details", e);
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update industry user permanent details.");
//        }
//    }

    @Override
    public ResponseEntity<?> updateIndustryUserPermanentDetails(PlatformDetailDTO platformDetailDTO) {
        try {
            Optional<PlatformPermanentDetail> optionalPermanentDetails = platformPermanentDetailRepository.findById(platformDetailDTO.getPlatformPermanentId());
            if (optionalPermanentDetails.isPresent()) {
                PlatformPermanentDetail permanentDb = optionalPermanentDetails.get();
                permanentDb.setDailySalary(platformDetailDTO.getDailySalary());
                permanentDb.setFilmCount(platformDetailDTO.getFilmCount());
                permanentDb.setNetWorth(platformDetailDTO.getNetWorth());

                for (SubProfessionsWebModel subProfessionDTO : platformDetailDTO.getSubProfession()) {
                    Optional<FilmSubProfessionPermanentDetail> optionalSubProfession = filmSubProfessionPermanentDetailsRepository.findById(subProfessionDTO.getSubProfessionId());
                    if (optionalSubProfession.isPresent()) {
                        FilmSubProfessionPermanentDetail subProfessionDetail = optionalSubProfession.get();
                        subProfessionDetail.setStartingYear(subProfessionDTO.getStartingYear());
                        subProfessionDetail.setEndingYear(subProfessionDTO.getEndingYear());
                        filmSubProfessionPermanentDetailsRepository.save(subProfessionDetail);
                    } else {
                        // Handle case when the sub-profession is not found
                        logger.error("Sub-profession with ID {} not found", subProfessionDTO.getSubProfessionId());
                    }
                }

                platformPermanentDetailRepository.save(permanentDb);
            }
            return ResponseEntity.ok("Industry user permanent details updated successfully.");
        } catch (Exception e) {
            logger.error("Error in updating industry user permanent details", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update industry user permanent details.");
        }
    }

    @Override
    public ResponseEntity<?> updateIndustryUserPermanentDetails(Integer userId, List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
        try {
            // Iterate through the provided industry user permanent detail web models
            for (IndustryUserPermanentDetailWebModel industryUserPermanentDetailWebModel : industryUserPermanentDetailWebModels) {
                // Check if an industry with the same name already exists for the user
                Optional<IndustryUserPermanentDetails> existingIndustryOptional = industryUserPermanentDetailsRepository.findByUserIdAndIndustriesName(userId, industryUserPermanentDetailWebModel.getIndustriesName());
                if (existingIndustryOptional.isPresent()) {
                    IndustryUserPermanentDetails existingIndustry = existingIndustryOptional.get();
                    // Check if platform details are different
                    List<PlatformPermanentDetail> existingPlatformDetails = existingIndustry.getPlatformDetails();
                    List<PlatformDetailsWebModel> newPlatformDetails = industryUserPermanentDetailWebModel.getPlatformDetails();
                    if (!arePlatformDetailsEqual(existingPlatformDetails, newPlatformDetails)) {
                        // Delete existing platform and profession details associated with the existing industry
                        platformPermanentDetailRepository.deleteByIndustryUserPermanentDetailsId(existingIndustry.getIupdId());
                        // Update industry user permanent details with the new platform and profession details
                        existingIndustry.getPlatformDetails().clear(); // Clear existing platform details
                        for (PlatformDetailsWebModel platformDetail : newPlatformDetails) {
                            PlatformPermanentDetail platformPermanentDetail = new PlatformPermanentDetail();
                            platformPermanentDetail.setPlatformName(platformDetail.getPlatformName().toUpperCase());
                            platformPermanentDetail.setIndustryUserPermanentDetails(existingIndustry);
                            platformPermanentDetail.setUserId(userId);
                            PlatformPermanentDetail savedPlatformPermanentDetail = platformPermanentDetailRepository.save(platformPermanentDetail);
                            for (ProfessionDetailDTO professionDetail : platformDetail.getProfessionDetails()) {
                                FilmProfessionPermanentDetail savedProfession = new FilmProfessionPermanentDetail();
                                savedProfession.setProfessionName(professionDetail.getProfessionName().toUpperCase());
                                savedProfession.setIndustryUserPermanentDetails(existingIndustry);
                                savedProfession.setPlatformPermanentDetail(savedPlatformPermanentDetail);
                                filmProfessionPermanentDetailRepository.save(savedProfession);
                            }
                        }
                        // Update existing industry user permanent details
                        existingIndustry.setIndustriesName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase());
                        industryUserPermanentDetailsRepository.save(existingIndustry);
                    }
                    industryTemporaryDetailsRepository.deleteByUserId(userId);
                    industryDetailsRepository.deleteByUserId(userId);
                    platformDetailsRepository.deleteByUserId(userId);
                    filmProfessionDetailRepository.deleteByUserId(userId);
                    filmSubProfessionDetailRepository.deleteByUserId(userId);
                } else {
                    Industry industry = industryRepository.findByIndustryName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase()).orElse(null);

                    // Create new industry user permanent details if it doesn't exist
                    IndustryUserPermanentDetails newIndustryPermanentDetails = new IndustryUserPermanentDetails();
                    newIndustryPermanentDetails.setIndustriesName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase());
                    newIndustryPermanentDetails.setUserId(userId);
                    newIndustryPermanentDetails.setIndustry(industry);
                    IndustryUserPermanentDetails savedIndustryUserPermanentDetails = industryUserPermanentDetailsRepository.save(newIndustryPermanentDetails);

                    for (PlatformDetailsWebModel platformDetail : industryUserPermanentDetailWebModel.getPlatformDetails()) {
                        Platform platform = platformRepository.findByPlatformName(platformDetail.getPlatformName().toUpperCase()).orElse(null);
                        PlatformPermanentDetail platformPermanentDetails = new PlatformPermanentDetail();
                        platformPermanentDetails.setPlatformName(platformDetail.getPlatformName().toUpperCase());
                        platformPermanentDetails.setIndustryUserPermanentDetails(savedIndustryUserPermanentDetails);
                        platformPermanentDetails.setUserId(userId);
                        platformPermanentDetails.setPlatform(platform);
                        PlatformPermanentDetail savedPlatformPermanentDetail = platformPermanentDetailRepository.save(platformPermanentDetails);

                        for (ProfessionDetailDTO professionDetail : platformDetail.getProfessionDetails()) {
                            FilmProfession filmProfession = filmProfessionRepository.findByProfessionName(professionDetail.getProfessionName().toUpperCase()).orElse(null);
                            FilmProfessionPermanentDetail filmProfessionPermanentDetail = new FilmProfessionPermanentDetail();
                            filmProfessionPermanentDetail.setProfessionName(professionDetail.getProfessionName().toUpperCase());
                            filmProfessionPermanentDetail.setIndustryUserPermanentDetails(savedIndustryUserPermanentDetails);
                            filmProfessionPermanentDetail.setPlatformPermanentDetail(savedPlatformPermanentDetail);
                            filmProfessionPermanentDetail.setFilmProfession(filmProfession);
                            FilmProfessionPermanentDetail savedFilmProfessionPermanentDetail = filmProfessionPermanentDetailRepository.save(filmProfessionPermanentDetail);

                            professionDetail.getSubProfessionName().stream()
                                    .filter(Objects::nonNull)
                                    .forEach(subProfessionName -> {
                                        FilmSubProfession filmSubProfession = filmSubProfessionRepository.findBySubProfessionName(subProfessionName).orElse(null);
                                        FilmSubProfessionPermanentDetail subProfessionPermanentDetails = FilmSubProfessionPermanentDetail
                                                .builder()
                                                .industryUserPermanentDetails(savedIndustryUserPermanentDetails)
                                                .platformPermanentDetail(savedPlatformPermanentDetail)
                                                .filmProfessionPermanentDetail(savedFilmProfessionPermanentDetail)
                                                .filmSubProfession(filmSubProfession)
                                                .build();
                                        filmSubProfessionPermanentDetailsRepository.saveAndFlush(subProfessionPermanentDetails);
                                    });
                        }
                    }
                    industryTemporaryDetailsRepository.deleteByUserId(userId);
                    industryDetailsRepository.deleteByUserId(userId);
                    platformDetailsRepository.deleteByUserId(userId);
                    filmProfessionDetailRepository.deleteByUserId(userId);
                    filmSubProfessionDetailRepository.deleteByUserId(userId);
                }
            }

            // Return a success response
            return ResponseEntity.ok().body("Industry user permanent details updated successfully.");

        } catch (Exception e) {
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update industry user permanent details.");
        }
    }

    // Helper method to check if platform details are equal
    private boolean arePlatformDetailsEqual(List<PlatformPermanentDetail> existingPlatformDetails, List<PlatformDetailsWebModel> newPlatformDetails) {
        // Compare sizes
        if (existingPlatformDetails.size() != newPlatformDetails.size()) {
            return false;
        }
        // Compare each platform detail
        for (PlatformPermanentDetail existingPlatform : existingPlatformDetails) {
            return newPlatformDetails.stream().filter(Objects::nonNull).anyMatch(data -> !data.getPlatformName().equalsIgnoreCase(existingPlatform.getPlatformName()));
        }
        return true;
    }

    @Override
    public ResponseEntity<?> verifyFilmHookCode(UserWebModel userWebModel) {
        Optional<User> filmHookData = userRepository.findByFilmHookCode(userWebModel.getFilmHookCode());
        if (filmHookData.isPresent()) {
            User user = filmHookData.get();
            int minRange = 1000;
            int maxRange = 9999;
            int otpNumber = (int) (Math.random() * (maxRange - minRange + 1) + minRange);
            user.setFilmHookOtp(otpNumber);
            userRepository.save(user);

            boolean sendVerificationRes = sendVerificationEmail(user);
            if (sendVerificationRes) {
                // If email sent successfully, return success response
                return ResponseEntity.ok("Verification email sent successfully.");
            } else {
                // If email sending failed, return error response
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification email.");
            }
        } else {
            // Handle case where filmHookData is not present
            return ResponseEntity.notFound().build();
        }
    }

    public boolean sendVerificationEmail(User user) {
        boolean response = true;
        try {
            if (user.getFilmHookOtp() == null) {
                throw new IllegalArgumentException("OTP is null");
            }

            String subject = "Verify Your EmailID";
            String senderName = "FilmHook";
            String senderEmail = "filmhookapps@gmail.com"; // Replace with your valid email address
            String mailContent = "<p>Hello " + user.getName() + ",</p>";
            mailContent += "<p>Please use the following OTP to verify your fimHookCode on FilmHook:</p>";
            mailContent += "<h3>" + user.getFilmHookOtp() + "</h3>";
            mailContent += "<p>Thank You<br>FilmHook</p>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(senderEmail, senderName);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(mailContent, true);

            javaMailSender.send(message);
        } catch (IllegalArgumentException e) {
            // Handle case where OTP is null
            // log.error("OTP is null for user: {}", user.getId());
            response = false;
        } catch (Exception e) {
            // Handle other exceptions
            // log.error("Failed to send verification email for user: {}", user.getId(), e);
            response = false;
        }
        return response;
    }

    @Override
    public ResponseEntity<?> verifyFilmHook(UserWebModel userWebModel) {
        try {
            List<User> userList = userRepository.findAll();
            boolean emailOtpVerified = false;
            for (User user : userList) {
                if (user.getFilmHookOtp() != null && user.getFilmHookOtp().equals(userWebModel.getFilmHookOtp())) {
                    user.setStatus(true);
                    userRepository.save(user);
                    emailOtpVerified = true;
                    break;
                }
            }

            if (emailOtpVerified) {
                return ResponseEntity.ok(new Response(1, "Email OTP verified successfully", ""));
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid Email OTP", ""));
            }

        } catch (Exception e) {
            logger.error("Error verifying email OTP: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Failed to verify email OTP", ""));
        }
    }

    @Override
    public ResponseEntity<?> getIndustryByuserId() {
        try {
            Integer userId = userDetails.userInfo().getId();
            List<IndustryUserPermanentDetails> industryDetails = industryUserPermanentDetailsRepository.findByUserId(userId);

            Map<String, Object> response = new HashMap<>();
            if (industryDetails.isEmpty()) {
                response.put("message", "No industry data found for the user");
                return ResponseEntity.status(404).body(response);
            }

            Set<Map<String, Object>> industryData = industryDetails.stream()
                    .map(detail -> {
                        Map<String, Object> industryMap = new HashMap<>();
                        industryMap.put("industryName", detail.getIndustriesName());
                        industryMap.put("image", detail.getIndustry().getFilePath() != null ? s3Util.generateS3FilePath(detail.getIndustry().getFilePath()) : null);

                        return industryMap;
                    })
                    .collect(Collectors.toSet());

            response.put("industryData", industryData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error retrieving industry names");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

	


