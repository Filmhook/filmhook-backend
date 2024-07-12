package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.PostService;

import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.LinkWebModel;
import com.annular.filmhook.webmodel.CommentInputWebModel;
import com.annular.filmhook.webmodel.CommentOutputWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/user/post")
public class PostController {

    public static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    PostService postService;

    @RequestMapping(path = "/savePost", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response savePost(@ModelAttribute PostWebModel inputFileData) {
        try {
            logger.info("savePost Inputs :- {}", inputFileData);
            PostWebModel outputFileData = postService.savePostsWithFiles(inputFileData);
            if (outputFileData != null) return new Response(1, "Post saved successfully...", outputFileData);
        } catch (Exception e) {
            logger.error("Error at savePost() -> {}", e.getMessage());
            return new Response(-1, "Error occurred while saving post with files -> {}", e.getMessage());
        }
        return new Response(-1, "Error occurred while saving post with files...", null);
    }

    @GetMapping("/downloadPostFile")
    public ResponseEntity<?> downloadPostFile(@RequestParam("userId") Integer userId,
                                              @RequestParam("category") String category,
                                              @RequestParam("fileId") String fileId,
                                              @RequestParam("fileType") String fileType) {
        try {
            logger.info("downloadPostFile Input Category :- {}, File Id :- {}", category, fileId);
            Resource resource = postService.getPostFile(userId, category, fileId, fileType);
            if (resource != null) {
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + fileId + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadPostFile() -> {}", e.getMessage());
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/getPostsByUserId")
    public Response getPostsByUserId(@RequestParam("userId") Integer userId) {
        try {
            List<PostWebModel> outputList = postService.getPostsByUserId(userId);
            if (!Utility.isNullOrEmptyList(outputList)) return new Response(1, "Post(s) found successfully...", outputList);
            else return new Response(-1, "No file(s) available for this user...", null);
        } catch (Exception e) {
            logger.error("Error at getPostsByUserId() -> {}", e.getMessage());
        }
        return new Response(-1, "Post files were not found...", null);
    }

    @GetMapping("/view/{postId}")
    public Response getPostsByPostId(@PathVariable String postId) {
        try {
            PostWebModel output = postService.getPostByPostId(postId);
            if (output != null) return new Response(1, "Post(s) found successfully...", output);
            else return new Response(-1, "No Post(s) available...", null);
        } catch (Exception e) {
            logger.error("Error at getPostsByPostId() -> {}", e.getMessage());
        }
        return new Response(-1, "Post files were not found...", null);
    }

    @GetMapping("/downloadUserPostFiles")
    public ResponseEntity<?> downloadPostFiles(@RequestParam("userId") Integer userId, @RequestParam("category") String category) {
        try {
            logger.info("downloadPostFiles Input Category :- {}", category);
            Resource resource = postService.getAllPostByUserIdAndCategory(userId, category);
            if (resource != null) {
                // Determine content type
                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                String contentType = "application/octet-stream";

                // Build filename from category or use a default filename
                String filename = category + ".zip"; // Example filename

                // Set content disposition header
                String headerValue = "attachment; filename=\"" + filename + "\"";

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadPostFiles() -> {}", e.getMessage());
            return ResponseEntity.internalServerError().build(); // Return error response
        }
        return ResponseEntity.notFound().build(); // Return not found response if resource is null
    }

    @GetMapping("/downloadAllPostsByCategory")
    public ResponseEntity<?> downloadAllUserPosts(@RequestParam("category") String category) {
        try {
            logger.info("downloadAllUserPosts Input Category :- {}", category);
            Resource resource = postService.getAllPostFilesByCategory(category);
            if (resource != null) {
                // Determine content type
                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                String contentType = "application/octet-stream";

                // Build filename from category or use a default filename
                String filename = category + ".zip"; // Example filename

                // Set content disposition header
                String headerValue = "attachment; filename=\"" + filename + "\"";

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadAllUserPosts() -> {}", e.getMessage());
            return ResponseEntity.internalServerError().build(); // Return error response
        }
        return ResponseEntity.notFound().build(); // Return didn't find response if resource is null
    }

    @GetMapping("/getAllUsersPosts")
    public Response getAllUsersPosts(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        try {
            List<PostWebModel> postWebModelList = postService.getAllUsersPosts(pageNo, pageSize);
            if (!Utility.isNullOrEmptyList(postWebModelList)) return new Response(1, "Success", postWebModelList);
        } catch (Exception e) {
            logger.error("Error at getAllUsersPosts() -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at getting user posts....", e.getMessage());
        }
        return new Response(-1, "Files were not found....", null);
    }

    @PostMapping("/addLike")
    public ResponseEntity<?> addOrUpdateLike(@RequestBody LikeWebModel likeWebModel) {
        try {
            LikeWebModel likeWebModelOutput = postService.addOrUpdateLike(likeWebModel);
            if (likeWebModelOutput != null) return ResponseEntity.ok(new Response(1, "Likes add/updated for the post successfully...", likeWebModelOutput));
        } catch (Exception e) {
            logger.error("addOrUpdateLike Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(@RequestBody CommentInputWebModel commentInputWebModel) {
        try {
            CommentOutputWebModel commentOutputWebModelOutput = postService.addComment(commentInputWebModel);
            if (commentOutputWebModelOutput != null) return ResponseEntity.ok(new Response(1, "Comment added for the post...", commentOutputWebModelOutput));
        } catch (Exception e) {
            logger.error("addComment Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }

    @PostMapping("/updateComment")
    public ResponseEntity<?> updateComment(@RequestBody CommentInputWebModel commentInputWebModel) {
        try {
            CommentOutputWebModel commentOutputWebModelOutput = postService.updateComment(commentInputWebModel);
            if (commentOutputWebModelOutput != null) return ResponseEntity.ok(new Response(1, "Comment added for the post...", commentOutputWebModelOutput));
        } catch (Exception e) {
            logger.error("updateComment Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getComment")
    public ResponseEntity<?> getComment(@RequestBody CommentInputWebModel commentInputWebModel) {
        try {
            List<CommentOutputWebModel> commentList = postService.getComment(commentInputWebModel);
            if (!Utility.isNullOrEmptyList(commentList)) return ResponseEntity.ok(new Response(1, "Comment retrieved for the post...", commentList));
            else return ResponseEntity.ok(new Response(-1, "No Comments available for this post...", null));
        } catch (Exception e) {
            logger.error("getComment Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }

    @PostMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(@RequestBody CommentInputWebModel commentInputWebModel) {
        try {
            CommentOutputWebModel commentOutputWebModelOutput = postService.deleteComment(commentInputWebModel);
            if (commentOutputWebModelOutput != null) return ResponseEntity.ok(new Response(1, "Comment deleted for the post...", commentOutputWebModelOutput));
        } catch (Exception e) {
            logger.error("deleteComment Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }

    @PostMapping("/addShare")
    public ResponseEntity<?> addShare(@RequestBody ShareWebModel shareWebModel) {
        try {
            ShareWebModel shareWebModelOutput = postService.addShare(shareWebModel);
            if (shareWebModelOutput != null) return ResponseEntity.ok(new Response(1, "Post shared successfully...", shareWebModelOutput));
        } catch (Exception e) {
            logger.error("addShare Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }

    @PostMapping("/addLink")
    public ResponseEntity<?> addLink(@RequestBody LinkWebModel linkWebModel) {
        try {
            LinkWebModel linkWebModels = postService.addLink(linkWebModel);
            if (linkWebModels != null) return ResponseEntity.ok(new Response(1, "Post shared successfully...", linkWebModels));
        } catch (Exception e) {
            logger.error("addLink Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }
    
    @PostMapping("/deletePostById")
    public ResponseEntity<?> deletePostByUserId(@RequestBody PostWebModel postWebModel) {
        try {
            boolean isDeleted = postService.deletePostByUserId(postWebModel);
            if (isDeleted) {
                return ResponseEntity.ok(new Response(1, "Success","Post deleted successfully."));
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "fail","Failed to delete post"));
            }
        } catch (Exception e) {
            logger.error("deletePostByUserId Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }

}