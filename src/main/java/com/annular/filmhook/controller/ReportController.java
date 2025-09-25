package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.security.UserDetailsImpl;
import com.annular.filmhook.service.ReportService;
import com.annular.filmhook.webmodel.ReportPostWebModel;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    public static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @PostMapping("/addPostReport")
    public ResponseEntity<?> addPostReport(@RequestBody ReportPostWebModel reportPostWebModel, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            logger.info("addPostReport controller start");
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(-1, "Authentication required", null));
            }
          
            
            return reportService.addPostReport(reportPostWebModel, userDetails.getId());
        } catch (Exception e) {
            logger.error("addPostReport Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getAllPostReport")
    public ResponseEntity<?> getAllPostReport(@RequestBody ReportPostWebModel postWebModel) {
        try {
            logger.info("getAllPostReport controller start");
            return reportService.getAllPostReport(postWebModel);
        } catch (Exception e) {
            logger.error("getAllPostReport Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getByPostReportId")
    public ResponseEntity<?> getByPostReportId(@RequestBody ReportPostWebModel reportPostWebModel) {
        try {
            logger.info("getByPostReportId controller start");
            return reportService.getByPostReportId(reportPostWebModel);
        } catch (Exception e) {
            logger.error("getByPostReportId Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getAllReportsByPostId")
    public ResponseEntity<?> getAllReportsByPostId(@RequestBody ReportPostWebModel postWebModel) {
        try {
            logger.info("getAllReportsByPostId controller start");
            return reportService.getAllReportsByPostId(postWebModel);
        } catch (Exception e) {
            logger.error("getAllReportsByPostId Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getReportsByUserId")
    public ResponseEntity<?> getReportsByUserId(@RequestBody ReportPostWebModel postWebModel) {
        try {
            logger.info("getReportsByUserId controller start");
            return reportService.getReportsByUserId(postWebModel);
        } catch (Exception e) {
            logger.error("getReportsByUserId Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
    @PostMapping("/updateReportsByDeleteAnsSuspension")
    public ResponseEntity<?> updateReportsByDeleteAnsSuspension(@RequestBody ReportPostWebModel postWebModel) {
        try {
            logger.info("updateReportsByDeleteAnsSuspension controller start");
            return reportService.updateReportsByDeleteAnsSuspension(postWebModel);
        } catch (Exception e) {
            logger.error("updateReportsByDeleteAnsSuspension Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
}
