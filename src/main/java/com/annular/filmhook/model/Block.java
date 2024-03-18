package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "block")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Block {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "block_id")
    private Integer blockId;;

    @Column(name = "user_Id")
    private Integer userId;//foreign key for user table 

    @Column(name = "block_user_Id")
    private Integer blockUserId;//foreign key for userTable 
    
    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;


}
