package com.annular.filmhook.service.impl;

import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Comment;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.ActionService;
import com.annular.filmhook.webmodel.CommentWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;

@Service
public class ActionServiceImpl implements ActionService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserDetails userDetails;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	LikeRepository likeRepository;

	private static final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

	@Override
	public ResponseEntity<?> addLike(LikeWebModel likeWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			Integer userId = userDetails.userInfo().getId();
			Integer postId = likeWebModel.getPostId();
			Optional<Likes> existingLike = likeRepository.findByUserIdAndPostId(userId, postId);
			if (existingLike.isPresent()) {
				logger.info("Like already exists for user {} and post {}", userId, postId);
				return ResponseEntity.ok(new Response(0, "Like already exists", null));
			}
			Likes like = new Likes();
			like.setUserId(userId);
			like.setStatus(true);
			like.setPostId(postId);
			like.setCreatedBy(userId);
			likeRepository.save(like);
			response.put("likeInfo", like);
			logger.info("addLike method end");
			return ResponseEntity.ok(new Response(1, "Add like successfully", response));
		} catch (Exception e) {
			logger.error("Error setting like {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error setting like", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> updateLike(LikeWebModel likeWebModel) {
		try {
			Optional<Likes> existingLikeOptional = likeRepository.findById(likeWebModel.getLikeId());
			if (existingLikeOptional.isPresent()) {
				Likes existingLike = existingLikeOptional.get();
				existingLike.setStatus(likeWebModel.getStatus());
				existingLike.setUpdatedBy(userDetails.userInfo().getId());
				Likes updatedLike = likeRepository.save(existingLike);
				return ResponseEntity.ok(new Response(1, "Update like successfully", updatedLike));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(-1, "Like not found", null));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error updating like", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> addComment(CommentWebModel commentWebModel) {
		try {
			Comment comment = new Comment();
			comment.setContent(commentWebModel.getContent());
			comment.setUserId(userDetails.userInfo().getId());
			comment.setPostId(commentWebModel.getPostId());
			comment.setStatus(true);

			Comment savedComment = commentRepository.save(comment);

			return ResponseEntity.ok(new Response(1, "Comment added successfully", savedComment));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error adding comment", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> deleteComment(CommentWebModel commentWebModel) {
		try {

			Optional<Comment> commentOptional = commentRepository.findById(commentWebModel.getCommentId());
			if (commentOptional.isPresent()) {

				commentRepository.deleteById(commentWebModel.getCommentId());

				return ResponseEntity.ok(new Response(1, "Comment deleted successfully", null));
			} else {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(-1, "Comment not found", null));
			}
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error deleting comment", e.getMessage()));
		}
	}
}
