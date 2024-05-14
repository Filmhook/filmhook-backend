package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.NotificationTypeEnum;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class NotificationWebModel {

    private Integer notificationId;

    private NotificationTypeEnum notificationType;
    private String message;
    private Integer notificationFrom;
    private Integer notificationTo;

    private Integer createdBy;
    private Date createdOn;

    private Integer updatedBy;
    private Date updatedOn;

}
