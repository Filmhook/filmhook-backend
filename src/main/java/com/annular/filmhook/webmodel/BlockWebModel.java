package com.annular.filmhook.webmodel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
public class BlockWebModel {

    private Integer blockId;;
    private Integer userId;//foreign key for user table 
    private Integer blockUserId;//foreign key for userTable 
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

}
