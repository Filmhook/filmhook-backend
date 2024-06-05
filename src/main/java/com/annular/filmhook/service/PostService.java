package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.CommentWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.LinkWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface PostService {

    PostWebModel savePostsWithFiles(PostWebModel postWebModel);

    List<PostWebModel> getPostsByUserId(Integer userId) throws IOException;

    PostWebModel getPostByPostId(String postId);

    Resource getPostFile(Integer userId, String category, String fileId, String fileType);

    Resource getAllPostByUserIdAndCategory(Integer userId, String category);

    Resource getAllPostFilesByCategory(String category);

    List<PostWebModel> getAllUsersPosts();

    LikeWebModel addOrUpdateLike(LikeWebModel likeWebModel);

    CommentWebModel addComment(CommentWebModel commentWebModel);

    List<CommentWebModel> getComment(CommentWebModel commentWebModel);

    CommentWebModel deleteComment(CommentWebModel commentWebModel);

    ShareWebModel addShare(ShareWebModel shareWebModel);

	LinkWebModel addLink(LinkWebModel linkWebModel);

	List<PostWebModel> getPostsByUserIds(Integer userId);

	CommentWebModel updateComment(CommentWebModel commentWebModel);

}
