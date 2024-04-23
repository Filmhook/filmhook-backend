package com.annular.filmhook.model;

import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "FilmProfession")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FilmProfession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_profession_id")
    private Integer filmProfesssionId;

    @Column(name = "profession_name")
    private String professionName;

    @ElementCollection
    @CollectionTable(name = "Film_sub_professions", joinColumns = @JoinColumn(name = "film_profession_id"))
    @Column(name = "sub_profession_name")
    private List<String> subProfessionName;

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

    @Lob
    @Column
    private byte[] image;
}
