package com.annular.filmhook.controller;

import com.annular.filmhook.service.PostService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShareController {

    @Autowired
    private PostService postService;

    @GetMapping("/media/{postId}")
    public ResponseEntity<String> renderOGPreview(@PathVariable String postId) {
        PostWebModel post = postService.getPostByPostId(postId);

        if (post == null || post.getPostFiles() == null || post.getPostFiles().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<h1>Post not found</h1>");
        }

        String title = "Post by " + post.getUserName();
        String description = post.getDescription() != null ? post.getDescription() : "Check this post on FilmHook!";
        List<FileOutputWebModel> files = post.getPostFiles();

        // Use first media file path
        String mediaUrl = files.get(0).getFilePath();
        
        String redirectUrl = "filmhook://media/" + postId;
        String pageUrl = "https://filmhookapps.com/media/" + postId;

        // ✅ Console logging
        System.out.println("OG Title: " + title);
        System.out.println("OG Media URL: " + mediaUrl);
        System.out.println("OG Page URL: " + pageUrl);

        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>" + escapeHtml(title) + "</title>\n" +
                "  <meta property=\"og:title\" content=\"" + escapeHtml(title) + "\" />\n" +
                "  <meta property=\"og:description\" content=\"" + escapeHtml(description) + "\" />\n" +
                "  <meta property=\"og:image\" content=\"" + mediaUrl + "\" />\n" +
                "  <meta property=\"og:url\" content=\"" + pageUrl + "\" />\n" +
                "  <meta name=\"twitter:card\" content=\"summary_large_image\" />\n" +
                "  <script>\n" +
                "    setTimeout(function() {\n" +
                "      window.location.href = '" + redirectUrl + "';\n" +
                "    }, 1000);\n" +
                "  </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <p>Redirecting to FilmHook app... <a href=\"" + redirectUrl + "\">Click here</a> if not redirected.</p>\n" +
                "</body>\n" +
                "</html>";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
