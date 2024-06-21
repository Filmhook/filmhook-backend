package com.annular.filmhook.model;

import java.sql.Date;

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
@Table(name = "AddressList")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddressList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "address")
    private String address;

    @Column(name = "status", columnDefinition = "tinyint default true")
    private Boolean status;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;

}
