package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;

import com.annular.filmhook.model.ShootingPropertyStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationPropertySummaryDTO {

    private Integer id;
    private String propertyName;
    private String fullName;
    private String propertyCode;
    private LocalDateTime approvedOn;
    private LocalDateTime createdOn;
    private ShootingPropertyStatus status;
}
