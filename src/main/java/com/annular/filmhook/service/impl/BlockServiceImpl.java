package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.FileOutputWebModel;

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
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.BlockRepository;
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
    BlockRepository blockRepository;

    @Autowired
    private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

	@Override
	public ResponseEntity<?> addBlock(BlockWebModel blockWebModel) {
		List<BlockWebModel> blockWebModels;
		try {
			User currentUser = User.builder().userId(blockWebModel.getBlockedBy()).build();
			User blockedUser = User.builder().userId(blockWebModel.getBlockedUser()).build();

			if(currentUser.getUserId().equals(blockedUser.getUserId()))
				return ResponseEntity.badRequest().body("Blocked By User and Blocked User cannot be the same...");

			Block existingBlockRow = blockRepository.findByBlockedByAndBlockedUserAndBlockStatus(currentUser, blockedUser, "UnBlocked");

			Block block = Objects.requireNonNullElseGet(existingBlockRow, Block::new);
			block.setBlockedBy(currentUser);
			block.setStatus(true);
			block.setBlockedUser(blockedUser);
			block.setBlockStatus("Blocked");
			block.setCreatedBy(userDetails.userInfo().getId());

			blockRepository.save(block);

			blockWebModels = this.transformBlockToBlockWebModel(List.of(block));
			return ResponseEntity.ok(new Response(1, "Add block successfully", blockWebModels));
		} catch (Exception e) {
			logger.error("Error setting block {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Error setting block", e.getMessage()));
		}

	}

    private List<BlockWebModel> transformBlockToBlockWebModel(List<Block> blockList) {
        List<BlockWebModel> blockWebModels = new ArrayList<>();
        try {
            if (!Utility.isNullOrEmptyList(blockList)) {
                blockList.stream()
                        .filter(Objects::nonNull)
                        .filter(block -> block.getBlockedUser() != null)
                        .forEach(block -> {
                            BlockWebModel blockWebModel = BlockWebModel.builder()
                                    .blockId(block.getBlockId())
                                    .blockedBy(block.getBlockedBy().getUserId())
                                    .blockedUser(block.getBlockedUser().getUserId())
                                    .blockStatus(block.getBlockStatus())
                                    .blockedUserName(block.getBlockedUser().getName())
                                    .blockedUserGender(block.getBlockedUser().getGender())
                                    .blockedUserProfilePicUrl(userService.getProfilePicUrl(block.getBlockedUser().getUserId()))
                                    .createdBy(block.getCreatedBy())
                                    .createdOn(block.getCreatedOn())
                                    .updatedBy(block.getUpdatedBy())
                                    .updatedOn(block.getUpdatedOn())
                                    .build();
                            blockWebModels.add(blockWebModel);
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockWebModels;
    }

	@Override
	public ResponseEntity<?> getAllBlock(Integer userId) {
		List<BlockWebModel> blockWebModels = new ArrayList<>();
		try {
			User userToSearch = User.builder().userId(userId != null ? userId : userDetails.userInfo().getId()).build();
			List<Block> blockData = blockRepository.findByBlockedByAndBlockStatus(userToSearch, "Blocked");
			return ResponseEntity.ok(this.transformBlockToBlockWebModel(blockData));
		} catch (Exception e) {
			logger.error("getAllBlock service Method Exception {} ", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}

	@Override
	public String unBlockProfile(BlockWebModel blockWebModel) {
		try {
			User currentUser = User.builder().userId(blockWebModel.getBlockedBy()).build();
			User blockedUser = User.builder().userId(blockWebModel.getBlockedUser()).build();
			Block blockRowToUpdate = blockRepository.findByBlockedByAndBlockedUser(currentUser, blockedUser);
			if (blockRowToUpdate != null) {
				blockRowToUpdate.setBlockStatus("UnBlocked");
				blockRepository.saveAndFlush(blockRowToUpdate);
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
