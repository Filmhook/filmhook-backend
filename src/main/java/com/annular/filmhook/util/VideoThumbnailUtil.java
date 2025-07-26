package com.annular.filmhook.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class VideoThumbnailUtil {

    private static final Logger logger = LoggerFactory.getLogger(VideoThumbnailUtil.class);

    public File generateThumbnail(File videoFile, String outputFilePath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg", "-i", videoFile.getAbsolutePath(),
                "-ss", "00:00:01", // 1 second into the video
                "-vframes", "1", // take only 1 frame
                "-q:v", "2", // quality
                outputFilePath
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();
            process.waitFor();

            File thumbnailFile = new File(outputFilePath);
            if (thumbnailFile.exists()) {
                logger.info("Thumbnail generated at: {}", thumbnailFile.getAbsolutePath());
                return thumbnailFile;
            } else {
                logger.error("Thumbnail generation failed");
                return null;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to generate thumbnail: {}", e.getMessage());
            return null;
        }
    }
}
