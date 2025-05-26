package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.ReportPostWebModel;

public interface ReportService {

    ResponseEntity<?> addPostReport(ReportPostWebModel reportPostWebModel);

    ResponseEntity<?> getAllPostReport(ReportPostWebModel postWebModel);

    ResponseEntity<?> getByPostReportId(ReportPostWebModel reportPostWebModel);

    ResponseEntity<?> getAllReportsByPostId(ReportPostWebModel postWebModel);

    ResponseEntity<?> getReportsByUserId(ReportPostWebModel postWebModel);

}
