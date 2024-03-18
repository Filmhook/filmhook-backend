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
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.BlockRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.BlockService;
import com.annular.filmhook.webmodel.BlockWebModel;

@Service
public class BlockServiceImpl implements BlockService {

	@Autowired
	UserDetails userDetails;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BlockRepository blockRepository;

	private static final Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

	@Override
	public ResponseEntity<?> addBlock(BlockWebModel blockWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			Block block = new Block();
			block.setUserId(userDetails.userInfo().getId());
			block.setStatus(true);
			block.setBlockUserId(blockWebModel.getBlockUserId());
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

			List<Block> blockData = blockRepository.findByUserId(userDetails.userInfo().getId());
			List<LinkedHashMap<String, Object>> responseList = new ArrayList<>();
			for (Block block : blockData) {
				Integer blockUserId = block.getBlockUserId();
				if (blockUserId != null) {
					Optional<User> userOptional = userRepository.findById(blockUserId);

					if (userOptional.isPresent()) {
						User user = userOptional.get();
						LinkedHashMap<String, Object> pinData = new LinkedHashMap<>();
						pinData.put("blockUserId", block.getBlockUserId());
						pinData.put("userId", block.getUserId());
						pinData.put("userName", user.getName());
						pinData.put("userGender", user.getGender());
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
}
