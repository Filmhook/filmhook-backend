package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    private Long id;
    private String type;
    private Long referenceId;
    private Long reporterUserId;
    private String reason;
    private String additionalComments;
    private LocalDateTime reportedOn;
    private Boolean resolved;
    private Long resolvedBy;
    private LocalDateTime resolvedOn;
}