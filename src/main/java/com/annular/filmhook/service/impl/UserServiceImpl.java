package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.*;
import com.annular.filmhook.repository.*;

import com.annular.filmhook.service.MediaFilesService;

import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.webmodel.UserWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.UserSearchWebModel;
import com.annular.filmhook.webmodel.IndustryWebModel;
import com.annular.filmhook.webmodel.ProfessionWebModel;
import com.annular.filmhook.webmodel.SubProfessionWebModel;
import com.annular.filmhook.webmodel.ScheduleWebModel;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserDetails loggedInUser;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalendarUtil calendarUtil;

    @Autowired
    S3Util s3Util;

    @Autowired
    MediaFilesService mediaFilesService;

    @Override
    public List<UserWebModel> getAllUsers() {
        return userRepository.findAll().stream().filter(Objects::nonNull).map(this::transformUserObjToUserWebModelObj).collect(Collectors.toList());
    }

    @Autowired
    IndustryRepository industryRepository;

    @Autowired
    IndustryUserPermanentDetailsRepository industryPermanentDetailsRepository;

    @Autowired
    PlatformRepository platformRepository;

    @Autowired
    PlatformPermanentDetailRepository platformPermanentDetailRepository;

    @Autowired
    FilmProfessionRepository filmProfessionRepository;

    @Autowired
    FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

    @Autowired
    FilmSubProfessionRepository filmSubProfessionRepository;

    @Autowired
    FilmSubProfessionPermanentDetailsRepository filmSubProfessionPermanentDetailsRepository;

    @Autowired
    BookingsRepository bookingsRepository;

    @Override
    public Optional<UserWebModel> getUserByUserId(Integer userId) {
        UserWebModel user = null;
        Optional<?> dbUser = userRepository.getUserByUserId(userId);
        if (dbUser.isPresent())
            user = this.transformUserObjToUserWebModelObj((User) dbUser.get());
        return Optional.ofNullable(user);
    }

    public UserWebModel transformUserObjToUserWebModelObj(User user) {
        UserWebModel userWebModel = new UserWebModel();

        userWebModel.setUserId(user.getUserId());

        userWebModel.setEmail(user.getEmail());
        userWebModel.setUserType(user.getUserType());

        userWebModel.setName(user.getName());
        if (!Utility.isNullOrBlankWithTrim(user.getDob())) {
            userWebModel.setDob(CalendarUtil.convertDateFormat(CalendarUtil.MYSQL_DATE_FORMAT, CalendarUtil.UI_DATE_FORMAT, user.getDob()));
            userWebModel.setAge(calendarUtil.getAgeFromDate(userWebModel.getDob(), CalendarUtil.UI_DATE_FORMAT));
        }
        userWebModel.setGender(user.getGender());

        userWebModel.setCountry(user.getCountry());
        userWebModel.setState(user.getState());
        userWebModel.setDistrict(user.getDistrict());
        userWebModel.setPhoneNumber(user.getPhoneNumber());
        userWebModel.setCurrentAddress(user.getCurrentAddress());
        userWebModel.setHomeAddress(user.getHomeAddress());

        userWebModel.setHeight(user.getHeight());
        userWebModel.setWeight(user.getWeight());
        userWebModel.setSkinTone(user.getSkinTone());
        userWebModel.setHairColor(user.getHairColor());
        userWebModel.setBmi(user.getBmi());
        userWebModel.setChestSize(user.getChestSize());
        userWebModel.setWaistSize(user.getWaistSize());
        userWebModel.setBicepsSize(user.getBiceps());

        userWebModel.setReligion(user.getReligion());
        userWebModel.setCaste(user.getCaste());
        userWebModel.setMaritalStatus(user.getMaritalStatus());
        userWebModel.setSpouseName(user.getSpouseName());
        if (user.getChildrenNames() != null) {
            userWebModel.setChildrenNames(new ArrayList<>(Arrays.asList(user.getChildrenNames().split(","))));
        }
        userWebModel.setMotherName(user.getMotherName());
        userWebModel.setFatherName(user.getFatherName());
        if (user.getChildrenNames() != null) {
            userWebModel.setBrotherNames(new ArrayList<>(Arrays.asList(user.getBrotherNames().split(","))));
        }
        if (user.getChildrenNames() != null) {
            userWebModel.setSisterNames(new ArrayList<>(Arrays.asList(user.getSisterNames().split(","))));
        }

        userWebModel.setSchoolName(user.getSchoolName());
        userWebModel.setCollegeName(user.getCollegeName());
        userWebModel.setQualification(user.getQualification());

        userWebModel.setWorkCategory(user.getWorkCategory());

        userWebModel.setStatus(user.getStatus());

        userWebModel.setCreatedBy(user.getCreatedBy());
        userWebModel.setCreatedOn(user.getCreatedOn());
        userWebModel.setUpdatedBy(user.getUpdatedBy());
        userWebModel.setUpdateOn(user.getUpdatedOn());

        List<FileOutputWebModel> profilePicList = mediaFilesService.getMediaFilesByCategoryAndUserId(MediaFileCategory.ProfilePic, user.getUserId());
        if(!Utility.isNullOrEmptyList(profilePicList)) userWebModel.setProfilePicOutput(profilePicList.get(0));

        List<FileOutputWebModel> coverPicList = mediaFilesService.getMediaFilesByCategoryAndUserId(MediaFileCategory.CoverPic, user.getUserId());
        if(!Utility.isNullOrEmptyList(coverPicList)) userWebModel.setCoverPhotoOutput(coverPicList.get(0));

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
            logger.error("Error occurred at updateBiographyData()...", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserBiographyData(UserWebModel userInput, User userToUpdate) {
//		userToUpdate.setDob(CalendarUtil.convertDateFormat(CalendarUtil.UI_DATE_FORMAT, CalendarUtil.MYSQL_DATE_FORMAT,
//				userInput.getDob()));
        userToUpdate.setDob(userInput.getDob());
        userToUpdate.setGender(userInput.getGender());
        userToUpdate.setCountry(userInput.getCountry());
        userToUpdate.setState(userInput.getState());
        userToUpdate.setDistrict(userInput.getDistrict());
        //userToUpdate.setPhoneNumber(userInput.getPhoneNumber());
        userToUpdate.setCurrentAddress(userInput.getCurrentAddress());
        userToUpdate.setHomeAddress(userInput.getHomeAddress());

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
            logger.error("Error occurred at updateBiologicalData()...", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserBiologicalData(UserWebModel userInput, User userToUpdate) {
        userToUpdate.setHeight(userInput.getHeight()); //+ "Cm");
        userToUpdate.setWeight(userInput.getWeight()); //+ "Kg");
        userToUpdate.setSkinTone(userInput.getSkinTone());
        userToUpdate.setHairColor(userInput.getHairColor());
        userToUpdate.setBmi(userInput.getBmi());
        userToUpdate.setChestSize(userInput.getChestSize()); //+ "in");
        userToUpdate.setWaistSize(userInput.getWaistSize()); //+ "in");
        userToUpdate.setBiceps(userInput.getBicepsSize());// + "in");

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
            logger.error("Error occurred at updateBiologicalData()...", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserPersonalInfo(UserWebModel userInput, User userToUpdate) {
        userToUpdate.setReligion(userInput.getReligion());
        userToUpdate.setCaste(userInput.getCaste());
        userToUpdate.setMaritalStatus(userInput.getMaritalStatus());
        if (userInput.getChildrenNames() != null) {
            userToUpdate.setChildrenNames(String.join(",", userInput.getChildrenNames()));
        }
        userToUpdate.setMotherName(userInput.getMotherName());
        userToUpdate.setSpouseName(userInput.getSpouseName());
        userToUpdate.setFatherName(userInput.getFatherName());
        if (userInput.getBrotherNames() != null) {
            userToUpdate.setBrotherNames(String.join(",", userInput.getBrotherNames()));
        }
        if (userInput.getSisterNames() != null) {
            userToUpdate.setSisterNames(String.join(",", userInput.getSisterNames()));
        }

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
            logger.error("Error occurred at updateBiologicalData()...", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserEducationalInfo(UserWebModel userInput, User userToUpdate) {
        userToUpdate.setSchoolName(userInput.getSchoolName());
        userToUpdate.setCollegeName(userInput.getCollegeName());
        userToUpdate.setQualification(userInput.getQualification());

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
            logger.error("Error occurred at updateBiologicalData()...", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void prepareUserProfessionInfo(UserWebModel userInput, User userToUpdate) {
        userToUpdate.setWorkCategory(userInput.getWorkCategory());
        userToUpdate.setUpdatedBy(userToUpdate.getUserId());
        userToUpdate.setUpdatedOn(new Date());
        // need to add profession details later.
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
                return (savedFileList != null && !savedFileList.isEmpty()) ? savedFileList.get(0) : null;
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
        if (outputWebModelList != null && !outputWebModelList.isEmpty()) return outputWebModelList.get(0);
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
                if (outputWebModelList != null && !outputWebModelList.isEmpty()) {
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
        if (outputWebModelList != null && !outputWebModelList.isEmpty()) return outputWebModelList;
        return null;
    }

    @Override
    public void deleteUserCoverPic(UserWebModel userWebModel) {
        try {
            List<FileOutputWebModel> outputWebModelList = this.getCoverPic(userWebModel);
            if (outputWebModelList != null && !outputWebModelList.isEmpty()) {
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
            List<FilmProfession> professionList = filmProfessionRepository.findByPlatform(Platform.builder().platformId(platformId).build());
            if (!Utility.isNullOrEmptyList(professionList)) {
                UserSearchWebModel userSearchWebModel = UserSearchWebModel.builder()
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

        /*
        * {
        *   "PRODUCER" : [
        *       {
        *           "userData" : UserWebModel,
        *           "professionData" : {
        *
        *           }
        *       },
        *   ]
        * }
         */
        Map<String, List<Map<String, Object>>> professionUserMap = new HashMap<>();
        List<Map<String, Object>> userDataList = new ArrayList<>();

        List<IndustryUserPermanentDetails> userIndustryDetails;
        List<PlatformPermanentDetail> userPlatformDetails;
        List<FilmProfessionPermanentDetail> userProfessionDetails;
        List<FilmSubProfessionPermanentDetail> userFilmSubProfessionDetails;
        Set<Integer> uniqueUsersSet = new HashSet<>();
        List<User> userList = new ArrayList<>();

        try {
            // Example search
            // Industry :- [KOLLYWOOD-1, MOLLYWOOD-2]
            // Platform :- [MOVIES-1]
            // Profession :- [ACTOR-1]
            // SubProfession :- [HERO-1]

            if (!Utility.isNullOrEmptyList(searchWebModel.getIndustryIds())) {
                logger.info("Input industry search criteria -> {}", searchWebModel.getIndustryIds());

                List<Industry> industryList = searchWebModel.getIndustryIds().stream()
                        .filter(Objects::nonNull)
                        .map(industryId -> Industry.builder().industryId(industryId).build())
                        .collect(Collectors.toList());

                userIndustryDetails = industryPermanentDetailsRepository.getDataByIndustryIds(industryList);
                if (!Utility.isNullOrEmptyList(userIndustryDetails))
                    userIndustryDetails.stream().map(IndustryUserPermanentDetails::getUserId).forEach(uniqueUsersSet::add);
            }

            if (!Utility.isNullOrBlankWithTrim(String.valueOf(searchWebModel.getPlatformId()))) {
                logger.info("Input Platform search criteria -> {}", searchWebModel.getPlatformId());
                userPlatformDetails = platformPermanentDetailRepository.getDataByPlatformId(Platform.builder().platformId(searchWebModel.getPlatformId()).build());
                if (!Utility.isNullOrEmptyList(userPlatformDetails))
                    userPlatformDetails.stream().map(PlatformPermanentDetail::getUserId).forEach(uniqueUsersSet::add);
            }

            if (!Utility.isNullOrEmptyList(searchWebModel.getProfessionIds())) {
                logger.info("Input profession search criteria -> {}", searchWebModel.getProfessionIds());

                List<FilmProfession> professionList = searchWebModel.getProfessionIds().stream()
                        .filter(Objects::nonNull)
                        .map(professionId -> FilmProfession.builder().filmProfessionId(professionId).build())
                        .collect(Collectors.toList());

                userProfessionDetails = filmProfessionPermanentDetailRepository.getDataByProfessionIds(professionList);
                if (!Utility.isNullOrEmptyList(userProfessionDetails))
                    userProfessionDetails.stream().map(FilmProfessionPermanentDetail::getUserId).forEach(uniqueUsersSet::add);
            }

            if (!Utility.isNullOrEmptyList(searchWebModel.getSubProfessionIds())) {
                logger.info("Input sub profession search criteria -> {}", searchWebModel.getSubProfessionIds());

                List<FilmSubProfession> subProfessionList = searchWebModel.getSubProfessionIds().stream()
                        .filter(Objects::nonNull)
                        .map(subProfessionId -> FilmSubProfession.builder().subProfessionId(subProfessionId).build())
                        .collect(Collectors.toList());

                userFilmSubProfessionDetails = filmSubProfessionPermanentDetailsRepository.getDataBySubProfessionIds(subProfessionList);
                if (!Utility.isNullOrEmptyList(userFilmSubProfessionDetails))
                    userFilmSubProfessionDetails.stream().map(FilmSubProfessionPermanentDetail::getUserId).forEach(uniqueUsersSet::add);
            }

            // Iterating the UserIds and preparing the output
            if (!Utility.isNullOrEmptySet(uniqueUsersSet)) {
                uniqueUsersSet.stream().filter(Objects::nonNull).map(this::getUser).forEach(user -> user.ifPresent(userList::add));

                if(!Utility.isNullOrEmptyList(userList)) {
                    userList.stream()
                            .filter(Objects::nonNull)
                            .forEach(user -> {
                                UserWebModel userWebModel = this.transformUserObjToUserWebModelObj(user);
                                List<FilmProfessionPermanentDetail> userProfessionDataList = filmProfessionPermanentDetailRepository.findByUserId(user.getUserId());
                                if (!Utility.isNullOrEmptyList(userProfessionDataList)) {
                                    userProfessionDataList.stream()
                                            .filter(Objects::nonNull)
                                            .forEach(professionData -> {

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("userId", userWebModel.getUserId());
                                                map.put("name", userWebModel.getName());
                                                map.put("dob", userWebModel.getDob());
                                                map.put("userProfilePic", userWebModel.getProfilePicOutput() != null ? userWebModel.getProfilePicOutput().getFilePath() : "");
                                                map.put("userRating", "");
                                                map.put("experience", "");
                                                map.put("moviesCount", professionData.getPlatformPermanentDetail().getFilmCount());
                                                map.put("netWorth", professionData.getPlatformPermanentDetail().getNetWorth());
                                                userDataList.add(map);

                                                professionUserMap.putIfAbsent(professionData.getProfessionName(), userDataList);
                                            });
                                }
                            });
                }
            }
        } catch (Exception e) {
            logger.error("Error at getUserByAllSearchCriteria() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return professionUserMap;
    }

    @Override
    public ScheduleWebModel saveSchedule(ScheduleWebModel scheduleWebModel) {
        try {
            Bookings bookings = Bookings.builder()
                    .project(scheduleWebModel.getProjectName())
                    .scheduledBy(scheduleWebModel.getScheduledBy())
                    .scheduledFor(scheduleWebModel.getScheduledTo())
                    .fromDate(scheduleWebModel.getFromDate())
                    .toDate(scheduleWebModel.getToDate())
                    .status(true)
                    .createdBy((loggedInUser != null && loggedInUser.userInfo() != null) ? loggedInUser.userInfo().getId() : null)
                    .createdOn(new Date())
                    .updatedBy((loggedInUser != null && loggedInUser.userInfo() != null) ? loggedInUser.userInfo().getId() : null)
                    .updatedOn(new Date())
                    .build();
            bookingsRepository.saveAndFlush(bookings);
            return this.transformBookingData(List.of(bookings)).get(0);
        } catch (Exception e) {
            logger.error("Error at saveSchedule() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ScheduleWebModel> getAllUserSchedules(Integer userId) {
        try {
            Integer userIdToSearch = userId != null ? userId : loggedInUser.userInfo().getId();
            return this.transformBookingData(bookingsRepository.findByScheduledFor(userIdToSearch));
        } catch (Exception e) {
            logger.error("Error at getAllUserSchedules() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<ScheduleWebModel> transformBookingData(List<Bookings> userBookings) {
        List<ScheduleWebModel> scheduleWebModels = new ArrayList<>();
        try {
            if (!Utility.isNullOrEmptyList(userBookings)) {
                userBookings.stream().filter(Objects::nonNull).forEach(booking -> {
                    ScheduleWebModel scheduleWebModel = ScheduleWebModel.builder()
                            .scheduleId(booking.getId())
                            .projectName(booking.getProject())
                            .scheduledBy(booking.getScheduledBy())
                            .scheduledTo(booking.getScheduledFor())
                            .fromDate(booking.getFromDate())
                            .toDate(booking.getToDate())
                            .active(booking.getStatus())
                            .createdBy(booking.getCreatedBy())
                            .createdOn(booking.getCreatedOn())
                            .build();
                    scheduleWebModels.add(scheduleWebModel);
                });
            }
        } catch (Exception e) {
            logger.error("Error at transformBookingData() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return scheduleWebModels;
    }
}
