package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Location;
import com.annular.filmhook.model.User;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.LocationWebModel;
import com.annular.filmhook.webmodel.UserSearchWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @GetMapping("/getAllUsers")
    public Response getAllUsers() {
        List<UserWebModel> userList = userService.getAllUsers();
        return new Response(1, "User list", userList);
    }

    @GetMapping("/getUserByUserId")
    public Response getUserByUserId(@RequestParam("userId") Integer userId) {
        Optional<UserWebModel> user = userService.getUserByUserId(userId);
        if (user.isPresent()) {
            return new Response(1, "User found...", user);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    // Biography Section

    /**
     * Method to update biography data
     *
     * @param userWebModel
     * @return Response
     */
    @PutMapping("/updateBiographyDetails")
    public Response updateBiography(@RequestBody UserWebModel userWebModel) {
        Optional<?> updatedUser = userService.updateBiographyData(userWebModel);
        if (updatedUser.isPresent()) {
            return new Response(1, "Updated Successfully...", updatedUser);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    // Body Measurements

    /**
     * Method to update biological data
     *
     * @param userWebModel
     * @return Response
     */
    @PutMapping("/updateBiologicalDetails")
    public Response updateBiologicalDetails(@RequestBody UserWebModel userWebModel) {
        Optional<?> updatedUser = userService.updateBiologicalData(userWebModel);
        if (updatedUser.isPresent()) {
            return new Response(1, "Updated Successfully...", updatedUser);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    // Personal Information

    /**
     * Method to update personal information
     *
     * @param userWebModel
     * @return Response
     */
    @PutMapping("/updatePersonalInfo")
    public Response updatePersonalInfo(@RequestBody UserWebModel userWebModel) {
        Optional<?> updatedUser = userService.updatePersonalInformation(userWebModel);
        if (updatedUser.isPresent()) {
            return new Response(1, "Updated Successfully...", updatedUser);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    // Education Information

    /**
     * Method to update education information
     *
     * @param userWebModel
     * @return Response
     */
    @PutMapping("/updateEducationInfo")
    public Response updateEducationInfo(@RequestBody UserWebModel userWebModel) {
        Optional<?> updatedUser = userService.updateEducationInformation(userWebModel);
        if (updatedUser.isPresent()) {
            return new Response(1, "Updated Successfully...", updatedUser);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }


    // Work category and profession information

    /**
     * Method to update Work category and profession information
     *
     * @param userWebModel
     * @return Response
     */
    @PutMapping("/updateProfessionInfo")
    public Response updateProfessionInfo(@RequestBody UserWebModel userWebModel) {
        Optional<?> updatedUser = userService.updateProfessionInformation(userWebModel);
        if (updatedUser.isPresent()) {
            return new Response(1, "Updated Successfully...", updatedUser);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    /**
     * Method to save or update user's profile pic
     *
     * @param userWebModel
     * @return Response
     */
    @PostMapping(path = "/saveProfilePhoto", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response saveProfilePhoto(@ModelAttribute UserWebModel userWebModel) {
        FileOutputWebModel profilePic = userService.saveProfilePhoto(userWebModel);
        if (profilePic != null) {
            return new Response(1, "Profile pic saved Successfully...", profilePic);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    /**
     * Method to get user's profile pic...
     *
     * @param userWebModel
     * @return Response
     */
    @PostMapping("/getProfilePic")
    public Response getProfilePic(@RequestBody UserWebModel userWebModel) {
        FileOutputWebModel profilePic = userService.getProfilePic(userWebModel);
        if (profilePic != null) return new Response(1, "Profile pic found successfully...", profilePic);
        return new Response(-1, "User profile pic not found...", null);
    }

    /**
     * Method to delete the user's profile pic
     *
     * @param userWebModel
     * @return Response
     */
    @PostMapping("/deleteProfilePic")
    public Response deleteProfilePic(@RequestBody UserWebModel userWebModel) {
        try {
            userService.deleteUserProfilePic(userWebModel);
            return new Response(1, "Profile pic deleted successfully...", null);
        } catch (Exception e) {
            logger.error("Error at deleteProfilePic -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at deleting profile pic...", null);
        }
    }

    /**
     * Method to save or update user's cover pic
     *
     * @param userWebModel
     * @return Response
     */
    @PostMapping(path = "/saveCoverPhoto", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response saveCoverPhoto(@ModelAttribute UserWebModel userWebModel) {
        List<FileOutputWebModel> coverPicList = userService.saveCoverPhoto(userWebModel);
        if (coverPicList != null) {
            return new Response(1, "Cover Photo saved Successfully...", coverPicList);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    /**
     * Method to get user's cover pic...
     *
     * @param userWebModel
     * @return Response
     */
    @PostMapping("/getCoverPic")
    public Response getCoverPic(@RequestBody UserWebModel userWebModel) {
        List<FileOutputWebModel> coverPic = userService.getCoverPic(userWebModel);
        if (coverPic != null) return new Response(1, "User Cover pic(s) found successfully...", coverPic);
        return new Response(-1, "User cover pic not found...", null);
    }

    /**
     * Method to delete the user's cover pic
     *
     * @param userWebModel
     * @return Response
     */
    @PostMapping("/deleteCoverPic")
    public Response deleteCoverPic(@RequestBody UserWebModel userWebModel) {
        try {
            userService.deleteUserCoverPic(userWebModel);
            return new Response(1, "Cover pic(s) deleted successfully...", null);
        } catch (Exception e) {
            logger.error("Error at deleteCoverPic -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at deleting cover pic...", null);
        }
    }

    // User search based on multiple criteria [Country > Industry > Platform > Profession > SubProfession]
    @PostMapping("/getIndustryByCountry")
    public Response getIndustryByCountry(@RequestBody UserSearchWebModel searchWebModel) {
        try {
            List<UserSearchWebModel> userSearchWebModelList = userService.getAllIndustryByCountryIds(searchWebModel.getCountryIds());
            if (!Utility.isNullOrEmptyList(userSearchWebModelList))
                return new Response(1, "Industry(s) found successfully...", userSearchWebModelList);
        } catch (Exception e) {
            logger.error("Error at getIndustryByCountry -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at industry search by country...", null);
        }
        return new Response(1, "Industry(s) not found for country id.", searchWebModel.getCountryIds());
    }

    @PostMapping("/getProfessionByPlatform")
    public Response getProfessionByPlatform(@RequestBody UserSearchWebModel searchWebModel) {
        try {
            List<UserSearchWebModel> userSearchWebModelList = userService.getAllProfessionByPlatformId(searchWebModel.getPlatformId());
            if (!Utility.isNullOrEmptyList(userSearchWebModelList))
                return new Response(1, "Profession(s) found successfully...", userSearchWebModelList);
        } catch (Exception e) {
            logger.error("Error at getProfessionByPlatform -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at profession search by platform...", null);
        }
        return new Response(-1, "Profession(s) not found for platform id -> [" + searchWebModel.getPlatformId() + "]", "");
    }

    @PostMapping("/getSubProfessionByProfession")
    public Response getSubProfessionByProfession(@RequestBody UserSearchWebModel searchWebModel) {
        try {
            List<UserSearchWebModel> userSearchWebModelList = userService.getAllSubProfessionByProfessionId(searchWebModel.getProfessionIds());
            if (!Utility.isNullOrEmptyList(userSearchWebModelList))
                return new Response(1, "Sub Profession(s) found successfully...", userSearchWebModelList);
        } catch (Exception e) {
            logger.error("Error at getSubProfessionByProfession -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at sub profession search by platform...", null);
        }
        return new Response(-1, "Sub Profession(s) not found for profession ids -> [{}]", searchWebModel.getProfessionIds());
    }

    @PostMapping("/getFinalUserList")
    public Response getUserByAllCriteria(@RequestBody UserSearchWebModel searchWebModel) {
        try {
            Map<String, List<Map<String, Object>>> outputMap = userService.getUserByAllSearchCriteria(searchWebModel);
            if (!Utility.isNullOrEmptyMap(outputMap))
                return new Response(1, "User(s) found successfully...", outputMap);
        } catch (Exception e) {
            logger.error("Error at getUserByAllCriteria -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at user search...", null);
        }
        return new Response(-1, "User(s) not found for the given search criteria...", null);
    }

    @GetMapping("/getAllAddressListOnSignUp")
    public ResponseEntity<?> getAllAddressListOnSignUp() {
        try {
            return userService.getAllAddressListOnSignUp();
        } catch (Exception e) {
            logger.error("getAllAddressListOnSignUp Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @GetMapping("/getAddressListOnSignUp")
    public ResponseEntity<?> getAddressListOnSignUp(@RequestParam("address") String address) {
        try {
            return userService.getAddressListOnSignUp(address);
        } catch (Exception e) {
            logger.error("getAddressListOnSignUp Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PutMapping("/updateUserName")
    public Response updateUserName(@RequestBody UserWebModel userWebModel) {
        Optional<?> updatedUser = userService.updateUserName(userWebModel);
        if (updatedUser.isPresent()) {
            return new Response(1, "Updated Successfully...", updatedUser);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    @PostMapping("/getUserId")
    public Response getUserId(@RequestBody UserWebModel userWebModel) {
        Optional<?> updatedUser = userService.getUserId(userWebModel);
        if (updatedUser.isPresent()) {
            return new Response(1, "getUserId...", updatedUser);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    @GetMapping("/getUserByName")
    public Response getUserId(@RequestParam("name") String name) {
        List<UserWebModel> users = userService.getUserByName(name);
        if (!Utility.isNullOrEmptyList(users)) {
            return new Response(1, "User(s) found successfully...", users);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    @PostMapping("/saveUserLocation")
    public Response saveUserLocation(@RequestBody LocationWebModel locationWebModel) {
        Optional<Location> updatedLocation = userService.saveUserLocation(locationWebModel);
        return updatedLocation.map(location -> new Response(1, "Location saved/updated successfully...", location))
                .orElseGet(() -> new Response(-1, "User not found...", null));
    }

    @GetMapping("/getNearByUsers")
    public Response getNearByUsers(@RequestParam("userId") Integer userId, @RequestParam("range") Integer range) {
        try {
            List<Map<String, Object>> nearbyUsers = userService.findNearByUsers(userId, range);
            if (!Utility.isNullOrEmptyList(nearbyUsers))
                return new Response(1, "Nearby user(s) found successfully...", nearbyUsers);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Response(-1, "Error", e.getMessage());
        }
        return new Response(-1, "Nearby user(s) not found...", "");
    }

    @PostMapping("/changePrimaryEmaiId")
    public Response changePrimaryEmaiId(@RequestBody UserWebModel userWebModel) {
        Optional<User> updatedEmailId = userService.changePrimaryEmaiId(userWebModel);
        if (updatedEmailId.isPresent()) {
            return new Response(1, "otp send successfully...", updatedEmailId.get());
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

    @PostMapping("/changePrimaryEmaiIdVerified")
    public Response changePrimaryEmaiIdVerified(@RequestBody UserWebModel userWebModel) {
        Optional<User> updatedEmailId = userService.changePrimaryEmaiIdVerified(userWebModel);
        if (updatedEmailId.isPresent()) {
            return new Response(1, "verified and primay EmailId updated successfully...", updatedEmailId.get());
        } else {
            return new Response(-1, "User not found...", null);
        }
    }

}
