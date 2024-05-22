package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import com.annular.filmhook.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FilmProfession;

@Repository
public interface FilmProfessionRepository  extends JpaRepository<FilmProfession,Integer>{

	
	Optional<FilmProfession> findByProfessionName(String professionName);

	Optional<FilmProfession> findByPlatformAndProfessionName(Platform platform, String professionName);

	List<FilmProfession> findByPlatform(Platform platform);

	List<FilmProfession> findByProfessionNameAndPlatform(String upperCase, Platform platform);

	List<FilmProfession> findAllByProfessionName(String upperCase);

//	@Query("SELECT f FROM FilmProfession f WHERE f.professionName = :professionName")
//	FilmProfession findByProfessionNames(String professionName);
}
