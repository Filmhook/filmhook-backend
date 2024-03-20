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
import org.springframework.web.bind.annotation.*;

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
            logger.info("saveGalleryFiles Inputs :- " + inputFileData);
            FileOutputWebModel outputFileData = galleryService.saveGalleryFiles(inputFileData);
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
            logger.info("downloadGalleryFile Input Category :- " + category + ", File Id :- " + fileId);
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
                logger.info("[" + outputList.size() + "] Gallery files found for userId :- " + userId);
                return new Response(1, "Gallery file(s) found successfully...", outputList);
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFiles()...", e);
        }
        return new Response(-1, "Files were not found...", null);
    }
}
