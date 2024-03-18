package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.ReportPostWebModel;

public interface ReportService {

	ResponseEntity<?> addPostReport(ReportPostWebModel reportPostWebModel);

	ResponseEntity<?> getAllPostReport();

	ResponseEntity<?> getByPostReportId(ReportPostWebModel reportPostWebModel);

}
