package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BasicFeildsDTO {

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Integer createdBy;
    private Integer updatedBy;
    private Boolean status;
}