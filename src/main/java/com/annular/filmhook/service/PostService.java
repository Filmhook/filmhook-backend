package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.CommentInputWebModel;
import com.annular.filmhook.webmodel.CommentOutputWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.LinkWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface PostService {

    PostWebModel savePostsWithFiles(PostWebModel postWebModel);

    List<PostWebModel> getPostsByUserId(Integer userId, Integer pageNo, Integer pageSize) throws IOException;

    PostWebModel getPostByPostId(String postId);

    Resource getPostFile(Integer userId, String category, String fileId, String fileType);

    Resource getAllPostByUserIdAndCategory(Integer userId, String category);

    Resource getAllPostFilesByCategory(String category);

    List<PostWebModel> getAllUsersPosts(Integer pageNo, Integer pageSize);

    LikeWebModel addOrUpdateLike(LikeWebModel likeWebModel);

    CommentOutputWebModel addComment(CommentInputWebModel commentInputWebModel);

    List<CommentOutputWebModel> getComment(CommentInputWebModel commentInputWebModel);

    CommentOutputWebModel deleteComment(CommentInputWebModel commentInputWebModel);

    ShareWebModel addShare(ShareWebModel shareWebModel);

    LinkWebModel addLink(LinkWebModel linkWebModel);

    List<PostWebModel> getPostsByUserIds(Integer userId);

    CommentOutputWebModel updateComment(CommentInputWebModel commentInputWebModel);
    
	boolean deletePostByUserId(PostWebModel postWebModel);


}
