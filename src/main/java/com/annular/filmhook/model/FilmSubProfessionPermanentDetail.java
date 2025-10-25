package com.annular.filmhook.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * Permanent data with user id table for FilmSubProfession
 */

@Entity
@Table(name = "film_sub_profession_permanent_detail")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FilmSubProfessionPermanentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_profession_permanent_id")
    private Integer professionPermanentId;

    @Column(name = "sub_profession_name")
    private String professionName;

    @Column(name = "ppd_profession_id")
    private Integer ppdProfessionId;

    @ManyToOne
    @JoinColumn(name = "industry_permanent_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private IndustryUserPermanentDetails industryUserPermanentDetails;

    @ManyToOne
    @JoinColumn(name = "platform_permanent_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private PlatformPermanentDetail platformPermanentDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_permanent_id", nullable = false)
    @JsonBackReference("profession-sub")
    private FilmProfessionPermanentDetail filmProfessionPermanentDetail;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "film_sub_profession_id", nullable = false)
    @JsonIgnore
    private FilmSubProfession filmSubProfession;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "starting_year")
    private Integer startingYear;

    @Column(name = "ending_year")
    private Integer endingYear;

    @Column(name = "status")
    private Boolean status;

}
