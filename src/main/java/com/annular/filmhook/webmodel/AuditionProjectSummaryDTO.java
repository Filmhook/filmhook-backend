package com.annular.filmhook.webmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditionProjectSummaryDTO {
    private Integer id;
    private String projectTitle;
    private String industry;
    private String category;
    private LocalDateTime postedOn;
    private LocalDateTime validTill;
    private String status;
}
