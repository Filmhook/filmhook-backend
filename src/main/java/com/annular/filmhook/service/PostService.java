package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.PostWebModel;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface PostService {

    PostWebModel savePostsWithFiles(PostWebModel postWebModel);

    List<PostWebModel> getPostsByUserId(Integer userId) throws IOException;

    Resource getPostFile(Integer userId, String category, String fileId);

    Resource getAllPostByUserIdAndCategory(Integer userId, String category);

    Resource getAllPostFilesByCategory(String category);

    List<PostWebModel> getAllUsersPosts();

}
