package com.annular.filmhook.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class VideoThumbnail {

    public static void createVideoThumbnail(String videoPath, String outputPath) throws Exception {
    	String playIconPath = "https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/play-icon.png";
		String ffmpegPath = "/usr/bin/ffmpeg";
      
//        String playIconPath = "C:\\filmhook-assets\\play-icon.png";
//
//       String ffmpegPath =  "C:\\Program Files\\webmUtil\\ffmpeg-7.1.1-essentials_build\\bin\\ffmpeg.exe";

        List<String> command = Arrays.asList(
                ffmpegPath,
                "-y",

                // INPUT VIDEO
                "-i", videoPath,

                // INPUT ICON
                "-i", playIconPath,

                // SEEK AFTER INPUT (IMPORTANT)
                "-ss", "00:00:01",

                // FILTER
                "-filter_complex",
                "[0:v]scale=640:-1[vid];" +
                "[1:v]scale=160:160[icon];" +
                "[vid][icon]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2",

                // OUTPUT SETTINGS
                "-frames:v", "1",
                "-c:v", "libwebp",
                "-lossless", "0",
                "-quality", "85",

                outputPath
        );

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        // 🔥 READ OUTPUT (VERY IMPORTANT)
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[FFMPEG-THUMB] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg thumbnail generation failed with exit code " + exitCode);
        }
    }
}
