package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmSubProfession;

@Repository
public interface FilmProfessionRepository extends JpaRepository<FilmProfession, Integer> {

    Optional<FilmProfession> findByProfessionName(String professionName);
    @Query("SELECT p FROM FilmProfession p")
    List<FilmProfession> findAllProfessions();
    
    // exclude multiple FilmProfession IDs
    List<FilmProfession> findByFilmProfessionIdNotIn(List<Integer> professionIds);

}
