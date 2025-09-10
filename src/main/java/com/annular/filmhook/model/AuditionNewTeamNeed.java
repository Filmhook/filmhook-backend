package com.annular.filmhook.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.annular.filmhook.util.StringListConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audition_new_team_need")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditionNewTeamNeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name")
    private String role;
    
    @Column(name = "count")
    private Integer count;

    // Character details
    @Column(name = "character_name")
    private String characterName;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "age_from")
    private Integer ageFrom;
    
    @Column(name = "age_to")
    private Integer ageTo;
    
    @Column(name = "ethnicity")
    private String ethnicity;
    
    @Column(name = "height_min")
    private Double heightMin;
    
    @Column(name = "height_max")
    private Double heightMax;
    
    @Column(name = "body_type")
    private String bodyType;
    
    @Convert(converter = StringListConverter.class)
    @Column(name = "regional_demonyms")
    private List<String> regionalDemonyms;

    // Opportunities
    @Column(name = "opportunity")
    private String opportunity; // PUBLIC, INDUSTRY, BOTH
    
    @Column(name = "experience_years")
    private Integer experienceYears;

    @Convert(converter = StringListConverter.class)
    @Column(name = "roles_responsibilities", columnDefinition = "TEXT")
    private List<String> rolesResponsibilities;

    // Payment
    @Column(name = "salary")
    private Double salary;
    
    @Column(name = "salary_type")
    private String salaryType;   // Per Day / Week / Month
    
    @Column(name = "payment_mode")
    private String paymentMode;  // Cash / Online / Bank
    
    @Column(name = "work_days")
    private Integer workDays;
    
    @Column(name = "views")
    private Integer views = 0;
    
    private Boolean status;
    private Integer createdBy;
    private Integer updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Convert(converter = StringListConverter.class)
    @Column(name = "facilities_provided") 
    private List<String> facilitiesProvided;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private AuditionNewProject project;
    
    @ManyToOne
    @JoinColumn(name = "sub_profession_id", referencedColumnName = "sub_profession_id")
    private FilmSubProfession subProfession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_id", referencedColumnName = "film_profession_id")
    private FilmProfession profession;
    
    
}
