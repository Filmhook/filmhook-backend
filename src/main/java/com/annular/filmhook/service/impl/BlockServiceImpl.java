package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Block;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.BlockRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.BlockService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.BlockWebModel;

@Service
public class BlockServiceImpl implements BlockService {

	@Autowired
	UserDetails userDetails;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BlockRepository blockRepository;
	
	@Autowired
	S3Util s3Util;
	
	@Autowired
	FileUtil fileUtil;
	
	@Autowired
	MediaFilesRepository mediaFilesRepository;

	private static final Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

	@Override
	public ResponseEntity<?> addBlock(BlockWebModel blockWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			Block block = new Block();
			block.setBlockedBy(userDetails.userInfo().getId());
			block.setStatus(true);
			block.setBlockedUser(blockWebModel.getBlockedUser());
			block.setBlockStatus("Blocked");
			block.setCreatedBy(userDetails.userInfo().getId());
			blockRepository.save(block);
			response.put("blockInfo", block);
			
			logger.info("addBlock method end");
			return ResponseEntity.ok(new Response(1, "Add block successfully", response));
		} catch (Exception e) {
			logger.error("Error setting block {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error setting block", e.getMessage()));
		}

	}

	@Override
	public ResponseEntity<?> getAllBlock() {
		try {
			logger.info("getAllBlockservice start");

			List<Block> blockData = blockRepository.findByBlockedBy(userDetails.userInfo().getId());
			List<LinkedHashMap<String, Object>> responseList = new ArrayList<>();
			for (Block block : blockData) {
				Integer blockUserId = block.getBlockedUser();
				if (blockUserId != null) {
					Optional<User> userOptional = userRepository.findById(blockUserId);

					if (userOptional.isPresent()) {
						User user = userOptional.get();
						Optional<MediaFiles> profilePicOptional = mediaFilesRepository
								.findByUserIdAndCategory(user.getUserId(), MediaFileCategory.ProfilePic);

						LinkedHashMap<String, Object> pinData = new LinkedHashMap<>();
						pinData.put("blockUserId", block.getBlockedUser());
						pinData.put("userId", block.getBlockedBy());;
						pinData.put("userName", user.getName());
						pinData.put("userGender", user.getGender());
						 if (profilePicOptional.isPresent()) {
		                        MediaFiles mediaFiles = profilePicOptional.get();
		                        pinData.put("filePathProfile", mediaFiles.getFilePath());
		                        pinData.put("fileNameProfile", mediaFiles.getFileName());
		                        pinData.put("fileNameSize", mediaFiles.getFileSize());
		                        pinData.put("fileNameTypeProfile", mediaFiles.getFileType());
		                        pinData.put("profilePicUrl", s3Util.getS3BaseURL() + S3Util.S3_PATH_DELIMITER
		                            + mediaFiles.getFilePath() + mediaFiles.getFileType());
		                    } else {
		                        // Handle case where profile picture is not found
		                        pinData.put("profilePicUrl", null);
		                    }
						responseList.add(pinData);
					} else {
						logger.warn("User not found for blockUserId: " + blockUserId);
					}
				} else {
					logger.warn("blockUserId is null for userProfilePin: " + block);
				}
			}
			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			logger.error("getAllBlock service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}
	@Override
	public String unBlockProfile(BlockWebModel blockWebModel) {
		try {
			Integer currentUser = (userDetails != null && userDetails.userInfo() != null) ? userDetails.userInfo().getId() : null;
			Block blockedUser = blockRepository.findByBlockedByAndBlockedUser(currentUser, blockWebModel.getBlockedUser());
			if (blockedUser != null) {
				blockedUser.setBlockStatus("UnBlocked");
				blockRepository.saveAndFlush(blockedUser);
				return "Profile unblocked successfully...";
			} else {
				return "Blocked profile not found...";
			}

		} catch (Exception e) {
			logger.error("Error at unBlockProfile -> {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
