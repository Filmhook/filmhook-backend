package com.annular.filmhook.service;

import java.math.BigDecimal;
import java.util.List;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.model.VisitPageDetails;
import com.annular.filmhook.model.VisitePageCategory;
import com.annular.filmhook.webmodel.PromoteWebModel;
import com.annular.filmhook.webmodel.VisitPageWebModel;

public interface PromoteAdService {

    Response savePromote(PromoteWebModel promoteWebModel);

    PromoteAd getPromoteByPostId(Integer postId);
    
//    Response updatePaymentSuccess(String txnid, String promoteId, BigDecimal amount );
//
//    Response updatePaymentFailed(String txnid, String promoteId, BigDecimal amount );

    Response getRecentPromotions(Integer userId);

//	Response updateBeforePayment(PromoteWebModel model);

	List<VisitPageWebModel> getAllObjectives();
	
	List<VisitPageWebModel> getPagesByCategoryId(Integer categoryId);

	List<VisitPageWebModel> getDetailsByVisitPageId(Integer visitPageId);
	
	void activatePromote(Integer promoteId, String txnId);

	void markPromotePaymentFailed(Integer promoteId, String txnId);

}