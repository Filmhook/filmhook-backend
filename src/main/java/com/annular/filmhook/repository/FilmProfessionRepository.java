package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FilmProfession;

@Repository
public interface FilmProfessionRepository  extends JpaRepository<FilmProfession,Integer>{

	@Query("SELECT f FROM FilmProfession f WHERE f.professionName = :professionName")
	FilmProfession findByProfessionName(String professionName);

	@Query("SELECT f FROM FilmProfession f WHERE f.professionName = :professionName")
	Optional<FilmProfession> findByProfesssionName(String professionName);


}
