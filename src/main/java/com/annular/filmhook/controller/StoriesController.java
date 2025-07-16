package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.service.StoriesService;
import com.annular.filmhook.webmodel.StoriesWebModel;
import com.annular.filmhook.webmodel.UserIdAndNameWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/stories")
public class StoriesController {

    public static final Logger logger = LoggerFactory.getLogger(StoriesController.class);

    @Autowired
    StoriesService storiesService;

//    @RequestMapping(path = "/uploadStory", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public Response uploadStory(@ModelAttribute StoriesWebModel inputData) {
//        try {
//            logger.info("uploadStory Inputs :- {}", inputData);
//            // Simply return the response from the service without wrapping it again
//            return storiesService.uploadStory(inputData);
//           // if (story != null) return new Response(1, "Story uploaded successfully...", story);
//        } catch (Exception e) {
//            logger.error("Error at uploadStory() -> {}", e.getMessage());
//            e.printStackTrace();
//        }
//        return new Response(-1, "Error occurred while uploading story...", null);
//    }
    @RequestMapping(path = "/uploadStory", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response uploadStory(@ModelAttribute StoriesWebModel inputData) {
        try {
            logger.info("uploadStory Inputs :- {}", inputData);
            StoriesWebModel story = storiesService.uploadStory(inputData);
            if (story != null) return new Response(1, "Story uploaded successfully...", story);
        } catch (Exception e) {
            logger.error("Error at uploadStory() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Error occurred while uploading story...", null);
    }
//    @GetMapping("/getUserStories")
//    public Response getAllUserStories(@RequestParam("userId") Integer userId) {
//        try {
//            List<Map<String, Object>> storyList = storiesService.getStoryByUserId(userId);
//            if (storyList != null)
//                return new Response(1, storyList.size() + " Stories retrieved successfully...", storyList);
//        } catch (Exception e) {
//            logger.error("Error at getAllUserStories() -> {}", e.getMessage());
//            e.printStackTrace();
//        }
//        return new Response(-1, "Story not found...", null);
//    }
    
    @GetMapping("/getAllUserStories")
    public ResponseEntity<?> getAllUserStories(@RequestParam Integer userId) {
        logger.info("Entered getAllUserStories() with userId: {}", userId);

        try {
            List<StoriesWebModel> stories = storiesService.getStoryByUserId(userId);

            logger.info("Successfully fetched {} stories for userId {}", stories.size(), userId);

            return ResponseEntity.ok(
                    new Response(1, stories.size() + " Stories retrieved successfully...", stories)
            );

        } catch (Exception e) {
            logger.error("Exception in getAllUserStories() for userId {}: {}", userId, e.getMessage(), e);

            return ResponseEntity.internalServerError().body(
                    new Response(0, "Failed to retrieve stories", null)
            );
        }
    }

    @GetMapping("/downloadStoryFile")
    public ResponseEntity<?> downloadStoryFile(@RequestParam("userId") Integer userId,
                                               @RequestParam("category") String category,
                                               @RequestParam("fileId") String fileId) {
        try {
            logger.info("downloadStoryFile Input Category :- {}, File Id :- {}", category, fileId);
            Resource resource = storiesService.getStoryFile(userId, category, fileId);
            if (resource != null) {
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + fileId + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            } else {
                return ResponseEntity.internalServerError().body(new Response(-1, "Requested file not found", ""));
            }
        } catch (Exception e) {
            logger.error("Error at downloadStoryFile() -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/deleteAllUserStories")
    public Response deleteUserStories(@RequestParam("userId") Integer userId) {
        try {
            List<Story> storyList = storiesService.deleteStoryByUserId(userId);
            if (storyList != null && !storyList.isEmpty()) {
                return new Response(1, "Story deleted successfully...", null);
            } else {
                return new Response(-1, "Story not available to delete...", null);
            }
        } catch (Exception e) {
            logger.error("Error at deleteUserStories() -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Story not found...", null);
        }
    }

    @DeleteMapping("/deleteStory")
    public Response deleteUserStoriesById(
            @RequestParam("storyId") String storyId,
            @RequestParam("userId") Integer userId
    ) {
        try {
            Story story = storiesService.deleteStoryById(storyId, userId);
            return new Response(1, "Story deleted successfully...", null);
        } catch (SecurityException se) {
            return new Response(-1, se.getMessage(), null);
        } catch (Exception e) {
            logger.error("Error in deleteUserStoriesById() -> {}", e.getMessage(), e);
            return new Response(-1, "Story could not be deleted.", null);
        }
    }


    @PutMapping("/updateStoryViews")
    public Response updateStoryViews(@RequestParam("userId") Integer userId,
                                     @RequestParam("storyId") String storyId) {
        try {
            Optional<Story> story = storiesService.updateStoryView(userId, storyId);
            if (story.isPresent()) return new Response(1, "Story views updated successfully...", null);
        } catch (Exception e) {
            logger.error("Error at updateStoryViews() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Story not found...", null);
    }
    
    @GetMapping("/getUserIdAndName")
    public Response getUserIdAndName(@RequestParam("userId")Integer userId) {
        try {
            List<UserIdAndNameWebModel> storyList = storiesService.getUserIdAndName(userId);
            if (storyList != null)
                return new Response(1, storyList.size() + " Stories retrieved successfully...", storyList);
        } catch (Exception e) {
            logger.error("Error at getUserIdAndName() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Story not found...", null);
    }
    @GetMapping("/getUserStoriesByUserId")
    public Response getUserStoriesByUserId(@RequestParam("userId") Integer userId) {
        try {
            List<StoriesWebModel> storyList = storiesService.getUserStoriesByUserId(userId);
            if (storyList != null)
                return new Response(1, storyList.size() + " Stories retrieved successfully...", storyList);
        } catch (Exception e) {
            logger.error("Error at getUserStoriesByUserId() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Story not found...", null);
    }
    
    @PostMapping("/saveView")
    public ResponseEntity<?> saveMediaView(
            @RequestParam String storyId,
            @RequestParam Integer mediaFileId,
            @RequestParam Integer viewerId) {
        try {
            storiesService.saveMediaView(storyId, mediaFileId, viewerId);
            return ResponseEntity.ok().body(Map.of(
                "status", 1,
                "message", "View saved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 0,
                "message", "Failed to save view",
                "error", e.getMessage()
            ));
        }
    }
    
  }
