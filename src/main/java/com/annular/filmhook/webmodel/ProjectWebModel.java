package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectWebModel {

    private Integer userId;
    private Integer platformPermanentId;
    private FileInputWebModel fileInputWebModel;
    private String description;

}
