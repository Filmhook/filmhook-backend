package com.annular.filmhook.service;

import java.util.List;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.model.VisitPageDetails;
import com.annular.filmhook.model.VisitePageCategory;
import com.annular.filmhook.webmodel.PromoteWebModel;
import com.annular.filmhook.webmodel.VisitPageWebModel;

public interface PromoteAdService {

    Response savePromote(PromoteWebModel promoteWebModel, Integer userId);

    PromoteAd getPromoteByPostId(Integer postId);
    
    Response updatePaymentSuccess(PromoteWebModel model);

    Response updatePaymentFailed(PromoteWebModel model);

    Response getRecentPromotions(Integer userId);

//	Response updateBeforePayment(PromoteWebModel model);

	List<VisitPageWebModel> getAllObjectives();
	
	List<VisitPageWebModel> getPagesByCategoryId(Integer categoryId);

	List<VisitPageWebModel> getDetailsByVisitPageId(Integer visitPageId);

}