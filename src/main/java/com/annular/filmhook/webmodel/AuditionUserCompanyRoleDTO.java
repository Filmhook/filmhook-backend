package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditionUserCompanyRoleDTO {
    private Long id;
    private Long userId;
    private Long companyId;
    private String designation;
    private String status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
