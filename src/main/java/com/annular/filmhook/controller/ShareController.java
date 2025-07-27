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

        FileOutputWebModel file = files.get(0);
        String mediaUrl;
        boolean isVideo = false;

        if (file.getFileType() != null && file.getFileType().toLowerCase().contains(".webm")) {
            mediaUrl = file.getThumbnailPath();  // show video thumbnail
            isVideo = true;
        } else {
            mediaUrl = file.getFilePath();  // show image
        }

        String redirectUrl = "filmhook://media/" + postId;
        String pageUrl = "https://filmhookapps.com/media/" + postId;

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
                "  <style>\n" +
                "    body { margin: 0; padding: 0; text-align: center; font-family: sans-serif; background-color: #000; color: #fff; }\n" +
                "    .media-container { position: relative; display: inline-block; max-width: 100%; }\n" +
                "    .media-container img { max-width: 100%; height: auto; display: block; }\n" +
                "    .play-icon {\n" +
                "      position: absolute;\n" +
                "      top: 50%;\n" +
                "      left: 50%;\n" +
                "      transform: translate(-50%, -50%);\n" +
                "      width: 64px;\n" +
                "      height: 64px;\n" +
                "      background: rgba(0, 0, 0, 0.5);\n" +
                "      border-radius: 50%;\n" +
                "      display: flex;\n" +
                "      justify-content: center;\n" +
                "      align-items: center;\n" +
                "    }\n" +
                "    .play-icon:before {\n" +
                "      content: '';\n" +
                "      display: inline-block;\n" +
                "      width: 0;\n" +
                "      height: 0;\n" +
                "      border-left: 20px solid white;\n" +
                "      border-top: 12px solid transparent;\n" +
                "      border-bottom: 12px solid transparent;\n" +
                "      margin-left: 6px;\n" +
                "    }\n" +
                "  </style>\n" +
                "  <script>\n" +
                "    setTimeout(function() {\n" +
                "      window.location.href = '" + redirectUrl + "';\n" +
                "    }, 1000);\n" +
                "  </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class='media-container'>\n" +
                "    <img src='" + mediaUrl + "' alt='Post Thumbnail' />\n";

        // Add play icon if it's a video
        if (isVideo) {
            html += "    <div class='play-icon'></div>\n";
        }

        html +=
                "  </div>\n" +
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
