package com.annular.filmhook.model;


import java.sql.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;

@Entity
@Table(name = "audition_sub_details")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuditionSubDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_id")
    private Integer subId;

    @Column(name = "sub_name")
    private String subName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_details_id", nullable = false)
    @JsonBackReference
    private AuditionDetails auditionDetails;

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

