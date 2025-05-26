package com.annular.filmhook.webmodel;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditionDetailsWebModel {

    private Integer auditionDetailsId;
    private String auditionDetailsName;// Audition Id from audition table is saved, to have this loosely coupled
    private Integer auditionDetailsCreatedBy;
    private Date auditionDetailsCreatedOn;
    private Integer auditionDetailsUpdatedBy;
    private Date auditionDetailsUpdatedOn;


}
