package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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

            if (currentUser.getUserId().equals(blockedUser.getUserId()))
                return ResponseEntity.badRequest().body("Blocked By User and Blocked User cannot be the same...");

            // Check if the block already exists
            Block existingBlockRow = blockRepository.findByBlockedByAndBlockedUser(currentUser, blockedUser);

            if (existingBlockRow != null) {
                // Toggle the block status
                if (existingBlockRow.getBlockStatus().equals("Blocked")) {
                    existingBlockRow.setBlockStatus("UnBlocked");
                    existingBlockRow.setStatus(false);
                } else {
                    existingBlockRow.setBlockStatus("Blocked");
                    existingBlockRow.setStatus(true);
                }
                existingBlockRow.setCreatedBy(userDetails.userInfo().getId());
                blockRepository.save(existingBlockRow);

                blockWebModels = this.transformBlockToBlockWebModel(List.of(existingBlockRow));
                return ResponseEntity.ok(new Response(1, "Block status toggled successfully", blockWebModels));
            } else {
                // Create a new block entry
                Block newBlock = Block.builder()
                        .blockedBy(currentUser)
                        .blockedUser(blockedUser)
                        .blockStatus("Blocked")
                        .status(true)
                        .createdBy(userDetails.userInfo().getId())
                        .build();

                blockRepository.save(newBlock);

                blockWebModels = this.transformBlockToBlockWebModel(List.of(newBlock));
                return ResponseEntity.ok(new Response(1, "Add block successfully", blockWebModels));
            }
        } catch (Exception e) {
            logger.error("Error setting block {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new Response(-1, "Error setting block", e.getMessage()));
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
                                    .userType(block.getBlockedUser().getUserType())
                                    .review(block.getBlockedUser().getAdminReview())
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
			blockWebModels = this.transformBlockToBlockWebModel(blockData);
            return ResponseEntity.ok(blockWebModels);
        } catch (Exception e) {
            logger.error("getAllBlock service Method Exception {} ", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
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
