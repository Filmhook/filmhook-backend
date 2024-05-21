package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FilmProfessions;
import com.annular.filmhook.webmodel.FilmWebModel;

@Repository
public interface FilmProfessionsRepository extends JpaRepository<FilmProfessions, Integer> {

	FilmProfessions findByProfessionName(String professionName);

	//FilmProfessions save(FilmWebModel filmProfessions);

}
