package com.annular.filmhook.service;
import java.util.List;

import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.webmodel.FilmProfessionResponseDTO;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
public interface AuditionNewService {
	List<MovieCategory> getAllCategories();
	List<MovieSubCategory> getSubCategories(Integer categoryId);
	List<FilmSubProfessionResponseDTO> getAllSubProfessions();
	List<FilmSubProfessionResponseDTO> getSubProfessionsByProfessionId(Integer professionId);
	List<FilmSubProfessionResponseDTO> getCart(Integer userId, Integer companyId);
	void addToCart(Integer userId, Integer companyId, Integer subProfessionId, Integer count);
	List<FilmProfessionResponseDTO> getAllProfessions();
	AuditionNewProject createProject(AuditionNewProjectWebModel projectDto);
	List<AuditionNewProjectWebModel> getProjectsBySubProfession(Integer subProfessionId);
	List<AuditionNewProjectWebModel> getProjectsByCompanyIdAndTeamNeed(Integer companyId, Integer teamNeedId);
	String toggleTeamNeedLike(Integer teamNeedId, Integer userId);
	void addView(Integer teamNeedId, Integer userId);
	int getViewCount(Integer teamNeedId);
}



