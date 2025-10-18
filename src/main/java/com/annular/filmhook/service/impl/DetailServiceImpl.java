package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Country;
import com.annular.filmhook.model.FileStatus;
import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmProfessionDetails;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.FilmSubProfessionDetails;
import com.annular.filmhook.model.FilmSubProfessionPermanentDetail;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.IndustryDetails;
import com.annular.filmhook.model.IndustryTemporaryDetails;
import com.annular.filmhook.model.IndustryUserPermanentDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.PlatformDetails;
import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.ShootingLocationImages;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.CountryRepository;
import com.annular.filmhook.repository.FilmProfessionDetailRepository;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.repository.FilmSubProfessionDetailRepository;
import com.annular.filmhook.repository.FilmSubProfessionPermanentDetailsRepository;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.IndustryDetailRepository;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.IndustryTemporaryDetailRepository;
import com.annular.filmhook.repository.IndustryUserPermanentDetailsRepository;
import com.annular.filmhook.repository.PlatformDetailRepository;
import com.annular.filmhook.repository.PlatformPermanentDetailRepository;
import com.annular.filmhook.repository.PlatformRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.DetailService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;
import com.annular.filmhook.webmodel.PlatformDetailDTO;
import com.annular.filmhook.webmodel.PlatformDetailsWebModel;
import com.annular.filmhook.webmodel.ProfessionDetailDTO;
import com.annular.filmhook.webmodel.ShootingLocationWebModal;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.SubProfessionsWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

@Service
public class DetailServiceImpl implements DetailService {

    @Autowired
    IndustryRepository industryRepository;

    @Autowired
    MediaFilesService mediaFilesService;
    
    

    @Autowired
    private MailNotification mailNotification;

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
    CountryRepository countryRepository;

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
                    industryMap.put("code", industry.getStateCode());
                    // industryMap.put("industryImage", industry.getImage());
                    industryMap.put("logo_file_path", s3Util.generateS3FilePath(industry.getFilePath()));
                    
                    // Retrieve the country for the industry
                    Country country = countryRepository.findById(industry.getCountry().getId())
                            .orElse(null);  // Handle case where countryId doesn't exist

