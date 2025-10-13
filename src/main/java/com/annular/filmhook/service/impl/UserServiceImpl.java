package com.annular.filmhook.service.impl;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.*;
import com.annular.filmhook.repository.*;

import com.annular.filmhook.service.BookingService;
import com.annular.filmhook.service.MediaFilesService;

import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.util.MailNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.UserWebModel;
import com.annular.filmhook.webmodel.AddressListWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.UserSearchWebModel;
import com.annular.filmhook.webmodel.IndustryWebModel;
import com.annular.filmhook.webmodel.LocationWebModel;
import com.annular.filmhook.webmodel.ProfessionWebModel;
import com.annular.filmhook.webmodel.SubProfessionWebModel;
import com.annular.filmhook.webmodel.BookingWebModel;
import com.annular.filmhook.webmodel.ExperienceDTO;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalendarUtil calendarUtil;

    @Autowired
    AddressListRepository addressListRepository;

    @Autowired
    S3Util s3Util;

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    UserService userService;

    @Autowired
    private MailNotification mailNotification;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    IndustryRepository industryRepository;

    @Autowired
    IndustryUserPermanentDetailsRepository industryPermanentDetailsRepository;

    @Autowired
    PlatformPermanentDetailRepository platformPermanentDetailRepository;

    @Autowired
    FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

    @Autowired
    FilmSubProfessionRepository filmSubProfessionRepository;

    @Autowired
    FilmSubProfessionPermanentDetailsRepository filmSubProfessionPermanentDetailsRepository;

    @Autowired
    PlatformFilmProfessionMapRepository platformFilmProfessionMapRepository;

    @Autowired
    BookingService bookingService;
    
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    FriendRequestRepository friendRequestRepository;

    @Override
    public List<UserWebModel> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(user -> user.getStatus().equals(true))
                .map(this::transformUserObjToUserWebModelObj)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserWebModel> getUserByUserId(Integer userId) {
        UserWebModel user = null;
        Optional<?> dbUser = userRepository.getUserByUserId(userId);
        if (dbUser.isPresent())
            user = this.transformUserObjToUserWebModelObj((User) dbUser.get());
        return Optional.ofNullable(user);
    }

    public UserWebModel transformUserObjToUserWebModelObj(User user) {
    	// Assuming `FilmSubProfessionRepository` has a method to fetch experience for a user
    	Optional<ExperienceDTO> experience = filmSubProfessionPermanentDetailsRepository.calculateExperienceForUser(user.getUserId());


    	UserWebModel userWebModel = new UserWebModel();

        userWebModel.setUserId(user.getUserId());

        userWebModel.setEmail(user.getEmail());
        userWebModel.setUserType(user.getUserType());

        userWebModel.setName(user.getName());
        userWebModel.setAdminReview(user.getAdminReview());
        
        // ✅ Followers Count (users who follow this user)
        int followersCount = friendRequestRepository.countByFollowersRequestReceiverIdAndFollowersRequestIsActive(
                user.getUserId(), true
        );
        userWebModel.setFollowersListCount(followersCount);

        // ✅ Following Count (users this user follows)
        int followingCount = friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestIsActive(
                user.getUserId(), true
        ).size();
        userWebModel.setFollowingListCount(followingCount);
        
        if (!Utility.isNullOrBlankWithTrim(user.getDob())) {
            userWebModel.setDob(user.getDob());
            //userWebModel.setAge(calendarUtil.getAgeFromDate(user.getDob(), CalendarUtil.MYSQL_DATE_FORMAT));
        }
        userWebModel.setGender(user.getGender());
        userWebModel.setCountry(user.getCountry());
        userWebModel.setState(user.getState());
        userWebModel.setDistrict(user.getDistrict());
        userWebModel.setPhoneNumber(user.getPhoneNumber());
        userWebModel.setCurrentAddress(user.getCurrentAddress());
        userWebModel.setHomeAddress(user.getHomeAddress());
        userWebModel.setBirthPlace(user.getBirthPlace());
        userWebModel.setLivingPlace(user.getLivingPlace());
        userWebModel.setSchedule(user.getSchedule());
        userWebModel.setCountryCode(user.getCountryCode());
        //userWebModel.setExperience(user.getExperience());
        
     // Set experience based on calculated experience, defaulting to 0 if null
        if (experience.isPresent() && experience.get().getTotalExperienceYears() != null) {
            userWebModel.setExperience(experience.get().getTotalExperienceYears());
        } else {
            userWebModel.setExperience(0); // Default to 0 if experience or years are null
        }


//     // Set experience based on calculated experience, defaulting to 0 if null
//     userWebModel.setExperience(experience.isPresent() ? experience.get().getTotalExperienceYears() : 0);
// // Set experience
        userWebModel.setBust(user.getBust());
        userWebModel.setHeightUnit(user.getHeightUnit());
        userWebModel.setWeightUnit(user.getWeightUnit());
        userWebModel.setHip(user.getHip());

        userWebModel.setHeight(user.getHeight());
        userWebModel.setWeight(user.getWeight());
        userWebModel.setSkinTone(user.getSkinTone());
        userWebModel.setHairColor(user.getHairColor());
        userWebModel.setBmi(user.getBmi());
        userWebModel.setChestSize(user.getChestSize());
        userWebModel.setWaistSize(user.getWaistSize());
        userWebModel.setBicepsSize(user.getBiceps());

        userWebModel.setReligion(user.getReligion());
        //userWebModel.setCaste(user.getCaste());
        userWebModel.setMaritalStatus(user.getMaritalStatus());
        userWebModel.setSpouseName(user.getSpouseName());
        if (!Utility.isNullOrBlankWithTrim(user.getChildrenNames())) userWebModel.setChildrenNames(new ArrayList<>(Arrays.asList(user.getChildrenNames().split(","))));

        userWebModel.setMotherName(user.getMotherName());
        userWebModel.setFatherName(user.getFatherName());
        if (!Utility.isNullOrBlankWithTrim(user.getBrotherNames())) userWebModel.setBrotherNames(new ArrayList<>(Arrays.asList(user.getBrotherNames().split(","))));
        if (!Utility.isNullOrBlankWithTrim(user.getSisterNames())) userWebModel.setSisterNames(new ArrayList<>(Arrays.asList(user.getSisterNames().split(","))));

        userWebModel.setSchoolName(user.getSchoolName());
        userWebModel.setCollegeName(user.getCollegeName());
        userWebModel.setQualification(user.getQualification());

        userWebModel.setWorkCategory(user.getWorkCategory());

        userWebModel.setStatus(user.getStatus());

        userWebModel.setCreatedBy(user.getCreatedBy());
        userWebModel.setCreatedOn(user.getCreatedOn());
        userWebModel.setUpdatedBy(user.getUpdatedBy());
        userWebModel.setUpdateOn(user.getUpdatedOn());

        userWebModel.setProfilePicOutput(this.getProfilePic(UserWebModel.builder().userId(user.getUserId()).build()));

        List<FileOutputWebModel> coverPicList = mediaFilesService.getMediaFilesByCategoryAndUserId(
                MediaFileCategory.CoverPic, user.getUserId()
        );
        if (!Utility.isNullOrEmptyList(coverPicList)) {
            userWebModel.setCoverPhotoOutput(coverPicList);  // ✅ set full list
        } else {
            userWebModel.setCoverPhotoOutput(new ArrayList<>()); // empty list to avoid null
        }


        String dateString = "";
        LocalDate finalDate = LocalDate.now();
        List<BookingWebModel> userBookings = bookingService.getConfirmedBookingsByUserId(user.getUserId());
        if (!Utility.isNullOrEmptyList(userBookings) && !CalendarUtil.isPastDate(userBookings.get(0).getToDate())) {
            finalDate = CalendarUtil.getNextDate(userBookings.get(0).getToDate());
        }
        dateString = CalendarUtil.getFormatedDateString(finalDate, CalendarUtil.UI_DATE_FORMAT);
        userWebModel.setBookingAvailableDate(dateString);

        return userWebModel;
    }

    @Override
    public Optional<User> getUser(Integer userId) {
        User user = null;
        Optional<?> dbUser = userRepository.getUserByUserId(userId);
        if (dbUser.isPresent())
            user = (User) dbUser.get();
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<?> updateBiographyData(UserWebModel userWebModel) {
        Optional<User> user;
        try {
            user = userRepository.getUserByUserId(userWebModel.getUserId());
            if (user.isPresent()) {
                this.prepareUserBiographyData(userWebModel, user.get());
                userRepository.save(user.get());
                return user;
            }
        } catch (Exception e) {
            logger.error("Error occurred at updateBiographyData() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserBiographyData(UserWebModel userInput, User userToUpdate) {
        if (!Utility.isNullOrBlankWithTrim(userInput.getDob())) userToUpdate.setDob(userInput.getDob());
        if (!Utility.isNullOrBlankWithTrim(userInput.getGender())) userToUpdate.setGender(userInput.getGender());
        if (!Utility.isNullOrBlankWithTrim(userInput.getBirthPlace())) userToUpdate.setBirthPlace(userInput.getBirthPlace());
        if (!Utility.isNullOrBlankWithTrim(userInput.getLivingPlace())) userToUpdate.setLivingPlace(userInput.getLivingPlace());
        
        if (!Utility.isNullOrBlankWithTrim(userInput.getCountry())) userToUpdate.setCountry(userInput.getCountry());
        if (!Utility.isNullOrBlankWithTrim(userInput.getState())) userToUpdate.setState(userInput.getState());
        if (!Utility.isNullOrBlankWithTrim(userInput.getDistrict())) userToUpdate.setDistrict(userInput.getDistrict());
        if (!Utility.isNullOrBlankWithTrim(userInput.getPhoneNumber())) userToUpdate.setPhoneNumber(userInput.getPhoneNumber());
        if (!Utility.isNullOrBlankWithTrim(userInput.getCurrentAddress())) userToUpdate.setCurrentAddress(userInput.getCurrentAddress());
        if (!Utility.isNullOrBlankWithTrim(userInput.getHomeAddress())) userToUpdate.setHomeAddress(userInput.getHomeAddress());

        userToUpdate.setUpdatedBy(userToUpdate.getUserId());
        userToUpdate.setUpdatedOn(new Date());
    }
    @Override
    public Optional<?> updateBiologicalData(UserWebModel userWebModel) {
        Optional<User> user;
        try {
            user = userRepository.getUserByUserId(userWebModel.getUserId());
            if (user.isPresent()) {
                this.prepareUserBiologicalData(userWebModel, user.get());
                userRepository.save(user.get());
                return user;
            }
        } catch (Exception e) {
            logger.error("Error occurred at updateBiologicalData() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserBiologicalData(UserWebModel userInput, User userToUpdate) {

        if (!Utility.isNullOrBlankWithTrim(userInput.getHeight())) userToUpdate.setHeight(userInput.getHeight()); //+ "Cm");
        if (!Utility.isNullOrBlankWithTrim(userInput.getWeight())) userToUpdate.setWeight(userInput.getWeight()); //+ "Kg");
        if (!Utility.isNullOrBlankWithTrim(userInput.getSkinTone())) userToUpdate.setSkinTone(userInput.getSkinTone());
        if (!Utility.isNullOrBlankWithTrim(userInput.getHairColor())) userToUpdate.setHairColor(userInput.getHairColor());
        if (!Utility.isNullOrBlankWithTrim(userInput.getBmi())) userToUpdate.setBmi(userInput.getBmi());
        if (!Utility.isNullOrBlankWithTrim(userInput.getChestSize())) userToUpdate.setChestSize(userInput.getChestSize()); //+ "in");
        if (!Utility.isNullOrBlankWithTrim(userInput.getWaistSize())) userToUpdate.setWaistSize(userInput.getWaistSize()); //+ "in");
        if (!Utility.isNullOrBlankWithTrim(userInput.getBicepsSize())) userToUpdate.setBiceps(userInput.getBicepsSize());// + "in");
        if(!Utility.isNullOrBlankWithTrim(userInput.getWeightUnit())) userToUpdate.setWeightUnit(userInput.getWeightUnit());
        if(!Utility.isNullOrBlankWithTrim(userInput.getHeightUnit())) userToUpdate.setHeightUnit(userInput.getHeightUnit());
        if(!Utility.isNullOrBlankWithTrim(userInput.getBust())) userToUpdate.setBust(userInput.getBust());
        if(!Utility.isNullOrBlankWithTrim(userInput.getHip())) userToUpdate.setHip(userInput.getHip());
        userToUpdate.setUpdatedBy(userToUpdate.getUserId());
        userToUpdate.setUpdatedOn(new Date());
    }

    @Override
    public Optional<?> updatePersonalInformation(UserWebModel userWebModel) {
        Optional<User> user;
        try {
            user = userRepository.getUserByUserId(userWebModel.getUserId());
            if (user.isPresent()) {
                this.prepareUserPersonalInfo(userWebModel, user.get());
                userRepository.save(user.get());
                return user;
            }
        } catch (Exception e) {
            logger.error("Error occurred at updatePersonalInformation() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserPersonalInfo(UserWebModel userInput, User userToUpdate) {

        if (!Utility.isNullOrBlankWithTrim(userInput.getReligion())) userToUpdate.setReligion(userInput.getReligion());
        //if (!Utility.isNullOrBlankWithTrim(userInput.getCaste())) userToUpdate.setCaste(userInput.getCaste());
        if (!Utility.isNullOrBlankWithTrim(userInput.getFatherName())) userToUpdate.setFatherName(userInput.getFatherName());
        if (!Utility.isNullOrBlankWithTrim(userInput.getMotherName())) userToUpdate.setMotherName(userInput.getMotherName());
        if (!Utility.isNullOrEmptyList(userInput.getBrotherNames())) userToUpdate.setBrotherNames(String.join(",", userInput.getBrotherNames()));
        if (!Utility.isNullOrEmptyList(userInput.getSisterNames())) userToUpdate.setSisterNames(String.join(",", userInput.getSisterNames()));
        if (!Utility.isNullOrBlankWithTrim(userInput.getMaritalStatus())) userToUpdate.setMaritalStatus(userInput.getMaritalStatus());
        if (!Utility.isNullOrBlankWithTrim(userInput.getSpouseName())) userToUpdate.setSpouseName(userInput.getSpouseName());
        if (!Utility.isNullOrEmptyList(userInput.getChildrenNames())) userToUpdate.setChildrenNames(String.join(",", userInput.getChildrenNames()));

        userToUpdate.setUpdatedBy(userToUpdate.getUserId());
        userToUpdate.setUpdatedOn(new Date());
    }

    @Override
    public Optional<?> updateEducationInformation(UserWebModel userWebModel) {
        Optional<User> user;
        try {
            user = userRepository.getUserByUserId(userWebModel.getUserId());
            if (user.isPresent()) {
                this.prepareUserEducationalInfo(userWebModel, user.get());
                userRepository.save(user.get());
                return user;
            }
        } catch (Exception e) {
            logger.error("Error occurred at updateEducationInformation() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserEducationalInfo(UserWebModel userInput, User userToUpdate) {

        if (!Utility.isNullOrBlankWithTrim(userInput.getSchoolName())) userToUpdate.setSchoolName(userInput.getSchoolName());
        if (!Utility.isNullOrBlankWithTrim(userInput.getCollegeName())) userToUpdate.setCollegeName(userInput.getCollegeName());
        if (!Utility.isNullOrBlankWithTrim(userInput.getQualification())) userToUpdate.setQualification(userInput.getQualification());

        userToUpdate.setUpdatedBy(userToUpdate.getUserId());
        userToUpdate.setUpdatedOn(new Date());
    }

    @Override
    public Optional<?> updateProfessionInformation(UserWebModel userWebModel) {
        Optional<User> user;
        try {
            user = userRepository.getUserByUserId(userWebModel.getUserId());
            if (user.isPresent()) {
                this.prepareUserProfessionInfo(userWebModel, user.get());
                userRepository.save(user.get());
                return user;
            }
        } catch (Exception e) {
            logger.error("Error occurred at updateProfessionInformation() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserProfessionInfo(UserWebModel userInput, User userToUpdate) {

        if (!Utility.isNullOrBlankWithTrim(userInput.getWorkCategory())) userToUpdate.setWorkCategory(userInput.getWorkCategory());

        userToUpdate.setUpdatedBy(userToUpdate.getUserId());
        userToUpdate.setUpdatedOn(new Date());
    }

    @Override
    public FileOutputWebModel saveProfilePhoto(UserWebModel userWebModel) {
        Optional<User> user;
        try {
            user = userRepository.getUserByUserId(userWebModel.getUserId());
            if (user.isPresent()) {
                // Find and delete old profile pic
                FileOutputWebModel fileOutputWebModel = this.getProfilePic(userWebModel);
                if (fileOutputWebModel != null) {
                    logger.info("Existing profile pic data [{}]", fileOutputWebModel);
                    List<Integer> profilePicIdsList = Collections.singletonList(fileOutputWebModel.getCategoryRefId());
                    mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.ProfilePic, profilePicIdsList);
                }

                // Save/Update profile pic
                userWebModel.getProfilePhoto().setCategory(MediaFileCategory.ProfilePic);
                userWebModel.getProfilePhoto().setCategoryRefId(user.get().getUserId());
                List<FileOutputWebModel> savedFileList = mediaFilesService.saveMediaFiles(userWebModel.getProfilePhoto(), user.get());
                return (!Utility.isNullOrEmptyList(savedFileList)) ? savedFileList.get(0) : null;
            }
        } catch (Exception e) {
            logger.error("Error occurred at saveProfilePhoto() -> [{}]", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public FileOutputWebModel getProfilePic(UserWebModel userWebModel) {
        List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, userWebModel.getUserId());
        if (!Utility.isNullOrEmptyList(outputWebModelList)) return outputWebModelList.get(0);
        return null;
    }

    @Override
    public void deleteUserProfilePic(UserWebModel userWebModel) {
        try {
            FileOutputWebModel fileOutputWebModel = this.getProfilePic(userWebModel);
            if (fileOutputWebModel != null) {
                List<Integer> profilePicIdsList = Collections.singletonList(fileOutputWebModel.getCategoryRefId());
                mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.ProfilePic, profilePicIdsList);
            }
        } catch (Exception e) {
            logger.error("Error at deleteUserProfilePic() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<FileOutputWebModel> saveCoverPhoto(UserWebModel userWebModel) {
        Optional<User> user;
        try {
            user = userRepository.getUserByUserId(userWebModel.getUserId());
            if (user.isPresent()) {
                // Find and delete old cover pic
                List<FileOutputWebModel> outputWebModelList = this.getCoverPic(userWebModel);
                if (!Utility.isNullOrEmptyList(outputWebModelList)) {
                    logger.info("Existing cover pic size [{}]", outputWebModelList.size());
                    List<Integer> coverPicIdsList = outputWebModelList.stream().map(FileOutputWebModel::getCategoryRefId).collect(Collectors.toList());
                    mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.CoverPic, coverPicIdsList);
                }

                // Save/Update cover pic
                userWebModel.getCoverPhoto().setCategory(MediaFileCategory.CoverPic);
                userWebModel.getCoverPhoto().setCategoryRefId(user.get().getUserId());
                return mediaFilesService.saveMediaFiles(userWebModel.getCoverPhoto(), user.get());
            }
        } catch (Exception e) {
            logger.error("Error occurred at saveCoverPhoto() -> [{}]", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public List<FileOutputWebModel> getCoverPic(UserWebModel userWebModel) {
        List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.CoverPic, userWebModel.getUserId());
        if (!Utility.isNullOrEmptyList(outputWebModelList)) return outputWebModelList;
        return null;
    }

    @Override
    public void deleteUserCoverPic(UserWebModel userWebModel) {
        try {
            List<FileOutputWebModel> outputWebModelList = this.getCoverPic(userWebModel);
            if (!Utility.isNullOrEmptyList(outputWebModelList)) {
                List<Integer> coverPicIdsList = outputWebModelList.stream().map(FileOutputWebModel::getCategoryRefId).collect(Collectors.toList());
                mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.CoverPic, coverPicIdsList);
            }
        } catch (Exception e) {
            logger.error("Error at deleteUserCoverPic() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<UserSearchWebModel> getAllIndustryByCountryIds(List<Integer> countryIds) {
        List<UserSearchWebModel> outputList = new ArrayList<>();
        try {
            List<Country> countryList = countryIds.stream()
                    .filter(Objects::nonNull)
                    .map(countryId -> Country.builder().id(countryId).build())
                    .collect(Collectors.toList());

            List<Industry> industryList = industryRepository.getIndustryByCountryIds(countryList);
            if (!Utility.isNullOrEmptyList(industryList)) {
            	 // Sort the industryList in ascending order by industry name
                industryList.sort(Comparator.comparing(Industry::getIndustryName));
                UserSearchWebModel userSearchWebModel = UserSearchWebModel.builder()
                        .industryList(this.transformIndustryData(industryList))
                        .build();
                outputList.add(userSearchWebModel);
            }
        } catch (Exception e) {
            logger.error("Error at getIndustryByCountryId() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

    private List<IndustryWebModel> transformIndustryData(List<Industry> industryList) {
        List<IndustryWebModel> industryWebModels = new ArrayList<>();
        industryList.stream().filter(Objects::nonNull).forEach(industry -> {
            IndustryWebModel industryWebModel = IndustryWebModel.builder()
                    .id(industry.getIndustryId())
                    .industryName(industry.getIndustryName())
                    .stateCode(industry.getStateCode())
                    .status(industry.getStatus())
                    .countryId(industry.getCountry() != null ? industry.getCountry().getId() : null)
                    //.image(industry.getImage() != null ? Base64.getEncoder().encode(industry.getImage()) : null)
                    .iconFilePath(!Utility.isNullOrBlankWithTrim(industry.getFilePath()) ? s3Util.generateS3FilePath(industry.getFilePath()) : "")
                    .build();
            industryWebModels.add(industryWebModel);
        });
        return industryWebModels;
    }

    @Override
    public List<UserSearchWebModel> getAllProfessionByPlatformId(Integer platformId) {
        List<UserSearchWebModel> outputList = new ArrayList<>();
        try {
            List<FilmProfession> professionList = platformFilmProfessionMapRepository.getFilmProfessionsByPlatform(
                    Platform.builder().platformId(platformId).build());
            
            if (!Utility.isNullOrEmptyList(professionList)) {
                UserSearchWebModel userSearchWebModel = UserSearchWebModel.builder()
                        .platformId(platformId) // Set the platformId here
                        .professionList(this.transformProfessionData(professionList))
                        .build();
                outputList.add(userSearchWebModel);
            }
        } catch (Exception e) {
            logger.error("Error at getAllProfessionByPlatformId() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }


    private List<ProfessionWebModel> transformProfessionData(List<FilmProfession> professionList) {
        List<ProfessionWebModel> professionWebModelList = new ArrayList<>();
        professionList.stream().filter(Objects::nonNull).forEach(profession -> {
            ProfessionWebModel professionWebModel = ProfessionWebModel.builder()
                    .id(profession.getFilmProfessionId())
                    .professionName(profession.getProfessionName())
                    .status(profession.getStatus())
                    //.image(profession.getImage() != null ? Base64.getEncoder().encode(profession.getImage()) : null)
                    .iconFilePath(!Utility.isNullOrBlankWithTrim(profession.getFilePath()) ? s3Util.generateS3FilePath(profession.getFilePath()) : "")
                    .build();
            professionWebModelList.add(professionWebModel);
        });
        return professionWebModelList;
    }

    @Override
    public List<UserSearchWebModel> getAllSubProfessionByProfessionId(List<Integer> professionIds) {
        List<UserSearchWebModel> outputList = new ArrayList<>();
        try {
            List<FilmProfession> professionList = professionIds.stream()
                    .filter(Objects::nonNull)
                    .map(professionId -> FilmProfession.builder().filmProfessionId(professionId).build())
                    .collect(Collectors.toList());
            List<FilmSubProfession> subProfessionList = filmSubProfessionRepository.getSubProfessionByProfessionIds(professionList);
            if (!Utility.isNullOrEmptyList(subProfessionList)) {
                UserSearchWebModel userSearchWebModel = UserSearchWebModel.builder()
                        .subProfessionList(this.transformSubProfessionData(subProfessionList))
                        .build();
                outputList.add(userSearchWebModel);
            }
        } catch (Exception e) {
            logger.error("Error at getAllAubProfessionByProfessionId() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

    private List<SubProfessionWebModel> transformSubProfessionData(List<FilmSubProfession> subProfessionList) {
        List<SubProfessionWebModel> subProfessionWebModelList = new ArrayList<>();
        subProfessionList.stream().filter(Objects::nonNull).forEach(subProfession -> {
            SubProfessionWebModel professionWebModel = SubProfessionWebModel.builder()
                    .id(subProfession.getSubProfessionId())
                    .subProfessionName(subProfession.getSubProfessionName())
                    .status(subProfession.getStatus())
                    .build();
            subProfessionWebModelList.add(professionWebModel);
        });
        return subProfessionWebModelList;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getUserByAllSearchCriteria(UserSearchWebModel searchWebModel) {

        // Output Format
        /*{
           "PRODUCER" : [
               {
                    "userId": "",
                    "name": "",
                    "dob": "",
                    "userProfilePic": "",
                    "userRating": "",
                    "experience": "",
                    "moviesCount": "",
                    "netWorth": "",
               },
           ]
        }*/
        Map<String, List<Map<String, Object>>> professionUserMap = new HashMap<>();

        /*List<IndustryUserPermanentDetails> userIndustryDetails;
        List<PlatformPermanentDetail> userPlatformDetails;
        List<FilmProfessionPermanentDetail> userProfessionDetails;
        List<FilmSubProfessionPermanentDetail> userFilmSubProfessionDetails;*/

        /*List<Integer> userIndustryDetails;
        List<Integer> userPlatformDetails;
        List<Integer> userProfessionDetails;
        List<Integer> userFilmSubProfessionDetails;

        Set<Integer> uniqueUsersSet = new HashSet<>();
        List<User> userList = new ArrayList<>();*/

        try {
            // Example search
            // Industry :- [KOLLYWOOD-1, MOLLYWOOD-2]
            // Platform :- [MOVIES-1]
            // Profession :- [ACTOR-1]
            // SubProfession :- [HERO-1]

            /*if (!Utility.isNullOrEmptyList(searchWebModel.getIndustryIds())) {
                logger.info("Input industry search criteria -> {}", searchWebModel.getIndustryIds());

                List<Industry> industryList = searchWebModel.getIndustryIds().stream()
                        .filter(Objects::nonNull)
                        .map(industryId -> Industry.builder().industryId(industryId).build())
                        .collect(Collectors.toList());

//                userIndustryDetails = industryPermanentDetailsRepository.getDataByIndustryIds(industryList);
//                if (!Utility.isNullOrEmptyList(userIndustryDetails))
//                    userIndustryDetails.stream().map(IndustryUserPermanentDetails::getUserId).forEach(uniqueUsersSet::add);

                userIndustryDetails = industryPermanentDetailsRepository.getUsersByIndustryIds(industryList);
                if (!Utility.isNullOrEmptyList(userIndustryDetails)) uniqueUsersSet.addAll(userIndustryDetails);
            }

            if (!Utility.isNullOrBlankWithTrim(String.valueOf(searchWebModel.getPlatformId()))) {
                logger.info("Input Platform search criteria -> {}", searchWebModel.getPlatformId());
//                userPlatformDetails = platformPermanentDetailRepository.getDataByPlatformId(Platform.builder().platformId(searchWebModel.getPlatformId()).build());
//                if (!Utility.isNullOrEmptyList(userPlatformDetails))
//                    userPlatformDetails.stream().map(PlatformPermanentDetail::getUserId).forEach(uniqueUsersSet::add);
                userPlatformDetails = platformPermanentDetailRepository.getUsersByPlatformId(Platform.builder().platformId(searchWebModel.getPlatformId()).build());
                if (!Utility.isNullOrEmptyList(userPlatformDetails)) uniqueUsersSet.addAll(userPlatformDetails);
            }

            if (!Utility.isNullOrEmptyList(searchWebModel.getProfessionIds())) {
                logger.info("Input profession search criteria -> {}", searchWebModel.getProfessionIds());

                List<FilmProfession> professionList = searchWebModel.getProfessionIds().stream()
                        .filter(Objects::nonNull)
                        .map(professionId -> FilmProfession.builder().filmProfessionId(professionId).build())
                        .collect(Collectors.toList());

//                userProfessionDetails = filmProfessionPermanentDetailRepository.getDataByProfessionIds(professionList);
//                if (!Utility.isNullOrEmptyList(userProfessionDetails))
//                    userProfessionDetails.stream().map(FilmProfessionPermanentDetail::getUserId).forEach(uniqueUsersSet::add);

                userProfessionDetails = filmProfessionPermanentDetailRepository.getUsersByProfessionIds(professionList);
                if (!Utility.isNullOrEmptyList(userProfessionDetails)) uniqueUsersSet.addAll(userProfessionDetails);
            }

            if (!Utility.isNullOrEmptyList(searchWebModel.getSubProfessionIds())) {
                logger.info("Input sub profession search criteria -> {}", searchWebModel.getSubProfessionIds());

                List<FilmSubProfession> subProfessionList = searchWebModel.getSubProfessionIds().stream()
                        .filter(Objects::nonNull)
                        .map(subProfessionId -> FilmSubProfession.builder().subProfessionId(subProfessionId).build())
                        .collect(Collectors.toList());

//                userFilmSubProfessionDetails = filmSubProfessionPermanentDetailsRepository.getDataBySubProfessionIds(subProfessionList);
//                if (!Utility.isNullOrEmptyList(userFilmSubProfessionDetails))
//                    userFilmSubProfessionDetails.stream().map(FilmSubProfessionPermanentDetail::getUserId).forEach(uniqueUsersSet::add);

                userFilmSubProfessionDetails = filmSubProfessionPermanentDetailsRepository.getUsersBySubProfessionIds(subProfessionList);
                if (!Utility.isNullOrEmptyList(userFilmSubProfessionDetails)) uniqueUsersSet.addAll(userFilmSubProfessionDetails);
            }

            // Iterating the UserIds and preparing the output
            if (!Utility.isNullOrEmptySet(uniqueUsersSet)) {
                logger.info("Unique User list -> {}", uniqueUsersSet);
                uniqueUsersSet.stream()
                        .filter(Objects::nonNull)
                        .map(this::getUser)
                        .forEach(user -> user.ifPresent(userList::add)); // getting all details about the user

                if (!Utility.isNullOrEmptyList(userList)) {
                    userList.stream()
                            .filter(Objects::nonNull)
                            .forEach(user -> {
                                logger.debug("User iteration -> {}", user.getName());
                                //UserWebModel userWebModel = this.transformUserObjToUserWebModelObj(user);
                                //List<FilmProfessionPermanentDetail> userProfessionDataList = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(user.getUserId());
                                List<FilmSubProfessionPermanentDetail> userProfessionDataList = filmSubProfessionPermanentDetailsRepository.getProfessionDataByUserId(user.getUserId());
                                logger.info("SubProfession count [{}] for [{}]", userProfessionDataList.size(), user.getName());
                                if (!Utility.isNullOrEmptyList(userProfessionDataList)) {
                                    userProfessionDataList.stream()
                                            .filter(Objects::nonNull)
                                            .filter(filter1 -> searchWebModel.getIndustryIds().contains(filter1.getIndustryUserPermanentDetails().getIndustry().getIndustryId()))
                                            .filter(filter2 -> searchWebModel.getPlatformId().equals(filter2.getPlatformPermanentDetail().getPlatform().getPlatformId()))
                                            .filter(filter3 -> searchWebModel.getProfessionIds().contains(filter3.getFilmProfessionPermanentDetail().getFilmProfession().getFilmProfessionId()))
                                            .filter(filter4 -> searchWebModel.getSubProfessionIds().contains(filter4.getFilmSubProfession().getSubProfessionId()))
                                            *//*.filter(filter4 ->  {
                                                AtomicBoolean match = new AtomicBoolean(false);
                                                if (!Utility.isNullOrEmptyList(searchWebModel.getSubProfessionIds())) {
                                                    searchWebModel.getSubProfessionIds()
                                                            .forEach(val ->
                                                                    match.set(filter4.getFilmProfession().getFilmSubProfessionCollection()
                                                                                    .stream()
                                                                                    .anyMatch(dbVal -> dbVal.getSubProfessionId().equals(val)))
                                                            );
                                                } else {
                                                    match.set(true);
                                                }
                                                return match.get();
                                            })*//*
                                            .forEach(professionData -> {
                                                logger.debug("Profession iteration -> {}, {}", professionData.getProfessionPermanentId(), professionData.getProfessionName());

                                                Map<String, Object> map = new LinkedHashMap<>();
                                                map.put("userId", user.getUserId());
                                                map.put("name", user.getName());
                                                map.put("dob", CalendarUtil.convertDateFormat(CalendarUtil.MYSQL_DATE_FORMAT, CalendarUtil.UI_DATE_FORMAT, user.getDob()));

                                                FileOutputWebModel profilePic = this.getProfilePic(UserWebModel.builder().userId(professionData.getUserId()).build());
                                                map.put("userProfilePic", profilePic != null ? profilePic.getFilePath() : "");

                                                map.put("userRating", "");
                                                map.put("experience", "");
                                                map.put("moviesCount", professionData.getPlatformPermanentDetail().getFilmCount());
                                                map.put("netWorth", professionData.getPlatformPermanentDetail().getNetWorth());

                                                map.put("industryId", professionData.getIndustryUserPermanentDetails().getIndustry().getIndustryId());
                                                map.put("industry", professionData.getIndustryUserPermanentDetails().getIndustriesName());

                                                map.put("platformId", professionData.getPlatformPermanentDetail().getPlatform().getPlatformId());
                                                map.put("platform", professionData.getPlatformPermanentDetail().getPlatformName());

                                                map.put("filmProfessionId", professionData.getFilmProfessionPermanentDetail().getFilmProfession().getFilmProfessionId());
                                                map.put("filmProfession", professionData.getFilmProfessionPermanentDetail().getFilmProfession().getProfessionName());

                                                *//*map.put("filmSubProfession", professionData.getFilmSubProfessionPermanentDetails()
                                                        .stream()
                                                        .collect(Collectors.toMap(
                                                                key -> key.getFilmSubProfession().getSubProfessionId(),
                                                                value -> value.getFilmSubProfession().getSubProfessionName())
                                                        )
                                                );*//*

                                                List<Map<String, Object>> finalUserList;
                                                if (professionUserMap.get(professionData.getProfessionName()) == null) {
                                                    finalUserList = new ArrayList<>();
                                                } else {
                                                    finalUserList = professionUserMap.get(professionData.getProfessionName());
                                                }
                                                finalUserList.add(map);
                                                professionUserMap.put(professionData.getProfessionName(), finalUserList);
                                            });
                                }
                            });
                }
            }*/

            List<FilmSubProfessionPermanentDetail> userProfessionDataList = filmSubProfessionPermanentDetailsRepository.findAll().stream().filter(data -> data.getStatus().equals(true)).collect(Collectors.toList());
            if (!Utility.isNullOrEmptyList(userProfessionDataList)) {
                userProfessionDataList.stream()
                        .filter(Objects::nonNull)
                        .filter(filter1 -> searchWebModel.getIndustryIds().contains(filter1.getIndustryUserPermanentDetails().getIndustry().getIndustryId()))
                        .filter(filter2 -> searchWebModel.getPlatformId().equals(filter2.getPlatformPermanentDetail().getPlatform().getPlatformId()))
                        .filter(filter3 -> searchWebModel.getProfessionIds().contains(filter3.getFilmProfessionPermanentDetail().getFilmProfession().getFilmProfessionId()))
                        .filter(filter4 -> searchWebModel.getSubProfessionIds().contains(filter4.getFilmSubProfession().getSubProfessionId()))
                        .forEach(professionData -> {
                            User user = this.getUser(professionData.getUserId()).orElse(null);
                            System.out.print("userssss"+user);
                            if (user != null  && Boolean.TRUE.equals(user.getIndustryUserVerified())) { // Check for industryUserVerified
                                logger.debug("Profession iteration -> {}, {}", professionData.getProfessionPermanentId(), professionData.getProfessionName());
                                Map<String, Object> map = new LinkedHashMap<>();

                                map.put("userId", user.getUserId());
                                map.put("name", user.getName());
                                map.put("userType",user.getUserType());
                                map.put("userOnlineStatus", user.getOnlineStatus());
                                map.put("adminReview", user.getAdminReview());
                                map.put("dob", CalendarUtil.convertDateFormat(CalendarUtil.MYSQL_DATE_FORMAT, CalendarUtil.UI_DATE_FORMAT, user.getDob()));

                                FileOutputWebModel profilePic = this.getProfilePic(UserWebModel.builder().userId(user.getUserId()).build());
                                map.put("userProfilePic", profilePic != null ? profilePic.getFilePath() : "");

                                map.put("userRating", "9.7");
                                map.put("experience", user.getExperience());
                                map.put("moviesCount", professionData.getPlatformPermanentDetail().getFilmCount());
                                map.put("netWorth", professionData.getPlatformPermanentDetail().getNetWorth());
                                map.put("dailySalary", professionData.getPlatformPermanentDetail().getDailySalary());                                map.put("industryId", professionData.getIndustryUserPermanentDetails().getIndustry().getIndustryId());
                                map.put("industry", professionData.getIndustryUserPermanentDetails().getIndustriesName());

                                map.put("platformId", professionData.getPlatformPermanentDetail().getPlatform().getPlatformId());
                                map.put("platform", professionData.getPlatformPermanentDetail().getPlatformName());

                                map.put("filmProfessionId", professionData.getFilmProfessionPermanentDetail().getFilmProfession().getFilmProfessionId());
                                map.put("filmProfession", professionData.getFilmProfessionPermanentDetail().getFilmProfession().getProfessionName());

                                List<Map<String, Object>> finalUserList;
                                if (professionUserMap.get(professionData.getProfessionName()) == null) {
                                    finalUserList = new ArrayList<>();
                                } else {
                                    finalUserList = professionUserMap.get(professionData.getProfessionName());
                                }
                                finalUserList.add(map);
                                professionUserMap.put(professionData.getProfessionName(), finalUserList);
                            }
                        });
            }
            logger.info("Final user search result -> [{}]", professionUserMap.keySet().size());
        } catch (Exception e) {
            logger.error("Error at getUserByAllSearchCriteria() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return professionUserMap;
    }

    @Override
    public ResponseEntity<?> getAllAddressListOnSignUp() {
        List<AddressList> addressLists = addressListRepository.findAll().parallelStream()
                .filter(address -> address.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(address.getSignUpAddress()))
                .collect(Collectors.toList());
        List<AddressListWebModel> result = addressLists.stream()
                .map(addr -> AddressListWebModel.builder()
                        .id(addr.getId())
                        .address(addr.getSignUpAddress())
                        .status(addr.getStatus())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> getAddressListOnSignUp(String address) {
        List<AddressList> addressLists = addressListRepository.findBySignUpAddressContainingIgnoreCase(address).parallelStream()
                .filter(addressList -> addressList.getStatus().equals(true))
                .collect(Collectors.toList());
        List<AddressListWebModel> result = addressLists.stream()
                .map(addr -> AddressListWebModel.builder()
                        .id(addr.getId())
                        .address(addr.getSignUpAddress())
                        .status(addr.getStatus())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public Optional<?> updateUserName(UserWebModel userWebModel) {
        try {
            Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (!Utility.isNullOrBlankWithTrim(userWebModel.getFirstName())) user.setFirstName(userWebModel.getFirstName());
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getLastName())) user.setLastName(userWebModel.getLastName());
                user.setName(user.getFirstName() + " " + user.getLastName());

                user.setUpdatedBy(userWebModel.getUserId());
                user.setUpdatedOn(new Date());

                User updatedUser = userRepository.save(user);
                return Optional.of(updatedUser);
            } else {
                return Optional.empty(); // User not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty(); // In case of any exception
        }
    }

    public Optional<HashMap<String, String>> getUserId(UserWebModel userWebModel) {
        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            HashMap<String, String> userMap = new HashMap<>();
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
            userMap.put("userName", user.getName());
            userMap.put("userProfilePic", userService.getProfilePicUrl(userWebModel.getUserId()));

            // Fetching the user Profession
            Set<String> professionNames = new HashSet<>();
            List<FilmProfessionPermanentDetail> professionPermanentDataList = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(userWebModel.getUserId());
            if (!Utility.isNullOrEmptyList(professionPermanentDataList)) {
                professionNames = professionPermanentDataList.stream()
                        .map(FilmProfessionPermanentDetail::getProfessionName)
                        .collect(Collectors.toSet());
            } else {
                professionNames.add("Public User");
            }

            // Convert the professionNames set to a comma-separated string
            String professionNamesString = String.join(", ", professionNames);
            userMap.put("professionNames", professionNamesString);

            return Optional.of(userMap);
        }
        return Optional.empty();
    }

    @Override
    public String getProfilePicUrl(Integer userId) {
        FileOutputWebModel profilePic = this.getProfilePic(UserWebModel.builder().userId(userId).build());
        return profilePic != null ? profilePic.getFilePath() : "";
    }

    @Override
    public List<UserWebModel> getUserByName(String name) {
        List<UserWebModel> responseList = new ArrayList<>();
        try {
            List<User> usersList = userRepository.findByNameContainingIgnoreCaseAndStatus(name, true);
            if (!Utility.isNullOrEmptyList(usersList)) {
                responseList = usersList.stream()
                        .filter(Objects::nonNull)
                        .map(user -> UserWebModel.builder()
                                .userId(user.getUserId())
                                .name(user.getName())
                                .adminReview(user.getAdminReview())
                                .userType(user.getUserType())
                                .profilePicOutput(this.getProfilePic(UserWebModel.builder().userId(user.getUserId()).build()))
                                .profilePicUrl(this.getProfilePicUrl(user.getUserId()))
                                .userType(user.getUserType())
                                .build())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error occurred at getUserByName() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return responseList;
    }

    @Override
    public Optional<Location> saveUserLocation(LocationWebModel locationWebModel) {
        try {
            User user = userRepository.findById(locationWebModel.getUserId()).orElse(null);
            if (user != null) {
                Location location = null;

                Location userLocation = user.getLocation();
                if (userLocation != null) {
                    location = userLocation;
                    location.setUpdatedBy(user.getUserId());
                    location.setUpdatedOn(new Date());
                } else {
                    // If location doesn't exist, create a new one
                    location = new Location();
                    location.setUser(user);  // Associate the new location with the user
                    location.setStatus(true);
                    location.setCreatedBy(user.getUserId());
                    location.setCreatedOn(new Date());
                }

                // Update location details
                location.setLatitude(Utility.parseDouble(locationWebModel.getLatitude()));
                location.setLongitude(Utility.parseDouble(locationWebModel.getLongitude()));
                location.setAddress(locationWebModel.getAddress());
                location.setLandMark(locationWebModel.getLandMark());
                location.setLocationName(locationWebModel.getLocationName());

                // Save location
                Location savedLocation = locationRepository.save(location);
                return Optional.of(savedLocation);
            }
        } catch (Exception e) {
            logger.error("Error at saveLocationByUserId() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

//    @Override
//    public List<Map<String, Object>> findNearByUsers(Integer userId, Integer range, String profession) {
//        try {
//            if (userId != null) {
//                Optional<User> userOptional = userRepository.findById(userId);
//                User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));
//
//                Location loggedInUserLocation = user.getLocation();
//                if (loggedInUserLocation == null) {
//                    throw new RuntimeException("User location not found");
//                }
//
//                List<User> nearByUsers;
//                if (range != null) {
//                    // Fetch nearby users within range
//                    Double rangeInMiles = 0.6213711922 * range; // 1 Mile(s) = 0.6213711922 * [km(s)]
//                    nearByUsers = locationRepository.getNearByUsers(user.getUserId(), rangeInMiles, loggedInUserLocation.getLatitude(), loggedInUserLocation.getLongitude()).stream()
//                            .map((Integer val) -> this.getUser(val).orElse(null)) // Explicit type specification
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//                } else {
//                    // Fetch all nearby users except the logged-in user
//                    nearByUsers = locationRepository.getAllUsersExceptLoggedIn(userId).stream()
//                            .map((Integer val) -> this.getUser(val).orElse(null)) // Explicit type specification
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//                }
//
//                // Create a list to store each user's location details
//                List<Map<String, Object>> nearbyUsersList = new ArrayList<>();
//                nearByUsers.forEach(userData -> {
//                    Location location = userData.getLocation();
//                    if (location != null) {
//                        double distance = Utility.calculateDistance(loggedInUserLocation.getLatitude(), loggedInUserLocation.getLongitude(), location.getLatitude(), location.getLongitude());
//                        logger.debug("[{}] is [{}] away from you...", userData.getName(), (Math.round(distance) + " Km"));
//
//                        // Fetching the user Profession
//                        Set<String> professionNames = new HashSet<>();
//                        List<FilmProfessionPermanentDetail> professionPermanentDataList = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(userData.getUserId());
//                        if (!professionPermanentDataList.isEmpty()) {
//                            professionNames = professionPermanentDataList.stream().map(FilmProfessionPermanentDetail::getProfessionName).collect(Collectors.toSet());
//                        } else {
//                            professionNames.add("CommonUser");
//                        }
//
//                        // Apply profession filter if specified
//                        if (profession == null || professionNames.contains(profession)) {
//                            Map<String, Object> userDetails = new LinkedHashMap<>();
//                            userDetails.put("userId", userData.getUserId());
//                            userDetails.put("latitude", location.getLatitude());
//                            userDetails.put("longitude", location.getLongitude());
//                            userDetails.put("distance", Math.round(distance));
//                            userDetails.put("distanceUnit", "Km");
//                            userDetails.put("profilePic", userService.getProfilePicUrl(userData.getUserId()));
//                            userDetails.put("userName", userData.getName());
//                            userDetails.put("professionNames", professionNames);
//
//                            nearbyUsersList.add(userDetails);
//                        }
//                    }
//                });
//
//                // Sort users by distance
//                nearbyUsersList.sort(Comparator.comparing(u -> (Long) u.get("distance")));
//                logger.info("NearBy Users count -> [{}]", nearbyUsersList.size());
//                return nearbyUsersList;
//            } else {
//                throw new RuntimeException("User ID must be provided");
//            }
//        } catch (Exception e) {
//            logger.error("Error at findNearByUsers() -> {}", e.getMessage());
//            e.printStackTrace();
//        }
//        return Collections.emptyList(); // Return empty list if any exception occurs
//    }
//    @Override
//    public List<Map<String, Object>> findNearByUsers(Integer userId, Integer range, String profession) {
//        try {
//            if (userId != null) {
//                Optional<User> userOptional = userRepository.findById(userId);
//                User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));
//
//                Location loggedInUserLocation = user.getLocation();
//                if (loggedInUserLocation == null) {
//                    throw new RuntimeException("User location not found");
//                }
//
//                List<User> nearByUsers;
//                if (range != null) {
//                    // Fetch nearby users within range
//                    Double rangeInMiles = 0.6213711922 * range; // 1 Mile(s) = 0.6213711922 * [km(s)]
//                    nearByUsers = locationRepository.getNearByUsers(user.getUserId(), rangeInMiles, loggedInUserLocation.getLatitude(), loggedInUserLocation.getLongitude()).stream()
//                            .map((Integer val) -> this.getUser(val).orElse(null)) // Explicit type specification
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//                } else {
//                    // Fetch all nearby users except the logged-in user
//                    nearByUsers = locationRepository.getAllUsersExceptLoggedIn(userId).stream()
//                            .map((Integer val) -> this.getUser(val).orElse(null)) // Explicit type specification
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//                }
//
//                // Create a list to store each user's location details
//                List<Map<String, Object>> nearbyUsersList = new ArrayList<>();
//
//                // Add the logged-in user's data first
//                Map<String, Object> loggedInUserDetails = new LinkedHashMap<>();
//                loggedInUserDetails.put("userId", user.getUserId());
//                loggedInUserDetails.put("latitude", loggedInUserLocation.getLatitude());
//                loggedInUserDetails.put("longitude", loggedInUserLocation.getLongitude());
//                loggedInUserDetails.put("distance", 0);
//                loggedInUserDetails.put("distanceUnit", "Km");
//                loggedInUserDetails.put("profilePic", userService.getProfilePicUrl(user.getUserId()));
//                loggedInUserDetails.put("userName", user.getName());
//                loggedInUserDetails.put("professionNames", getProfessionNames(user.getUserId()));
//                nearbyUsersList.add(loggedInUserDetails);
//
//                nearByUsers.forEach(userData -> {
//                    Location location = userData.getLocation();
//                    if (location != null) {
//                        double distance = Utility.calculateDistance(loggedInUserLocation.getLatitude(), loggedInUserLocation.getLongitude(), location.getLatitude(), location.getLongitude());
//                        logger.debug("[{}] is [{}] away from you...", userData.getName(), (Math.round(distance) + " Km"));
//
//                        // Fetching the user Profession
//                        Set<String> professionNames = getProfessionNames(userData.getUserId());
//
//                        // Apply profession filter if specified
//                        if (profession == null || professionNames.contains(profession)) {
//                            Map<String, Object> userDetails = new LinkedHashMap<>();
//                            userDetails.put("userId", userData.getUserId());
//                            userDetails.put("latitude", location.getLatitude());
//                            userDetails.put("longitude", location.getLongitude());
//                            userDetails.put("distance", Math.round(distance));
//                            userDetails.put("distanceUnit", "Km");
//                            userDetails.put("profilePic", userService.getProfilePicUrl(userData.getUserId()));
//                            userDetails.put("userName", userData.getName());
//                            userDetails.put("professionNames", professionNames);
//
//                            nearbyUsersList.add(userDetails);
//                        }
//                    }
//                });
//
//                // Sort users by distance, excluding the logged-in user who is already at index 0
//                nearbyUsersList.subList(1, nearbyUsersList.size()).sort(Comparator.comparing(u -> (Long) u.get("distance")));
//                logger.info("NearBy Users count -> [{}]", nearbyUsersList.size() - 1); // Exclude the logged-in user from the count
//                return nearbyUsersList;
//            } else {
//                throw new RuntimeException("User ID must be provided");
//            }
//        } catch (Exception e) {
//            logger.error("Error at findNearByUsers() -> {}", e.getMessage());
//            e.printStackTrace();
//        }
//        return Collections.emptyList(); // Return empty list if any exception occurs
//    }

    private Set<String> getProfessionNames(Integer userId) {
        List<FilmProfessionPermanentDetail> professionPermanentDataList = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(userId);
        if (!professionPermanentDataList.isEmpty()) {
            return professionPermanentDataList.stream().map(FilmProfessionPermanentDetail::getProfessionName).collect(Collectors.toSet());
        } else {
            return Collections.singleton("");
        }
    }


    @Override
    public Optional<User> changePrimaryEmaiId(UserWebModel userWebModel) {
        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setChangeEmailId(userWebModel.getChangeEmailId());

            // Generate OTP
            int otp = Integer.parseInt(Utility.generateOtp(4));
            user.setEmailOtp(otp);
            userRepository.save(user);

            // Send verification email
            boolean sendVerificationRes = mailNotification.sendVerificationEmail(user);
            if (sendVerificationRes) return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> changePrimaryEmaiIdVerified(UserWebModel userWebModel) {
        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
        if (userOptional.isEmpty()) return Optional.empty();

        User user = userOptional.get();
        int providedOtp = userWebModel.getEmailOtp();

        // Verify the OTP
        if (user.getEmailOtp() != null && user.getEmailOtp().equals(providedOtp)) {
            // OTP matches, update the email and mark it as verified
            user.setEmail(user.getChangeEmailId());
            user.setChangeEmailId(null); // Clear the change email field
            user.setVerified(true); // Mark as verified
            user.setEmailOtp(null); // Clear the OTP field

            userRepository.save(user); // Save changes

            return Optional.of(user); // Return the updated user
        } else {
            // OTP does not match or OTP is null
            return Optional.empty();
        }
    }

	@Override
	public ResponseEntity<?> getNewAddressListOnSignUp(String address) {
	        List<AddressList> addressLists = addressListRepository.findBynewSignUpAddressContainingIgnoreCase(address).parallelStream()
	                .filter(addressList -> addressList.getStatus().equals(true))
	                .collect(Collectors.toList());
	        List<AddressListWebModel> result = addressLists.stream()
	                .map(addr -> AddressListWebModel.builder()
	                        .id(addr.getId())
	                        .address(addr.getNewSignUpAddress())
	                        .status(addr.getStatus())
	                        .build())
	                .collect(Collectors.toList());
	        return ResponseEntity.ok(result);
	    }

	@Override
	public ResponseEntity<?> getLocationByuserId(Integer userId) {
	    try {
	        // Retrieve user data to ensure the user exists
	        Optional<User> userData = userRepository.findById(userId);
	        
	        if (userData.isPresent()) {
	            // User exists, retrieve location data
	            Optional<Location> locationData = locationRepository.findByUserId(userId);
	            
	            if (locationData.isPresent()) {
	                // Location data found, construct response
	                Location location = locationData.get();
	                Map<String, Object> response = new HashMap<>();
	                response.put("latitude", location.getLatitude());
	                response.put("longitude", location.getLongitude());
	                response.put("address", location.getAddress());
	                response.put("locationName", location.getLocationName());
	                response.put("landMark", location.getLandMark());
	                response.put("profilePic", userService.getProfilePicUrl(location.getUser().getUserId()));
	                response.put("userName", location.getUser().getName());
	                
	                return ResponseEntity.ok(response);
	            } else {
	                // No location data found for the user
	                return ResponseEntity.notFound().build();
	            }
	        } else {
	            // User not found
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        // Handle exceptions
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }

	}
	@Override
	public List<Map<String, Object>> findNearByUsers(Integer userId) {
	    try {
	        if (userId != null) {
	            Optional<User> userOptional = userRepository.findById(userId);
	            User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));

	            Location loggedInUserLocation = user.getLocation();
	            if (loggedInUserLocation == null) {
	                throw new RuntimeException("User location not found");
	            }

	            // Fetch all nearby users except the logged-in user
	            List<User> nearByUsers = locationRepository.getAllUsersExceptLoggedIn(userId).stream()
	                    .map(this::getUser)
	                    .filter(Optional::isPresent)
	                    .map(Optional::get)
	                    .collect(Collectors.toList());

	            // Create a list to store each user's location details
	            List<Map<String, Object>> nearbyUsersList = new ArrayList<>();

	            // Add the logged-in user's data first
	            Map<String, Object> loggedInUserDetails = new LinkedHashMap<>();
	            loggedInUserDetails.put("userId", user.getUserId());
	            loggedInUserDetails.put("latitude", loggedInUserLocation.getLatitude());
	            loggedInUserDetails.put("longitude", loggedInUserLocation.getLongitude());
	            loggedInUserDetails.put("distance", 0);
	            loggedInUserDetails.put("distanceUnit", "Km");
	            loggedInUserDetails.put("profilePic", userService.getProfilePicUrl(user.getUserId()));
	            loggedInUserDetails.put("userName", user.getName());
	            loggedInUserDetails.put("professionNames", getProfessionNames(user.getUserId()));
	            loggedInUserDetails.put("userType", user.getUserType());
	            loggedInUserDetails.put("review", user.getAdminReview());
	            nearbyUsersList.add(loggedInUserDetails);

	            // Process nearby users
	            nearByUsers.forEach(userData -> {
	                Location location = userData.getLocation();
	                if (location != null) {
	                    double distance = Utility.calculateDistance(
	                            loggedInUserLocation.getLatitude(),
	                            loggedInUserLocation.getLongitude(),
	                            location.getLatitude(),
	                            location.getLongitude()
	                    );
	                    logger.debug("[{}] is [{}] away from you...", userData.getName(), (Math.round(distance) + " Km"));

	                    Map<String, Object> userDetails = new LinkedHashMap<>();
	                    userDetails.put("userId", userData.getUserId());
	                    userDetails.put("latitude", location.getLatitude());
	                    userDetails.put("longitude", location.getLongitude());
	                    userDetails.put("distance", Math.round(distance));
	                    userDetails.put("distanceUnit", "Km");
	                    userDetails.put("profilePic", userService.getProfilePicUrl(userData.getUserId()));
	                    userDetails.put("userName", userData.getName());
	                    userDetails.put("professionNames", getProfessionNames(userData.getUserId()));
	                    userDetails.put("userType", userData.getUserType());
	                    userDetails.put("review", userData.getAdminReview());

	                    nearbyUsersList.add(userDetails);
	                }
	            });

	            nearbyUsersList.subList(1, nearbyUsersList.size()).sort(Comparator.comparing(u -> (Long) u.get("distance")));
	            logger.info("NearBy Users count -> [{}]", nearbyUsersList.size() - 1); // Exclude the logged-in user from the count
	            return nearbyUsersList;
	        } else {
	            throw new RuntimeException("User ID must be provided");
	        }
	    } catch (Exception e) {
	        logger.error("Error at findNearByUsers() -> {}", e.getMessage());
	        e.printStackTrace();
	    }
	    return Collections.emptyList(); // Return empty list if any exception occurs
	}
	 @Override
	    public ResponseEntity<?> deactivateUserId(Integer userId, String password) {
	        // Validate input parameters
	        if (userId == null || password == null || password.isEmpty()) {
	            return ResponseEntity.badRequest().body(new Response(0, "fail", "User ID and password must be provided."));
	        }

	        // Retrieve the user from the database
	        Optional<User> userOptional = userRepository.findById(userId);
	        if (!userOptional.isPresent()) {
	            return ResponseEntity.badRequest().body(new Response(0, "fail", "User not found."));
	        }

	        User user = userOptional.get();

	        // Verify the password (compare the provided password with the stored hashed password)
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        if (!passwordEncoder.matches(password, user.getPassword())) {
	            return ResponseEntity.badRequest().body(new Response(0, "fail", "Incorrect password."));
	        }

	        // Deactivate the user account (set 'status' flag to false)
	        if (!user.getStatus()) {
	        	return ResponseEntity.badRequest().body(new Response(0, "fail", "User is already deactivated."));
	        }
	        //user.setStatus(false); // Ensure 'status' is a boolean or equivalent flag in the User entity
	        userRepository.save(user); // Save changes to the database

//	        // Send deactivation email
//	        boolean emailSent = sendVerificationEmail(user, false);
//	        if (!emailSent) {
//	            // Log the failure, but do not block the response
//	            System.err.println("Failed to send deactivation email to user: " + user.getEmail());
//	        }

	        return ResponseEntity.ok(new Response(1, "success", "User account has been deactivated successfully."));
	    }

	 @Override
	 public ResponseEntity<?> saveDeleteReason(UserWebModel userWebModel) {
	     try {
	         // Fetch user by ID
	         Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());

	         if (userOptional.isPresent()) {
	             User user = userOptional.get();

	             // Update the deleteReason field
	             user.setDeleteReason(userWebModel.getDeleteReason());

	             // Save the updated user back to the database
	             userRepository.save(user);

	             return ResponseEntity.ok("Delete reason saved successfully for user with ID: " + userWebModel.getUserId());
	         } else {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userWebModel.getUserId());
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving the delete reason.");
	     }
	 }

	 @Override
	 public ResponseEntity<?> getDeleteStatus(UserWebModel userWebModel) {
	     try {
	         // Fetch user by ID
	         Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());

	         if (userOptional.isPresent()) {
	             User user = userOptional.get();

	             // Create a HashMap to store the response
	             Map<String, Object> response = new HashMap<>();
	             response.put("userId", user.getUserId());
	             response.put("deleteReason", user.getDeleteReason());
	             response.put("accessOrDeniedStatus", user.getDeactivateAccessOrdeny());
	             response.put("deniedAccessRead", user.getDeniedAccessRead());

	             return ResponseEntity.ok(response);
	         } else {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userWebModel.getUserId());
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving the delete status.");
	     }
	 }

	@Override
	public ResponseEntity<?> confirmdeleteUserId(Integer userId, String password) {
        // Validate input parameters
        if (userId == null || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(0, "fail", "User ID and password must be provided."));
        }

        // Retrieve the user from the database
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new Response(0, "fail", "User not found."));
        }

        User user = userOptional.get();

        // Verify the password (compare the provided password with the stored hashed password)
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(new Response(0, "fail", "Incorrect password."));
        }

        // Deactivate the user account (set 'status' flag to false)
        if (!user.getStatus()) {
        	return ResponseEntity.badRequest().body(new Response(0, "fail", "User is already deactivated."));
        }
        user.setStatus(false); // Ensure 'status' is a boolean or equivalent flag in the User entity
        userRepository.save(user); // Save changes to the database

        // Send deactivation email
        try {
            boolean emailSent = sendVerificationEmail(user, false); // false = deactivation context
            if (!emailSent) {
                System.err.println("Failed to send deactivation email to user: " + user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
        return ResponseEntity.ok(new Response(1, "success", "User account has been deactivated successfully."));
	}
	
	
	public boolean sendVerificationEmail(User user, boolean isActivation) {
	    try {
	        String to = user.getEmail();
	        String name = user.getName();

	        String subject = isActivation
	                ? "✅ Your Account Has Been Activated"
	                : "⚠️ Your Account Has Been Deactivated";

	        String actionMessage = isActivation
	                ? "Your account is now active. You can continue using all FilmHook services without interruption."
	                : "Your account has been deactivated and you will no longer have access to FilmHook services.";

	        StringBuilder content = new StringBuilder();
	        content.append("<html><body style='font-family:Arial, sans-serif; padding:20px;'>")

	            // Logo
	            .append("<div style='text-align:center; margin-bottom:20px;'>")
	            .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' width='170' alt='FilmHook Logo'>")
	            .append("</div>")

	            // Greeting and Message
	            .append("<p style='font-size:12px; color:#333333;'>Hi <strong>").append(name).append("</strong>,</p>")
	            .append("<p style='font-size:12px; color:#444444;'>").append(actionMessage).append("</p>")

	            // Support Message
	            .append("<p style='font-size:12px; color:#444444;'>If you did not request this change or think it’s a mistake, please contact our support team immediately.</p>")

	            // Footer Info
	            .append("<hr style='border:0;border-top:1px solid #e0e0e0;margin:30px 0;'>")
	            .append("<p style='font-size:12px; color:#444444;'>Regards,<br><strong>FilmHook Team</strong><br>")
	            .append("<a href='mailto:support@filmhook.com' style='color:#007bff;'>📧 support@filmhook.com</a><br>")
	            .append("<a href='https://filmhook.com' style='color:#007bff;'>🌐 www.filmhook.com</a></p>")

	            // App Links
	            .append("<p style='font-size:15px;'><strong>📲 Get the App:</strong></p><p>")
	            .append("<a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'>")
	            .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='30' style='margin-right:10px;'></a>")
	            .append("<a href='#'>")
	            .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='30'></a></p>")

	            // Social Media Icons
	            .append("<p style='font-size:15px;'><strong>📢 Follow Us:</strong></p><p>")
	            .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='25' style='margin-right:8px;'></a>")
	            .append("<a href='https://x.com/Filmhook_Apps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='25' style='margin-right:8px;'></a>")
	            .append("<a href='https://www.threads.net/@filmhookapps/'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='25' style='margin-right:8px;'></a>")
	            .append("<a href='https://www.instagram.com/filmhookapps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='25' style='margin-right:8px;'></a>")
	            .append("<a href='https://youtube.com/@film-hookapps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='25' style='margin-right:8px;'></a>")
	            .append("<a href='https://www.linkedin.com/in/film-hook-68666a353'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linked.png' width='25'></a>")
	            .append("</p>")

	            // Copyright
	            .append("<p style='font-size:12px; color:#aaaaaa; text-align:center;'>")
	            .append("This is an automated email. Please do not reply.<br>")
	            .append("&copy; ").append(java.time.Year.now()).append(" FilmHook. All rights reserved.")
	            .append("</p>")

	            .append("</body></html>");

	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(content.toString(), true);

	        mailSender.send(message);
	        return true;

	    } catch (MessagingException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	@Override
	public ResponseEntity<?> updateRerferrralcode(UserWebModel userWebModel) {
	    try {
	        Optional<User> db = userRepository.findById(userWebModel.getUserId());
	        if (db.isPresent()) {
	            User user = db.get();

	            // Get current time (like 16:00)
	            LocalTime currentTime = LocalTime.now();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
	            String referralCode = currentTime.format(formatter); // append 1 => 16001

	            user.setReferralCode(referralCode);
	            userRepository.save(user);

	            return ResponseEntity.ok("Referral code updated successfully: " + referralCode);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	    }
	}

	@Override
	public ResponseEntity<?> getReferralCodeByUserId(Integer userId) {
	    try {
	        Optional<User> userOptional = userRepository.findById(userId);
	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	           // String referralCode = user.getReferralCode();

	            Map<String, Object> response = new HashMap<>();
	            response.put("userId", user.getUserId());
	            response.put("referralCode", user.getReferralCode());

	            return ResponseEntity.ok(response);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                 .body(new Response(-1, "User not found", ""));
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Error: " + e.getMessage(), ""));
	    }
	}

	@Override
	public ResponseEntity<?> addLocation(UserWebModel locationWebModel) {
	    HashMap<String, Object> response = new HashMap<>();
	    try {
	        logger.info("addLocation method start");
	        
	        // Validate that userId is provided
	        if (locationWebModel.getUserId() == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new Response(-1, "Error", "User ID is required"));
	        }
	        
	        // Find the user by userId
	        Optional<User> userOptional = userRepository.findById(locationWebModel.getUserId());
	        
	        if (!userOptional.isPresent()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new Response(-1, "Error", "User not found with ID: " + locationWebModel.getUserId()));
	        }
	        
	        // Get the user and update location details
	        User user = userOptional.get();
	        
	        // Update user's location information (direct fields in User entity)
	        user.setLocationName(locationWebModel.getLocationName());
	        user.setLocationAddress(locationWebModel.getLocationAddress());
	        user.setLocationLandMark(locationWebModel.getLocationLandMark());
	        user.setLocationLatitude(locationWebModel.getLocationLatitude());
	        user.setLocationLongitude(locationWebModel.getLocationLongitude());
	        
	        // Note: apartment, blockNumber, building, floor, street fields are not present in User entity
	        // These would be in the separate Location entity if needed
	        
	       
	        user.setUpdatedOn(new Date());
	        
	        // Save the updated user
	        user = userRepository.save(user);
	        
	        logger.info("addLocation method end");
	        
	        // Prepare response with updated user location details
	        Map<String, Object> locationDetails = new HashMap<>();
	        locationDetails.put("userId", user.getUserId());
	        locationDetails.put("locationName", user.getLocationName());
	        locationDetails.put("locationAddress", user.getLocationAddress());
	        locationDetails.put("locationLandMark", user.getLocationLandMark());
	        locationDetails.put("locationLatitude", user.getLocationLatitude());
	        locationDetails.put("locationLongitude", user.getLocationLongitude());
	        
	        response.put("LocationDetails", locationDetails);
	        
	        return ResponseEntity.status(HttpStatus.OK)
	            .body(new Response(1, "Success", response));
	            
	    } catch (Exception e) {
	        logger.error("addLocation Method Exception: ", e);
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}


//	    public boolean sendVerificationEmail(User user, Boolean status) {
//	        try {
//	            String subject, mailContent = "";
//	            if (!status) { // If status is false, indicating deactivation
//	                subject = "Account Deactivation Notice";
//	               // mailContent += "<p>Dear " + user.getName() + ",</p>";
//	                mailContent += "<p>Your account has been deactivated. If this was a mistake, please contact our support team for assistance.</p>";
//	               // mailContent += "<p>Best Regards,<br/>The Film-Hook Team</p>";
//	            } else { // Other scenarios (e.g., profile rejection)
//	                subject = "Profile Rejected";
//	                mailContent += "<p>Your profile on FilmHook has been rejected. Please contact support for further details.</p>";
//	            }
//	            return mailNotification.sendEmailSync(user.getName(), user.getEmail(), subject, mailContent);
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	        return false;
//	    }
	 @Override
	    public List<UserWebModel> getUserByFhId(String filmHookCode) {
	        List<UserWebModel> responseList = new ArrayList<>();
	        try {
	            List<User> usersList = userRepository.findByFilmHookCodeContainingIgnoreCaseAndStatus(filmHookCode, true);
	            if (!Utility.isNullOrEmptyList(usersList)) {
	                responseList = usersList.stream()
	                        .filter(Objects::nonNull)
	                        .map(user -> UserWebModel.builder()
	                                .userId(user.getUserId())
	                                .name(user.getName())
	                                .adminReview(user.getAdminReview())
	                                .userType(user.getUserType())
	                                .profilePicOutput(this.getProfilePic(UserWebModel.builder().userId(user.getUserId()).build()))
	                                .profilePicUrl(this.getProfilePicUrl(user.getUserId()))
	                                .userType(user.getUserType())
	                                .filmHookCode(user.getFilmHookCode())
	                                .build())
	                        .collect(Collectors.toList());
	            }
	        } catch (Exception e) {
	            logger.error("Error occurred at getUserByfilmHookCode() -> {}", e.getMessage());
	            e.printStackTrace();
	        }
	        return responseList;
	    }



	}