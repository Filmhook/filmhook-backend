package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HelpAndSupportWebModel {

    private Integer helpAndSupportId;
    private Integer userId;
    private Boolean helpAndSupportIsActive;
    private Integer helpAndSupportCreatedBy;
    private Integer helpAndSupportUpdatedBy;
    private Date helpAndSupportCreatedOn;
    private Date helpAndSupportUpdatedOn;
    private String message;
    private String subject;
    private String receipentEmail;

}
