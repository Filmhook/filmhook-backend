package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.PostService;

import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.PostWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/user/post")
public class PostController {

    public static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    PostService postService;

    @RequestMapping(path = "/savePost", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response savePost(@ModelAttribute PostWebModel inputFileData) {
        try {
            logger.info("savePost Inputs :- {}", inputFileData);
            PostWebModel outputFileData = postService.savePostsWithFiles(inputFileData);
            if (outputFileData != null) return new Response(1, "Post saved successfully...", outputFileData);
        } catch (Exception e) {
            logger.error("Error at savePost()...", e);
            return new Response(-1, "Error occurred while saving post with files...", e);
        }
        return new Response(-1, "Error occurred while saving post with files...", null);
    }

    @GetMapping("/downloadPostFile")
    public ResponseEntity<?> downloadPostFile(@RequestParam("userId") Integer userId,
                                              @RequestParam("category") String category,
                                              @RequestParam("fileId") String fileId) {
        try {
            logger.info("downloadPostFile Input Category :- {}, File Id :- {}", category, fileId);
            Resource resource = postService.getPostFile(userId, category, fileId);
            if (resource != null) {
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + fileId + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadPostFile()...", e);
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/getPostsByUserId")
    public Response getPostsByUserId(@RequestParam("userId") Integer userId) {
        try {
            List<PostWebModel> outputList = postService.getPostsByUserId(userId);
            if (outputList != null && !outputList.isEmpty()) {
                return new Response(1, "Post(s) found successfully...", outputList);
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getPostsByUserId()...", e);
        }
        return new Response(-1, "Post files were not found...", null);
    }

    @GetMapping("/downloadUserPostFiles")
    public ResponseEntity<?> downloadPostFiles(@RequestParam("userId") Integer userId, @RequestParam("category") String category) {
        try {
            logger.info("downloadPostFiles Input Category :- {}", category);
            Resource resource = postService.getAllPostByUserIdAndCategory(userId, category);
            if (resource != null) {
                // Determine content type
                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                String contentType = "application/octet-stream";

                // Build filename from category or use a default filename
                String filename = category + ".zip"; // Example filename

                // Set content disposition header
                String headerValue = "attachment; filename=\"" + filename + "\"";

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadPostFiles()...", e);
            return ResponseEntity.internalServerError().build(); // Return error response
        }
        return ResponseEntity.notFound().build(); // Return not found response if resource is null
    }

    @GetMapping("/downloadAllPostsByCategory")
    public ResponseEntity<?> downloadAllUserPosts(@RequestParam("category") String category) {
        try {
            logger.info("downloadAllUserPosts Input Category :- {}", category);
            Resource resource = postService.getAllPostFilesByCategory(category);
            if (resource != null) {
                // Determine content type
                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                String contentType = "application/octet-stream";

                // Build filename from category or use a default filename
                String filename = category + ".zip"; // Example filename

                // Set content disposition header
                String headerValue = "attachment; filename=\"" + filename + "\"";

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadAllUserPosts()...", e);
            return ResponseEntity.internalServerError().build(); // Return error response
        }
        return ResponseEntity.notFound().build(); // Return didn't find response if resource is null
    }

    @GetMapping("/getAllUsersPosts")
    public Response getAllUsersPosts() {
        try {
            List<PostWebModel> postWebModelList = postService.getAllUsersPosts();
            if (!Utility.isNullOrEmptyList(postWebModelList)) return new Response(1, "Success", postWebModelList);
        } catch (Exception e) {
            logger.error("Error at getAllUsersPosts()...", e);
        }
        return new Response(-1, "Files were not found...", null);
    }

}