package com.annular.filmhook.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.model.AuditionSubDetails;
import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionDetailsWebModel;
import com.annular.filmhook.webmodel.AuditionIgnoranceWebModel;
import com.annular.filmhook.webmodel.AuditionWebModel;

public interface AuditionService {

    ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel);

    ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel);

    ResponseEntity<?> auditionIgnorance(AuditionIgnoranceWebModel auditionIgnoranceWebModel);

    ResponseEntity<?> getAuditionDetails(AuditionDetailsWebModel auditionDetailsWebModel);

    ResponseEntity<?> getAllAddressList();

    ResponseEntity<?> getAddressList(String address);

    ResponseEntity<?> getAuditionByCategory(Integer auditionCategory);

    ResponseEntity<?> getAuditionByFilterAddress(Integer auditionCategory, String searchKey);

    ResponseEntity<?> deleteAuditionById(Integer auditionId, Integer userId);

    ResponseEntity<?> updateAudition(AuditionWebModel auditionWebModel);

	ResponseEntity<?> getAcceptanceDetailsByUserId(AuditionWebModel auditionWebModel);

	ResponseEntity<?> getAuditionByUserId(AuditionWebModel auditionWebModel);

	ResponseEntity<?> getAuditionAcceptanceListByUserId(AuditionWebModel auditionWebModel);

	void updatePaymentStatus(String txnid, String string, String mihpayid, String amount);
	
	ResponseEntity<?> getSubDetailsByAuditionDetailsId(Integer AuditionDetailsId);
	
	ResponseEntity<?> getAuditionBySubCategory(Integer subCategoryId);

}
