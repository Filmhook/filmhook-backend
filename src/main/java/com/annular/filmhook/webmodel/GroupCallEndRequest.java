package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class GroupCallEndRequest {
    private Integer groupCallId;
    private Integer hostId;
}