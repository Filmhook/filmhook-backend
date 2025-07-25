package com.annular.filmhook.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.annular.filmhook.service.PostService;
import com.annular.filmhook.webmodel.PostWebModel;


@Controller
@RequestMapping("/og/post/view") // <-- unique path for OG previews
public class PostOGPreviewController {

    @Autowired
    private PostService postService;

    @GetMapping("/{postId}")
    public String getPostPreviewPage(@PathVariable String postId, Model model) {
        PostWebModel post = postService.getPostByPostId(postId);

        if (post == null) {
            System.out.println("Post not found for ID: " + postId);
            return "error/404";
        }

        System.out.println("Post found: " + post.getPostId());

        String thumbnail = "https://filmhookapps.com/default-thumbnail.jpg";
        if (post.getPostFiles() != null && !post.getPostFiles().isEmpty()) {
            if (post.getPostFiles().get(0) != null && post.getPostFiles().get(0).getFilePath() != null) {
                thumbnail = post.getPostFiles().get(0).getFilePath();
            }
        }

        model.addAttribute("ogTitle", post.getUserName() + "'s post on Filmhook");
        model.addAttribute("ogDescription", post.getDescription() != null ? post.getDescription() : "Check out this post on Filmhook!");
        model.addAttribute("ogImage", thumbnail);
        model.addAttribute("ogUrl", "https://filmhookapps.com/og/post/view/" + postId);

        return "post-preview";
    }
}
