package com.annular.filmhook.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.Profession;
import com.annular.filmhook.model.SubProfesssion;
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
