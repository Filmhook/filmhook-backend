package com.annular.filmhook.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.LiveChannel;
import com.annular.filmhook.repository.LiveDetailsRepository;
import com.annular.filmhook.service.LiveStreamService;
import com.annular.filmhook.webmodel.LiveDetailsWebModel;

@Service
public class LiveStreamServiceImpl implements LiveStreamService {

	@Autowired
	LiveDetailsRepository liveDetailsRepository;

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
	public ResponseEntity<?> getLiveDetails(LiveDetailsWebModel liveDetailsWebModel) {
		try {
			Optional<LiveChannel> liveDetailsDB = liveDetailsRepository
					.findById(liveDetailsWebModel.getLiveChannelId());
			if (liveDetailsDB.isPresent()) {
				LiveChannel data = liveDetailsDB.get();
				return new ResponseEntity<>(data, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(
						"Live Detail with ID " + liveDetailsWebModel.getLiveChannelId() + " not found",
						HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Error fetching live Detaisl: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
