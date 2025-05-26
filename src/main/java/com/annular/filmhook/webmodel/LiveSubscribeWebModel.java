package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveSubscribeWebModel {

    private Integer liveChannelId;
    private Integer userId;
    private Boolean liveSubscribeIsActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String startTime;
    private String endTime;

}



