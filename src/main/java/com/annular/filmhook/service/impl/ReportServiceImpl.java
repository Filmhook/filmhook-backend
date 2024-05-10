package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.ReportPost;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserMediaPin;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.ReportRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.ReportService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.ReportPostWebModel;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	UserDetails userDetails;
	
	@Autowired
	S3Util s3Util;
	
	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ReportRepository reportRepository;
	
	@Autowired
	MediaFilesRepository mediaFilesRepository;

	private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

	@Override
	public ResponseEntity<?> addPostReport(ReportPostWebModel reportPostWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			ReportPost reportPost = new ReportPost();
			reportPost.setUserId(userDetails.userInfo().getId());
			reportPost.setPostId(reportPostWebModel.getPostId());
			reportPost.setReason(reportPostWebModel.getReason());
			reportPost.setStatus(false); // Assuming initially report status is false
			reportPost.setCreatedBy(userDetails.userInfo().getId()); // Assuming user who reports is the creator
			reportRepository.save(reportPost);
			response.put("reportInfo", reportPost);
			logger.info("addMethod method end");
			return ResponseEntity.ok(new Response(1, "Add ReportPost successfully", response));
		} catch (Exception e) {
			logger.error("Error setting reportPost {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error setting reportPost", e.getMessage()));
		}

	}
	
	@Override
	public ResponseEntity<?> getAllPostReport() {
	    try {
	        List<ReportPost> reportPosts = reportRepository.findAll();
	        List<Map<String, Object>> combinedDetailsList = new ArrayList<>(); // List to hold combined details

	        for (ReportPost reportPost : reportPosts) {
	            Optional<MediaFiles> mediaFileOptional = mediaFilesRepository.findById(reportPost.getPostId());
	            
	            if (mediaFileOptional.isPresent()) {
	                MediaFiles mediaFiles = mediaFileOptional.get();
	               
	                Optional<User> userOptional = userRepository.findById(reportPost.getUserId());
	                
	                if (userOptional.isPresent()) {
	                    User user = userOptional.get();
	                    
	                    Map<String, Object> combinedDetails = new HashMap<>();
	                    combinedDetails.put("reportDetails", reportPost);
	                    combinedDetails.put("filename", mediaFiles.getFileName());
	                    combinedDetails.put("fileType", mediaFiles.getFileType());
	                    combinedDetails.put("filepath", mediaFiles.getFilePath());
	                    combinedDetails.put("userName", user != null ? user.getName() : null);
	                    combinedDetails.put("fileDescription", mediaFiles.getDescription());
	                    combinedDetails.put("fileUrl", s3Util.getS3BaseURL() + S3Util.S3_PATH_DELIMITER
	                            + mediaFiles.getFilePath() + mediaFiles.getFileType());
	                    
	                    combinedDetailsList.add(combinedDetails); // Add combined details to the list
	                }
	            }
	        }
	        
	        return new ResponseEntity<>(combinedDetailsList, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Error fetching post reports: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	@Override
	public ResponseEntity<?> getByPostReportId(ReportPostWebModel reportPostWebModel) {
		  try {
	            Optional<ReportPost> optionalReportPost = reportRepository.findById(reportPostWebModel.getReportPostId());
	            if (optionalReportPost.isPresent()) {
	                ReportPost reportPost = optionalReportPost.get();
	                return new ResponseEntity<>(reportPost, HttpStatus.OK);
	            } else {
	                return new ResponseEntity<>("Report with ID " + reportPostWebModel.getReportPostId() + " not found", HttpStatus.NOT_FOUND);
	            }
	        } catch (Exception e) {
	            return new ResponseEntity<>("Error fetching post report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    
	}

}
