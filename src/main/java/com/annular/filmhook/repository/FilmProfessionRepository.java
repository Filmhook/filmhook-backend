package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FilmProfession;

@Repository
public interface FilmProfessionRepository extends JpaRepository<FilmProfession, Integer> {

    Optional<FilmProfession> findByProfessionName(String professionName);

}
