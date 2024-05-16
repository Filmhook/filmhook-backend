package com.annular.filmhook.repository;

import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmSubProfession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmSubProfessionRepository extends JpaRepository<FilmSubProfession, Integer> {

    Optional<FilmSubProfession> findBySubProfessionName(String professionName);

    List<FilmSubProfession> findByProfession(FilmProfession profession);

    @Query("Select p From FilmSubProfession p Where p.profession in (:professionList) and p.status = true")
    List<FilmSubProfession> getSubProfessionByProfessionIds(List<FilmProfession> professionList);

	List<FilmSubProfession> findAllBySubProfessionName(String upperCase);

}
