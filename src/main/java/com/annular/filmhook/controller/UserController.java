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

    @GetMapping("/getS3BucketName")
    public Response getS3BucketName() {
        String objKeyName = null;
        List<S3Object> s3Objects = awsS3Service.getAllObjectsByBucketAndDestination(s3Util.getS3BucketName(), "Sample/");
        if (s3Objects != null && s3Objects.size() > 0) {
            s3Objects.stream().filter(Objects::nonNull).forEach(item -> logger.info("S3 Object Key :- {}", item.key()));
            objKeyName = s3Objects.get(0).key();
            return new Response(1, "S3 objects found...", objKeyName);
        }
        return new Response(-1, "S3 objects not found...", null);
    }

    @GetMapping("/getObjectAsBytes")
    public Response getS3Object() {
        String objKeyName = null;
        byte[] val = awsS3Service.getObjectFromS3(s3Util.getS3BucketName(), "Sample/User/Gallery/a31b0981-c616-45b5-b6f5-835ca385ee1e");
        if (val != null) {
            objKeyName = Arrays.toString(val);
            return new Response(1, "S3 objects found...", objKeyName);
        }
        return new Response(-1, "S3 objects not found...", null);
    }

    @GetMapping("/testS3Actions")
    public Response getS3Objects() throws IOException {
        String objKeyName = null;
        // Upload file
        ClassPathResource res = new ClassPathResource("classes/Sample.txt");
        File file = new File(res.getPath());
        awsS3Service.putObjectIntoS3(s3Util.getS3BucketName(), "Sample/", file);

        // Read All
        List<S3Object> s3Objects = awsS3Service.getAllObjectsByBucketAndDestination(s3Util.getS3BucketName(), "Sample/");
        if (s3Objects != null && s3Objects.size() > 0) {
            s3Objects.stream().filter(Objects::nonNull).forEach(item -> logger.info("S3 Object Key :- {}", item.key()));
            objKeyName = s3Objects.get(0).key();
        }

        // Delete All
        //awsS3Service.deleteAllObjectsFromDestination(s3Util.getS3BucketName(), "Sample/");

        return new Response(1, "S3 objects found...", objKeyName);
    }
}
