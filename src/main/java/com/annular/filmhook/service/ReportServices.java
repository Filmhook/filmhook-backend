package com.annular.filmhook.service;

import java.util.List;

import com.annular.filmhook.model.Report;
import com.annular.filmhook.webmodel.ReportRequestDTO;

public interface ReportServices {
	 Report createReport(ReportRequestDTO dto);

	    List<Report> getReportsByTypeAndRefId(String type, Long refId);

	    Report resolveReport(Long reportId, Long adminId);
}
