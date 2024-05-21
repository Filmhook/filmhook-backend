package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class ProjectWebModel {

    private Integer userId;
    private Integer platformPermanentId;
    private FileInputWebModel fileInputWebModel;
    private String description;
    }
