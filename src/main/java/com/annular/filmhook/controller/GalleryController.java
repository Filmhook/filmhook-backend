package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.GalleryService;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

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
@RequestMapping("/user/gallery")
public class GalleryController {

    public static final Logger logger = LoggerFactory.getLogger(GalleryController.class);

    @Autowired
    GalleryService galleryService;

    @RequestMapping(path = "/saveGalleryFiles", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response saveGalleryFiles(@ModelAttribute FileInputWebModel inputFileData) {
        try {
            logger.info("saveGalleryFiles Inputs :- {}", inputFileData);
            List<FileOutputWebModel> outputFileData = galleryService.saveGalleryFiles(inputFileData);
            if (outputFileData != null) return new Response(1, "File(s) saved successfully...", outputFileData);
        } catch (Exception e) {
            logger.error("Error at saveGalleryFiles()...", e);
            return new Response(-1, "Error occurred while saving gallery files...", e);
        }
        return new Response(-1, "Error occurred while saving gallery files...", null);
    }

    @GetMapping("/downloadGalleryFile")
    public ResponseEntity<?> downloadGalleryFile(@RequestParam("userId") Integer userId,
                                                 @RequestParam("category") String category,
                                                 @RequestParam("fileId") String fileId) {
        try {
            logger.info("downloadGalleryFile Input Category :- {}, File Id :- {}", category, fileId);
            Resource resource = galleryService.getGalleryFile(userId, category, fileId);
            if (resource != null) {
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + fileId + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadGalleryFile()...", e);
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/getGalleryFilesByUserId")
    public Response getGalleryFiles(@RequestParam("userId") Integer userId) {
        try {
            List<FileOutputWebModel> outputList = galleryService.getGalleryFilesByUser(userId);
            if (outputList != null && !outputList.isEmpty()) {
                logger.info("[{}] Gallery files found for userId :- {}", outputList.size(), userId);
                return new Response(1, "Gallery file(s) found successfully...", outputList);
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFiles()...", e);
        }
        return new Response(-1, "Files were not found...", null);
    }

    @GetMapping("/downloadGalleryFiles")
    public ResponseEntity<?> downloadGalleryFiles(@RequestParam("userId") Integer userId,@RequestParam("category") String category) {
        try {
            logger.info("downloadGalleryFiles Input Category :- {}", category);
            Resource resource = galleryService.getAllGalleryFilesInCategory(userId,category);
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
            logger.error("Error at downloadGalleryFile()...", e);
            return ResponseEntity.internalServerError().build(); // Return error response
        }
        return ResponseEntity.notFound().build(); // Return not found response if resource is null
    }

    @GetMapping("/downloadAllUserGalleryFiles")
    public ResponseEntity<?> downloadAllUserGalleryFiles(@RequestParam("category") String category) {
        try {
            logger.info("downloadAllUserGalleryFiles Input Category :- {}", category);
            Resource resource = galleryService.getAllGalleryFilesInCategory(category);
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
            logger.error("Error at downloadGalleryFile()...", e);
            return ResponseEntity.internalServerError().build(); // Return error response
        }
        return ResponseEntity.notFound().build(); // Return not found response if resource is null
    }

    @GetMapping("/getGalleryFilesByAllUser")
    public Response getAllUsersGalleryFiles() {
        try {
            List<FileOutputWebModel> outputList = galleryService.getAllUsersGalleryFiles();
            if (outputList != null && !outputList.isEmpty()) {
                logger.info("[{}] gallery files found...", outputList.size());
                return new Response(1, "Gallery file(s) found successfully...", outputList);
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getAllUsersGalleryFiles()...", e);
        }
        return new Response(-1, "Files were not found...", null);
    }
}