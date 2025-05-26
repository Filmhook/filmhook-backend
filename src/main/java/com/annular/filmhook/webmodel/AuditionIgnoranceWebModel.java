package com.annular.filmhook.webmodel;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditionIgnoranceWebModel {

    private Integer auditionIgnoranceId;
    private boolean isIgnoranceAccepted;
    private Integer auditionIgnoranceUser;
    private Integer auditionRefId;// Audition Id from audition table is saved, to have this loosely coupled
    private Integer auditionIgnoranceCreatedBy;
    private Date auditionIgnoranceCreatedOn;
    private Integer auditionIgnoranceUpdatedBy;
    private Date auditionIgnoranceUpdatedOn;

}
