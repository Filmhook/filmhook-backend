package com.annular.filmhook.controller;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/deeplink")
public class DeepLinkController {

    // Configurable constants
    private static final String ANDROID_PACKAGE = "com.projectfh";
    private static final String WEB_BASE_URL = "https://www.filmhooks.annulartech.net";

    @GetMapping("/{type}/{id}")
    public void handleDeepLink(
            @PathVariable String type,
            @PathVariable String id,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // Build path: e.g. "property/14"
        String path = type + "/" + id;

        // Construct Android intent URI
        String androidIntent = "intent://" + path +
                "#Intent;scheme=filmhook;package=" + ANDROID_PACKAGE + ";end";

        // Construct web fallback URL
        String webLink = WEB_BASE_URL + "/" + path;

        // Get user-agent to detect Android
        String userAgent = request.getHeader("User-Agent");

        // Debug logs
        System.out.println("User-Agent: " + userAgent);
        System.out.println("Android Intent: " + androidIntent);
        System.out.println("Web Fallback: " + webLink);

        // Redirect to intent or web based on device
        if (userAgent != null && userAgent.toLowerCase().contains("android")) {
            response.sendRedirect(androidIntent);
        } else {
            response.sendRedirect(webLink);
        }
    }
}
