package com.annular.filmhook.model;

import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "FilmProfessions")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FilmProfessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filmPfnId")
    private Integer filmPfnId;

    @Column(name = "profession_name")
    private String professionName;

    @ElementCollection
    @CollectionTable(name = "Film_sub_professions", joinColumns = @JoinColumn(name = "filmPfnId"))
    @Column(name = "sub_professions_name")
    private List<String> subProfessionsName;

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