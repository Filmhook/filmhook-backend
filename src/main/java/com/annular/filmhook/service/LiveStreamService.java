package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.LiveDetailsWebModel;

public interface LiveStreamService {

	ResponseEntity<?> saveLiveDetails(LiveDetailsWebModel liveDetailsWebModel);

	ResponseEntity<?> getLiveDetails(LiveDetailsWebModel liveDetailsWebModel);

}
