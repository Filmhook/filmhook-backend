package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class BookingWebModel {

    private Integer bookingId;

    private Integer currentUserId; // Current logged in user id
    private Integer bookingUserId; // User id to be booked their dates

    private String projectName;
    private String bookingStatus; // 'Pending' --> 'Confirm' or 'Rejected'
    private String fromDate;
    private String toDate;

    private Boolean active;

    private String errorMsg;

    private Integer createdBy;
    private Date createdOn;

}
