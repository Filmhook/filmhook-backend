package com.annular.filmhook.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.annular.filmhook.util.StringListConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audition_new_project")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditionNewProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "production_company_name")
    private String productionCompanyName;
    
    @Column(name = "project_title")
    private String projectTitle;

    // Country & Industry
    @Column(name = "country")
    private String country;
    
    @Convert(converter = StringListConverter.class)
    @Column(name = "industries")
    private List<String> industries;  // e.g. [Tollywood, Bollywood]
    

    @Column(name = "dubbed_country")
    private String dubbedCountry;  
    
    @Convert(converter = StringListConverter.class)
    @Column(name = "dubbed_industries")
    private List<String> dubbedIndustries;

    @Convert(converter = StringListConverter.class)
    @Column(name = "platforms")
    private List<String> platforms;   // e.g. [Movies, Web Series]

    @Convert(converter = StringListConverter.class)
    @Column(name = "movie_types")
    private List<String> movieTypes;

    @Convert(converter = StringListConverter.class)
    @Column(name = "theme_movie_types")
    private List<String> themeMovieTypes;

    @Convert(converter = StringListConverter.class)
    @Column(name = "shoot_locations")
    private List<String> shootLocations;

    @Column(name = "shoot_start_date")
    private LocalDate shootStartDate;
    
    @Column(name = "shoot_end_date")
    private LocalDate shootEndDate;

    @Column(name = "project_description", columnDefinition = "TEXT")
    private String projectDescription;
    
    @Column(name = "audition_profile_picture")
    private String auditionProfilePicture;

    // Relationship
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditionNewTeamNeed> teamNeeds;
    
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "company_id", nullable = false)
     private AuditionCompanyDetails company;
}
