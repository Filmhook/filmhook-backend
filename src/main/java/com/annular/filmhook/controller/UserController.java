package com.annular.filmhook.controller;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.*;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.UserWebModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    AwsS3Service awsS3Service;

    @Autowired
    S3Util s3Util;

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

}
