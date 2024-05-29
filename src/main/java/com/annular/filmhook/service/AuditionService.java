package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.AddressListWebModel;
import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionDetailsWebModel;
import com.annular.filmhook.webmodel.AuditionIgnoranceWebModel;
import com.annular.filmhook.webmodel.AuditionWebModel;

public interface AuditionService {

	ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel);

	ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel);

	ResponseEntity<?> auditionIgnorance(AuditionIgnoranceWebModel auditionIgnoranceWebModel);

	ResponseEntity<?> getAuditionDetails(AuditionDetailsWebModel auditionDetailsWebModel);

	ResponseEntity<?> getAddressList(AddressListWebModel addressListWebModel);

	ResponseEntity<?> getAuditionByCategory(Integer auditionCategory);

	ResponseEntity<?> getAuditionByFilterAddress(Integer auditionCategory, String searchKey);

}
