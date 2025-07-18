package com.annular.filmhook.webmodel;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    private String type;
    private Long referenceId;
    private Long reporterUserId;
    private String reason;
    private String additionalComments;
}
