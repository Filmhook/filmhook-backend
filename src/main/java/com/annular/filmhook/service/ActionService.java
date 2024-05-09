package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.CommentWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;

public interface ActionService {

	ResponseEntity<?> addLike(LikeWebModel likeWebModel);

	ResponseEntity<?> updateLike(LikeWebModel likeWebModel);

	ResponseEntity<?> addComment(CommentWebModel commentWebModel);

	ResponseEntity<?> deleteComment(CommentWebModel commentWebModel);

	ResponseEntity<?> addShare(ShareWebModel shareWebModel);

	ResponseEntity<?> getComment(CommentWebModel commentWebModel);


}
