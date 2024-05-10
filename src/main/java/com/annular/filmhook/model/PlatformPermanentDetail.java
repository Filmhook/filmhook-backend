package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "platform_permanent_detail")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class PlatformPermanentDetail {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platform_permanent_id")
    private Integer platformPermanentId;
    
    @Column(name = "platform_name")
    private String platformName;

    @Column(name = "ppd_platform_id")
    private Integer ppdPlatformId;

    @Column(name = "film_count")
    private Integer filmCount;
    
    @Column(name = "netWorth")
    private Integer netWorth;
    
    @Column(name = "dailySalary")
    private Integer dailySalary;

    @OneToMany(mappedBy = "platformPermanentDetail", cascade = CascadeType.ALL)
    private List<FilmProfessionPermanentDetail> professionDetails;

    @ManyToOne
    @JoinColumn(name = "iupd_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private IndustryUserPermanentDetails industryUserPermanentDetails;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Platform platform;

    @Column(name = "userId")
    private Integer userId;
}