                    if (country != null) {
                        // Take the filePath from the Country table
                        industryMap.put("country_logo_file_path", s3Util.generateS3FilePath(country.getFilePath()));
                    } else {
                        // Fall back to industry filePath if no matching country is found
                        industryMap.put("country_logo_file_path", s3Util.generateS3FilePath(industry.getFilePath()));
                    }
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
                    platformMap.put("logo_file_path", s3Util.generateS3FilePath(platform.getFilePath()));
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
                    professionMap.put("logo_file_path", s3Util.generateS3FilePath(profession.getFilePath()));
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
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
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
            return ResponseEntity.internalServerError().body("Error occurred while fetching temporary details.");
        }
    }

    @Override
    public ResponseEntity<?> addIndustryUserPermanentDetails(Integer userId, List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
        try {
            //if(userDetails.userInfo() != null && userDetails.userInfo().getId().equals(userId)) {
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
                    platformPermanentDetail.setStatus(true);
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
                        filmProfessionPermanentDetail.setStatus(true);
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
                                    .status(true)
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
            //} else {
            //    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provided user is not a valid user...");
            //}
        } catch (Exception e) {
            // Return an error response if an exception occurs
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to add industry user permanent details.");
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
    public List<FileOutputWebModel> saveIndustryUserFiless(IndustryFileInputWebModel inputFileData) {
        List<FileOutputWebModel> fileOutputWebModelList = null;
        try {
            Optional<User> userFromDB = userService.getUser(inputFileData.getUserId());
            if (userFromDB.isPresent()) {
                logger.info("User found: {}", userFromDB.get().getName());
                fileOutputWebModelList = userMediaFileService.saveMediaFiless(inputFileData, userFromDB.get()); // Save
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
    public List<FileOutputWebModel> saveIndustryUserFilesss(IndustryFileInputWebModel inputFileData) {
        List<FileOutputWebModel> fileOutputWebModelList = null;
        try {
            Optional<User> userFromDB = userService.getUser(inputFileData.getUserId());
            if (userFromDB.isPresent()) {
                logger.info("User found: {}", userFromDB.get().getName());
                fileOutputWebModelList = userMediaFileService.saveMediaFilesss(inputFileData, userFromDB.get()); // Save
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
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
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
            return ResponseEntity.internalServerError().body("Error occurred while fetching temporary details.");
        }
    }

    @Override
    public List<FileOutputWebModel> getIndustryFiles(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            outputWebModelList = userMediaFilesService.getMediaFilesByUserAndCategory(userId);
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser() -> {}", e.getMessage());
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
            logger.error("Error at getIndustryFile() -> {}", e.getMessage());
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
                    if (platform.getImage() != null) {
                        //String base64Image = Base64.getEncoder().encodeToString(platform.getImage());
                        platformMap.put("platformImage", !Utility.isNullOrBlankWithTrim(platform.getFilePath()) ? s3Util.generateS3FilePath(platform.getFilePath()) : "");
                    } else {
                        // Handle case when platform image is not found
                        platformMap.put("image", "default_image_url");
                    }

                    // Fetch industries for the platform
                    Set<Map<String, String>> industries = new HashSet<>();
                    for (PlatformPermanentDetail platformDetail : platformDetails) {
                        if (platformDetail.getPlatformName().equals(platformName)) {
                            Map<String, String> industryMap = new HashMap<>();
                            String industryName = platformDetail.getIndustryUserPermanentDetails().getIndustriesName();

                            // Fetch industry image
                            Optional<Industry> industryOptional = industryRepository.findByIndustryName(industryName);
                            if (industryOptional.isPresent()) {
                                Industry industry = industryOptional.get();
                                //String base64Image = Base64.getEncoder().encodeToString(industry.getImage());
                                industryMap.put("industryimage", !Utility.isNullOrBlankWithTrim(industry.getFilePath()) ? s3Util.generateS3FilePath(industry.getFilePath()) : "");
                            } else {
                                // Handle case when industry is not found
                                industryMap.put("image", "default_image_url");
                            }
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
                    List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByUserIdAndCategoryAndRefIdAndStatus(userId, MediaFileCategory.Project, detail.getPlatformPermanentId(), FileStatus.APPROVED);
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

                        // Fetch the profession entity from the database based on professionName
                        Optional<FilmProfession> professionOptional = filmProfessionRepository.findByProfessionName(professionName);
                        if (professionOptional.isPresent()) {
                            FilmProfession profession = professionOptional.get();

                            // Get the icon file path from the profession entity

                            professionMap.put("professionName", professionName);
                            professionMap.put("professionIcon", !Utility.isNullOrBlankWithTrim(profession.getFilePath()) ? s3Util.generateS3FilePath(profession.getFilePath()) : ""); // Adding icon file path to the response

                            // Fetching sub-professions and other details can continue as before
                            // ...
                        } else {
                            // Handle case when profession is not found
                            // You can choose to skip this profession or handle it according to your requirements
                            professionMap.put("professionName", professionName);
                            professionMap.put("professionIcon", "default_icon_file_path"); // Default icon file path if profession is not found
                            // ...
                        }
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
            return ResponseEntity.internalServerError().body("Failed to retrieve platform details.");
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
//            return ResponseEntity.internalServerError().body("Failed to update industry user permanent details.");
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

                List<SubProfessionsWebModel> subProfessions = platformDetailDTO.getSubProfession();
                if (subProfessions != null) {
                    for (SubProfessionsWebModel subProfessionDTO : subProfessions) {
                        Optional<FilmSubProfessionPermanentDetail> optionalSubProfession = filmSubProfessionPermanentDetailsRepository.findById(subProfessionDTO.getSubProfessionId());
                        if (optionalSubProfession.isPresent()) {
                            FilmSubProfessionPermanentDetail subProfessionDetail = optionalSubProfession.get();
                            subProfessionDetail.setStartingYear(subProfessionDTO.getStartingYear());
                            subProfessionDetail.setEndingYear(subProfessionDTO.getEndingYear());
                            filmSubProfessionPermanentDetailsRepository.save(subProfessionDetail);

                            // Calculate the total experience for the user
                            List<FilmSubProfessionPermanentDetail> details = filmSubProfessionPermanentDetailsRepository.findByUserId(userDetails.userInfo().getId());
                            int totalExperience = details.stream()
                                    .mapToInt(data -> data.getEndingYear() - data.getStartingYear())
                                    .sum();

                            // Fetch the user and update the experience
                            Optional<User> userOptional = userRepository.findById(userDetails.userInfo().getId());
                            if (userOptional.isPresent()) {
                                User userToUpdate = userOptional.get();
                                userToUpdate.setExperience(totalExperience); // Assuming there's a setExperience method
                                userRepository.save(userToUpdate);
                            } else {
                                return ResponseEntity.ok().body("User not found.");
                            }
                        } else {
                            // Handle case when the sub-profession is not found
                            logger.error("Sub-profession with ID {} not found", subProfessionDTO.getSubProfessionId());
                        }
                    }
                } else {
                    logger.warn("No sub-professions provided in the update request.");
                }

                platformPermanentDetailRepository.save(permanentDb);
                return ResponseEntity.ok("Industry user permanent details updated successfully.");
            } else {
                return ResponseEntity.ok().body("Platform details not found.");
            }
        } catch (Exception e) {
            logger.error("Error in updating industry user permanent details {} ", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to update industry user permanent details.");
        }
    }


    @Override
    public ResponseEntity<?> updateIndustryUserPermanentDetails(Integer userId, List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
        try {
            for (IndustryUserPermanentDetailWebModel industryUserPermanentDetailWebModel : industryUserPermanentDetailWebModels) {
                Optional<IndustryUserPermanentDetails> existingIndustryOptional = industryUserPermanentDetailsRepository.findByUserIdAndIndustriesName(userId, industryUserPermanentDetailWebModel.getIndustriesName());

                if (existingIndustryOptional.isPresent()) {
                    IndustryUserPermanentDetails existingIndustry = existingIndustryOptional.get();

                    List<PlatformPermanentDetail> existingPlatformDetails = existingIndustry.getPlatformDetails();
                    List<PlatformDetailsWebModel> newPlatformDetails = industryUserPermanentDetailWebModel.getPlatformDetails();

                    if (!arePlatformDetailsEqual(existingPlatformDetails, newPlatformDetails)) {
                        // Delete existing sub-professions and professions
                        for (PlatformPermanentDetail platformPermanentDetail : existingPlatformDetails) {
                            filmSubProfessionPermanentDetailsRepository.deleteByPlatformPermanentDetailId(platformPermanentDetail.getPlatformPermanentId());
                            filmProfessionPermanentDetailRepository.deleteByPlatformPermanentDetailId(platformPermanentDetail.getPlatformPermanentId());
                        }

                        // Delete existing platform details
                        platformPermanentDetailRepository.deleteByIndustryUserPermanentDetailsId(existingIndustry.getIupdId());
                        existingIndustry.getPlatformDetails().clear();

                        // Add new platform details
                        for (PlatformDetailsWebModel platformDetail : newPlatformDetails) {
                            Platform platform = platformRepository.findByPlatformName(platformDetail.getPlatformName().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Platform not found"));

                            PlatformPermanentDetail platformPermanentDetail = new PlatformPermanentDetail();
                            platformPermanentDetail.setPlatformName(platformDetail.getPlatformName().toUpperCase());
                            platformPermanentDetail.setIndustryUserPermanentDetails(existingIndustry);
                            platformPermanentDetail.setUserId(userId);
                            platformPermanentDetail.setPlatform(platform); // Ensure the platform is set

                            PlatformPermanentDetail savedPlatformPermanentDetail = platformPermanentDetailRepository.save(platformPermanentDetail);

                            // Process professions and sub-professions
                            for (ProfessionDetailDTO professionDetail : platformDetail.getProfessionDetails()) {
                                FilmProfession filmProfession = filmProfessionRepository.findByProfessionName(professionDetail.getProfessionName().toUpperCase()).orElse(null);

                                FilmProfessionPermanentDetail savedProfession = new FilmProfessionPermanentDetail();
                                savedProfession.setProfessionName(professionDetail.getProfessionName().toUpperCase());
                                savedProfession.setIndustryUserPermanentDetails(existingIndustry);
                                savedProfession.setPlatformPermanentDetail(savedPlatformPermanentDetail);
                                savedProfession.setFilmProfession(filmProfession);
                                FilmProfessionPermanentDetail savedFilmProfessionPermanentDetail = filmProfessionPermanentDetailRepository.save(savedProfession);

                                // Handle sub-professions
                                for (String subProfessionName : professionDetail.getSubProfessionName()) {
                                    if (subProfessionName != null) {
                                        FilmSubProfession filmSubProfession = filmSubProfessionRepository.findBySubProfessionName(subProfessionName).orElse(null);
                                        FilmSubProfessionPermanentDetail subProfessionPermanentDetails = FilmSubProfessionPermanentDetail.builder()
                                                .industryUserPermanentDetails(existingIndustry)
                                                .platformPermanentDetail(savedPlatformPermanentDetail)
                                                .filmProfessionPermanentDetail(savedFilmProfessionPermanentDetail)
                                                .filmSubProfession(filmSubProfession)
                                                .status(true)
                                                .build();
                                        filmSubProfessionPermanentDetailsRepository.saveAndFlush(subProfessionPermanentDetails);
                                    }
                                }
                            }
                        }

                        existingIndustry.setIndustriesName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase());
                        industryUserPermanentDetailsRepository.save(existingIndustry);
                    }

                    // Clear temporary and related details
                    industryTemporaryDetailsRepository.deleteByUserId(userId);
                    industryDetailsRepository.deleteByUserId(userId);
                    platformDetailsRepository.deleteByUserId(userId);
                    filmProfessionDetailRepository.deleteByUserId(userId);
                    filmSubProfessionDetailRepository.deleteByUserId(userId);

                } else {
                    Industry industry = industryRepository.findByIndustryName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase()).orElse(null);

                    IndustryUserPermanentDetails newIndustryPermanentDetails = new IndustryUserPermanentDetails();
                    newIndustryPermanentDetails.setIndustriesName(industryUserPermanentDetailWebModel.getIndustriesName().toUpperCase());
                    newIndustryPermanentDetails.setUserId(userId);
                    newIndustryPermanentDetails.setIndustry(industry);
                    IndustryUserPermanentDetails savedIndustryUserPermanentDetails = industryUserPermanentDetailsRepository.save(newIndustryPermanentDetails);

                    for (PlatformDetailsWebModel platformDetail : industryUserPermanentDetailWebModel.getPlatformDetails()) {
                        Platform platform = platformRepository.findByPlatformName(platformDetail.getPlatformName().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Platform not found"));

                        PlatformPermanentDetail platformPermanentDetails = new PlatformPermanentDetail();
                        platformPermanentDetails.setPlatformName(platformDetail.getPlatformName().toUpperCase());
                        platformPermanentDetails.setIndustryUserPermanentDetails(savedIndustryUserPermanentDetails);
                        platformPermanentDetails.setUserId(userId);
                        platformPermanentDetails.setPlatform(platform); // Ensure the platform is set

                        PlatformPermanentDetail savedPlatformPermanentDetail = platformPermanentDetailRepository.save(platformPermanentDetails);

                        // Process professions and sub-professions
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
                                                .status(true)
                                                .build();
                                        filmSubProfessionPermanentDetailsRepository.saveAndFlush(subProfessionPermanentDetails);
                                    });
                        }
                    }

                    // Clear temporary and related details
                    industryTemporaryDetailsRepository.deleteByUserId(userId);
                    industryDetailsRepository.deleteByUserId(userId);
                    platformDetailsRepository.deleteByUserId(userId);
                    filmProfessionDetailRepository.deleteByUserId(userId);
                    filmSubProfessionDetailRepository.deleteByUserId(userId);
                }
            }

            return ResponseEntity.ok().body("Industry user permanent details updated successfully.");
        } catch (Exception e) {
            logger.error("Failed to update industry user permanent details for userId: {}", userId, e);
            return ResponseEntity.internalServerError().body("Failed to update industry user permanent details.");
        }
    }

    // Helper method to check if platform details are equal
    private boolean arePlatformDetailsEqual(List<PlatformPermanentDetail> existingPlatformDetails, List<PlatformDetailsWebModel> newPlatformDetails) {
        if (existingPlatformDetails.size() != newPlatformDetails.size()) {
            return false;
        }
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

            boolean sendVerificationRes = mailNotification.sendVerificationFilmHookOTP(user);
            if (sendVerificationRes) {
                // If email sent successfully, return success response
                return ResponseEntity.ok("Verification email sent successfully.");
            } else {
                // If email sending failed, return error response
                return ResponseEntity.internalServerError().body("Failed to send verification email.");
            }
        } else {
            // Handle case where filmHookData is not present
            return ResponseEntity.notFound().build();
        }
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
            	Integer userId = userWebModel.getUserId();
            	Optional<User> user = userRepository.findById(userId);
            	if (user.isPresent()) {
                    User users = user.get();
                    users.setRefCode(userWebModel.getRefCode());
                    userRepository.save(users);
                }
                return ResponseEntity.ok(new Response(1, "Email OTP verified successfully", ""));
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid Email OTP", ""));
            }

        } catch (Exception e) {
            logger.error("Error verifying email OTP: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to verify email OTP", ""));
        }
    }

    @Override
    public ResponseEntity<?> getIndustryByuserId(Integer userId) {
        try {
            //Integer userId = userDetails.userInfo().getId();
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
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving industry names", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> deleteTemporaryDetails(Integer userId) {
        try {
            // Delete records from FilmSubProfessionDetails by userId
            filmSubProfessionDetailRepository.deleteByUserId(userId);

            // Delete records from FilmProfessionDetails by userId
            filmProfessionDetailRepository.deleteByUserId(userId);

            // Delete records from IndustryDetails by userId
            industryDetailsRepository.deleteByUserId(userId);

            // Delete records from PlatformDetails by userId
            platformDetailsRepository.deleteByUserId(userId);

            // Delete the main IndustryTemporaryDetails record by userId
            industryTemporaryDetailsRepository.deleteByUserId(userId);

            // Log deletion success
            logger.info("Temporary details deleted successfully for userId: {}", userId);

            return ResponseEntity.ok("Temporary details deleted successfully");
        } catch (Exception e) {
            // Handle any exceptions that occur during deletion
            logger.error("deleteTemporaryDetails Service Method Exception: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", "Could not delete temporary details"));
        }
    }





}

	


