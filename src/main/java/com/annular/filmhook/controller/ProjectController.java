package com.annular.filmhook.controller;

import java.util.List;

import com.annular.filmhook.service.ProjectService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.ProjectWebModel;

@RestController
@RequestMapping("/IndustryUser/project")
public class ProjectController {

    public static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    ProjectService projectService;

    @PostMapping(path = "/saveProjectFiles", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response saveProjectFiles(@ModelAttribute ProjectWebModel projectWebModel) {
        try {
            logger.info("saveProjectFiles Inputs :- {}", projectWebModel);
            List<FileOutputWebModel> outputList = projectService.saveProjectFiles(projectWebModel);
            if (outputList != null && !outputList.isEmpty()) return new Response(1, "File(s) saved successfully...", outputList);
        } catch (Exception e) {
            logger.error("Error at saveProjectFiles() -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error occurred while saving saveProject Files..", e);
        }
        return new Response(-1, "Error occurred while save Project Files...", null);
    }

    @GetMapping("/getProjectFilesByPlatformId")
    public Response getProjectFilesByPlatformId(@RequestParam("userId") Integer userId, @RequestParam("platformPermanentId") Integer platformPermanentId) {
        try {
            List<FileOutputWebModel> outputList = projectService.getProjectFiles(userId, platformPermanentId);
            if (outputList != null && !outputList.isEmpty()) {
                logger.info("[{}] project media files found for userId :- {}", outputList.size(), userId);
                return new Response(1, "Project file(s) found successfully...", outputList);
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getProjectFilesByPlatformId() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Files were not found...", null);
    }

    @GetMapping("/downloadProjectFile")
    public ResponseEntity<?> downloadProjectFile(@RequestParam("userId") Integer userId,
                                                 @RequestParam("category") String category,
                                                 @RequestParam("fileId") String fileId) {
        try {
            logger.info("downloadProjectFile Input Category :- {}, File Id :- {}", category, fileId);
            Resource resource = projectService.getProjectFiles(userId, category, fileId);
            if (resource != null) {
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + fileId + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadProjectFile -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

}
