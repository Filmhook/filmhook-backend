package com.annular.filmhook.webmodel;

import java.sql.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlaceWebModel {

    private Integer marketPlaceId;
    private String companyName;
    private String productName;
    private String productDescription;
    private String newProduct;
    private Boolean rentalOrsale;
    private Integer count;
    private Integer cost;
    private boolean marketPlaceIsactive;
    private Integer marketPlaceCreatedBy;
    private Date marketPlaceCreatedOn;
    private Integer userId;

    private FileInputWebModel fileInputWebModel; // for file input details
    private List<FileOutputWebModel> fileOutputWebModel; // for file output details
}
