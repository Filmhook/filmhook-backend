package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.LiveSubscribeWebModel;

public interface LiveSubscribeService {

	ResponseEntity<?> saveLiveSubscribe(LiveSubscribeWebModel liveSubscribeWebModel);

	ResponseEntity<?> getLiveSubcribes(LiveSubscribeWebModel liveSubscribeWebModel);

}
