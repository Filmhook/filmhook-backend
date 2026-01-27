package com.annular.filmhook.service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.webmodel.PromoteWebModel;

public interface PromoteAdService {

    PromoteAd savePromote(PromoteWebModel promoteWebModel, Integer userId);

    PromoteAd getPromoteByPostId(Integer postId);
    
    Response updatePaymentSuccess(PromoteWebModel model);

    Response updatePaymentFailed(PromoteWebModel model);

    Response getRecentPromotions(Integer userId);

	Response updateBeforePayment(PromoteWebModel model);
}