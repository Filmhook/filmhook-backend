package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.ReportService;
import com.annular.filmhook.webmodel.ReportPostWebModel;

@RestController
@RequestMapping("/report")
public class ReportController {
	
	@Autowired
	ReportService reportService;
	
	public static final Logger logger = LoggerFactory.getLogger(ReportController.class);
	
	@PostMapping("/addPostReport")
	public ResponseEntity<?> addPostReport(@RequestBody ReportPostWebModel reportPostWebModel) {
		try {
			logger.info("addPostReport controller start");
			return reportService.addPostReport(reportPostWebModel);
		} catch (Exception e) {
			logger.error("addPostReport Method Exception {}" + e);
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
			logger.error("getAllPostReport Method Exception {}" + e);
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
			logger.error("getByPostReportId Method Exception {}" + e);
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
			logger.error("getAllReportsByPostId Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

}
