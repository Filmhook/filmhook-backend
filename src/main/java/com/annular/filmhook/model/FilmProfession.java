package com.annular.filmhook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;


@Entity
@Table(name = "FilmProfession")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmProfession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_profession_id")
    private Integer filmProfessionId;

    @Column(name = "profession_name")
    private String professionName;

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
    @JsonIgnore
    @ToString.Exclude
    private byte[] image;

    @Column(name = "icon_file_path")
    private String filePath;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profession")
    @ToString.Exclude
    @JsonIgnore
    private Collection<FilmSubProfession> filmSubProfessionCollection;

}
