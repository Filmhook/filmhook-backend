package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ScheduleWebModel {

    private Integer scheduleId;

    private Integer scheduledBy; // Current logged in user id
    private Integer scheduledTo; // User id to be booked their dates

    private String projectName;

    private String fromDate;
    private String toDate;

    private Boolean active;

    private Integer createdBy;
    private Date createdOn;

}
