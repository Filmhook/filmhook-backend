package com.annular.filmhook.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.LiveChannel;
import com.annular.filmhook.model.LiveStreamComment;
import com.annular.filmhook.repository.LiveDetailsRepository;
import com.annular.filmhook.repository.LiveStreamCommentRepository;
import com.annular.filmhook.service.LiveStreamService;
import com.annular.filmhook.webmodel.LiveDetailsWebModel;
import com.annular.filmhook.webmodel.LiveStreamCommentWebModel;

@Service
public class LiveStreamServiceImpl implements LiveStreamService {

	@Autowired
	LiveDetailsRepository liveDetailsRepository;
	
	@Autowired
	LiveStreamCommentRepository liveStreamCommentRepository;

	@Override
	public ResponseEntity<?> saveLiveDetails(LiveDetailsWebModel liveDetailsWebModel) {
		try {

			LiveChannel liveDetails = new LiveChannel();
			liveDetails.setUserId(liveDetailsWebModel.getUserId());
			liveDetails.setChannelName(liveDetailsWebModel.getChannelName());
			liveDetails.setLiveIsActive(true);
//			liveDetails.setCreatedBy(liveDetailsWebModel.getCreatedBy());
			liveDetails.setToken(liveDetailsWebModel.getToken());
			liveDetails.setStartTime(liveDetailsWebModel.getStartTime());
			liveDetails.setEndTime(liveDetailsWebModel.getEndTime());
			liveDetails.setLiveDate(liveDetailsWebModel.getLiveDate());

			liveDetailsRepository.save(liveDetails);

			// Return a success response
			return ResponseEntity.ok(new Response(1, "Live Details saved Successfully", liveDetails));
		} catch (Exception e) {
			// Handle any exceptions or errors
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "failed to save live details", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> getLiveDetails() {
		try {
			List<LiveChannel> liveDetailsDB = liveDetailsRepository
					.findAll();
			
				return new ResponseEntity<>(liveDetailsDB, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>("Error fetching live Detaisl: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<?> saveLiveStreamComment(LiveStreamCommentWebModel liveStreamCommentWebModel) {
		try {

			LiveStreamComment liveDetails = new LiveStreamComment();
			liveDetails.setUserId(liveStreamCommentWebModel.getUserId());
			liveDetails.setLiveStreamMessage(liveStreamCommentWebModel.getLiveStreamMessage());
			liveDetails.setLiveStreamCommenIsActive(true);
			liveDetails.setLiveStreamCommencreatedBy(liveStreamCommentWebModel.getLiveStreamCommencreatedBy());
			liveDetails.setUserId(liveStreamCommentWebModel.getUserId());
			liveDetails.setLiveChannelId(liveStreamCommentWebModel.getLiveChannelId());
			
			liveStreamCommentRepository.save(liveDetails);

			// Return a success response
			return ResponseEntity.ok(new Response(1, "Live comment saved Successfully", liveDetails));
		} catch (Exception e) {
			// Handle any exceptions or errors
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "failed to save live details", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> getLiveCommentDetails(Integer liveChannelId) {
		try {
			List<LiveStreamComment> liveDetailsDB = liveStreamCommentRepository
					.findByLiveChannelId(liveChannelId);
			
				return new ResponseEntity<>(liveDetailsDB, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>("Error fetching live Detaisl: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
