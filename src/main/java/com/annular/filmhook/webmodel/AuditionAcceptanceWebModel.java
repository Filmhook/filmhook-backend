package com.annular.filmhook.webmodel;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditionAcceptanceWebModel {

    private Integer auditionAcceptanceId;

    private boolean isAuditionAccepted;

    private Integer auditionAcceptanceUser;

    private Integer auditionRefId;//Audition Id from audition table is saved, to have this loosely coupled

    private Integer auditionAcceptanceCreatedBy;

    private Date auditionAcceptanceCreatedOn;

    private Integer auditionAcceptanceUpdatedBy;

    private Date auditionAcceptanceUpdatedOn;

}
