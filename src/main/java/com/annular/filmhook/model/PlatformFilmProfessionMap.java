package com.annular.filmhook.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "platform_film_profession_map")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlatformFilmProfessionMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    @ToString.Exclude
    private Platform platform;

    @ManyToOne
    @JoinColumn(name = "film_profession_id", nullable = false)
    @ToString.Exclude
    private FilmProfession filmProfession;

    @Column(name = "status")
    private Boolean status;

}
