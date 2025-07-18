package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.Report;
import com.annular.filmhook.repository.ReportRepositorys;
import com.annular.filmhook.service.ReportServices;
import com.annular.filmhook.webmodel.ReportRequestDTO;

@Service
public class ReportServiceImpls implements ReportServices {

    private final ReportRepositorys reportRepository;

    @Autowired
    public ReportServiceImpls(ReportRepositorys reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report createReport(ReportRequestDTO dto) {
        Report report = Report.builder()
                .type(dto.getType())
                .referenceId(dto.getReferenceId())
                .reporterUserId(dto.getReporterUserId())
                .reason(dto.getReason())
                .additionalComments(dto.getAdditionalComments())
                .reportedOn(LocalDateTime.now())
                .resolved(false)
                .build();

        return reportRepository.save(report);
    }

    @Override
    public List<Report> getReportsByTypeAndRefId(String type, Long refId) {
        return reportRepository.findByTypeAndReferenceId(type, refId);
    }

    @Override
    public Report resolveReport(Long reportId, Long adminId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setResolved(true);
        report.setResolvedBy(adminId);
        report.setResolvedOn(LocalDateTime.now());

        return reportRepository.save(report);
    }
}