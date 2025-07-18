package com.annular.filmhook.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.annular.filmhook.model.Report;
import com.annular.filmhook.service.ReportServices;
import com.annular.filmhook.webmodel.ReportRequestDTO;
import com.annular.filmhook.webmodel.ReportResponseDTO;

@RestController
@RequestMapping("/api/reports")
public class ReportControllers {

	@Autowired
	private ReportServices reportService;

    @PostMapping("/story")
    public ResponseEntity<ReportResponseDTO> createReport(@RequestBody ReportRequestDTO dto) {
        Report report = reportService.createReport(dto);
        return ResponseEntity.ok(convertToDTO(report));
    }

    @GetMapping("/{type}/{refId}")
    public ResponseEntity<List<ReportResponseDTO>> getReportsByTypeAndRefId(
            @PathVariable String type,
            @PathVariable Long refId) {

        List<ReportResponseDTO> response = reportService
                .getReportsByTypeAndRefId(type, refId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reportId}/resolve")
    public ResponseEntity<ReportResponseDTO> resolveReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId) {

        Report resolvedReport = reportService.resolveReport(reportId, adminId);
        return ResponseEntity.ok(convertToDTO(resolvedReport));
    }

    private ReportResponseDTO convertToDTO(Report report) {
        return ReportResponseDTO.builder()
                .id(report.getId())
                .type(report.getType())
                .referenceId(report.getReferenceId())
                .reporterUserId(report.getReporterUserId())
                .reason(report.getReason())
                .additionalComments(report.getAdditionalComments())
                .reportedOn(report.getReportedOn())
                .resolved(report.getResolved())
                .resolvedBy(report.getResolvedBy())
                .resolvedOn(report.getResolvedOn())
                .build();
    }
}

