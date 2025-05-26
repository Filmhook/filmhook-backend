package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.NotificationTypeEnum;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
