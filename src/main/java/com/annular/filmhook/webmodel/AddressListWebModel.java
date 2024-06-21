package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressListWebModel {

    private Integer id;
    private String address;
    private boolean status;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

}
