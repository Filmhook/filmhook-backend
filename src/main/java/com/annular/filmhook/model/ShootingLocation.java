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
@Table(name = "ShootingLocation")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShootingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shooting_location_id")
    private Integer shootingLocationId;

    @Column(name = "shooting_location_name")
    private String shootingLocationName;

    @Column(name = "shootint_location_description")
    private String shootingLocationDescription;

    @Column(name = "shooting_termsAnd_Condition")
    private String shootingTermsAndCondition;

    @Column(name = "Indoor_or_outdoor")
    private Boolean indoorOrOutdoorLocation;

    @Column(name = "location_url")
    private String locationUrl;

    @Column(name = "cost")
    private float cost;

    @Column(name = "hour_month_day")
    private String hourMonthDay;

    @Column(name = "shootint_location_isactive")
    private boolean shootingLocationIsactive;

    @Column(name = "shootint_location_created_by")
    private Integer shootingLocationCreatedBy;

    @Column(name = "shootint_location_createdon")
    @CreationTimestamp
    private Date shootingLocationCreatedOn;

    @Column(name = "shootint_location_updated_by")
    private Integer shootingLocationUpdatedBy;

    @Column(name = "shootint_location_updatedon")
    @CreationTimestamp
    private Date shootingLocationUpdatedOn;

    @Column(name = "userId")
    private Integer userId;

}
