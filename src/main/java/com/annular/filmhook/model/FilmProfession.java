package com.annular.filmhook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Master data table for Film's Profession
 */

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

    /*@ElementCollection
    @CollectionTable(name = "Film_sub_professions", joinColumns = @JoinColumn(name = "film_profession_id"))
    @Column(name = "sub_profession_name")
    private List<String> subProfessionName;*/

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

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    @ToString.Exclude
    private Platform platform;

    @Column(name = "icon_file_path")
    private String filePath;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval=true, mappedBy = "profession")
    @ToString.Exclude
    private Collection<FilmSubProfession> filmSubProfessionCollection;

}
