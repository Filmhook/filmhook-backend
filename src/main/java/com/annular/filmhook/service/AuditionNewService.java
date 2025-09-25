package com.annular.filmhook.service;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.webmodel.FilmProfessionResponseDTO;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.AuditionPayment;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
import com.annular.filmhook.webmodel.AuditionPaymentDTO;
import com.annular.filmhook.webmodel.AuditionPaymentWebModel;
public interface AuditionNewService {
	List<MovieCategory> getAllCategories();
	List<MovieSubCategory> getSubCategories(Integer categoryId);
	List<FilmSubProfessionResponseDTO> getAllSubProfessions();
	List<FilmSubProfessionResponseDTO> getSubProfessionsByProfessionId(Integer professionId);
	List<FilmSubProfessionResponseDTO> getCart(Integer userId, Integer companyId);
	void addToCart(Integer userId, Integer companyId, Integer subProfessionId, Integer count);
	List<FilmProfessionResponseDTO> getAllProfessions();
//	AuditionNewProject createProject(AuditionNewProjectWebModel projectDto);
	List<AuditionNewProjectWebModel> getProjectsBySubProfession(Integer subProfessionId);
	List<AuditionNewProjectWebModel> getProjectsByCompanyIdAndTeamNeed(Integer companyId, Integer teamNeedId,Integer professionId);
	String toggleTeamNeedLike(Integer teamNeedId, Integer userId);
	void addView(Integer teamNeedId, Integer userId);
	int getViewCount(Integer teamNeedId);

	AuditionPayment createPayment(AuditionPaymentWebModel webModel);  
	ResponseEntity<?> paymentSuccess(String txnid);
	ResponseEntity<?> paymentFailure(String txnid, String errorMessage);
	ResponseEntity<?> getPaymentByTxnid(String txnid);
	AuditionPaymentDTO calculateAuditionPayment(Integer projectId, Integer userId, Integer selectedDays);
	void softDeleteTeamNeed(Integer teamNeedId, Integer userId, Integer companyId);
	
	void updateExpiredPaymentsAndProjects();
	AuditionNewProject saveOrUpdateProject(AuditionNewProjectWebModel projectDto);
}



