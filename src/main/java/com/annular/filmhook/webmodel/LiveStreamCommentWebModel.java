package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveStreamCommentWebModel {

    private Integer liveStreamCommentId;
    private Integer liveChannelId;//foreign key of liveChannelId
    private Integer userId;// foreign key for user table
    private String liveStreamMessage;
    private Boolean liveStreamCommenIsActive;
    private Integer liveStreamCommencreatedBy;
    private Date liveStreamCommenCreatedOn;
    private Integer liveStreamCommenUpdatedBy;
    private Date liveStreamCommenUpdatedOn;
    private String liveId;

}



