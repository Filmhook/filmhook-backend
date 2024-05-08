package com.annular.filmhook.service.impl;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.webmodel.UserWebModel;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalendarUtil calendarUtil;

    @Autowired
    MediaFilesService mediaFilesService;
    
    @Override
    public List<UserWebModel> getAllUsers() {
        return userRepository.findAll().stream().filter(Objects::nonNull).map(this::transformUserObjToUserWebModelObj).collect(Collectors.toList());
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
        UserWebModel userWebModel = new UserWebModel();

        userWebModel.setUserId(user.getUserId());

        userWebModel.setEmail(user.getEmail());
        userWebModel.setUserType(user.getUserType());

        userWebModel.setName(user.getName());
//		userWebModel.setDob(CalendarUtil.convertDateFormat(CalendarUtil.MYSQL_DATE_FORMAT, CalendarUtil.UI_DATE_FORMAT,
//				user.getDob()));
        userWebModel.setDob(user.getDob());
        userWebModel.setAge(calendarUtil.getAgeFromDate(user.getDob()).toString());
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
        List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.CoverPic,userWebModel.getUserId());
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
}
