package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.service.StoriesService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.StoriesWebModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user/stories")
public class StoriesController {

    public static final Logger logger = LoggerFactory.getLogger(StoriesController.class);

    @Autowired
    StoriesService storiesService;

    @RequestMapping(path = "/uploadStory", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response uploadStory(@ModelAttribute StoriesWebModel inputData) {
        try {
            logger.info("uploadStory Inputs :- {}", inputData);
            FileOutputWebModel story = storiesService.uploadStory(inputData);
            if (story != null) return new Response(1, "Story uploaded successfully...", story);
        } catch (Exception e) {
            logger.error("Error at uploadStory()...", e);
        }
        return new Response(-1, "Error occurred while uploading story...", null);
    }

    @GetMapping("/getUserStories")
    public Response getAllUserStories(@RequestParam("userId") Integer userId) {
        try {
            List<StoriesWebModel> storyList = storiesService.getStoryByUserId(userId);
            if (storyList != null)
                return new Response(1, storyList.size() + " Stories retrieved successfully...", storyList);
        } catch (Exception e) {
            logger.error("Error at getAllUserStories()...", e);
        }
        return new Response(-1, "Story not found...", null);
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Response(-1, "Requested file not found", ""));
            }
        } catch (Exception e) {
            logger.error("Error at downloadStoryFile()...", e);
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
            logger.error("Error at deleteUserStories()...", e);
            return new Response(-1, "Story not found...", null);
        }
    }

    @DeleteMapping("/deleteStory")
    public Response deleteUserStoriesById(@RequestParam("id") Integer storyId) {
        try {
            Story story = storiesService.deleteStoryById(storyId);
            if (story != null) {
                return new Response(1, "Story deleted successfully...", null);
            } else {
                return new Response(-1, "Story not available to delete...", null);
            }
        } catch (Exception e) {
            logger.error("Error at deleteUserStoriesById()...", e);
        }
        return new Response(-1, "Story not found...", null);
    }

    @PutMapping("/updateStoryViews")
    public Response updateStoryViews(@RequestParam("userId") Integer userId,
                                     @RequestParam("storyId") String storyId) {
        try {
            Optional<Story> story = storiesService.updateStoryView(userId, storyId);
            if (story.isPresent()) return new Response(1, "Story views updated successfully...", null);
        } catch (Exception e) {
            logger.error("Error at updateStoryViews()...", e);
        }
        return new Response(-1, "Story not found...", null);
    }
}
