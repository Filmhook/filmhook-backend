package com.annular.filmhook.service;

import java.util.List;

import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.webmodel.FilmProfessionResponseDTO;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;

public interface AuditionNewService {
	List<MovieCategory> getAllCategories();
	List<MovieSubCategory> getSubCategories(Integer categoryId);
	List<FilmSubProfessionResponseDTO> getAllSubProfessions();
	List<FilmSubProfessionResponseDTO> getSubProfessionsByProfessionId(Integer professionId);
	List<FilmSubProfessionResponseDTO> getCart(Integer userId, Integer companyId);
	void addToCart(Integer userId, Integer companyId, Integer subProfessionId, Integer count);
	 List<FilmProfessionResponseDTO> getAllProfessions();
}
