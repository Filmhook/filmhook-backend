package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.LiveDetailsWebModel;
import com.annular.filmhook.webmodel.LiveStreamCommentWebModel;

public interface LiveStreamService {

	ResponseEntity<?> saveLiveDetails(LiveDetailsWebModel liveDetailsWebModel);

	ResponseEntity<?> getLiveDetails();

	ResponseEntity<?> saveLiveStreamComment(LiveStreamCommentWebModel liveStreamCommentWebModel);

	ResponseEntity<?> getLiveCommentDetails(Integer liveChannelId);

	ResponseEntity<?> getAllLiveChannelId();

}
