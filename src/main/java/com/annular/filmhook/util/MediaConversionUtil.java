package com.annular.filmhook.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.VideoSize;


public class MediaConversionUtil {
	public static void convertToWebP(String inputPath, String outputPath) throws IOException, InterruptedException {
	    ProcessBuilder processBuilder = new ProcessBuilder(
	 //   "/usr/bin/cwebp",
   "C:\\Program Files\\webpUtil\\libwebp-1.5.0-windows-x64\\bin\\cwebp.exe",
	        "-q", "90",
	        inputPath,
	        "-o", 
	        outputPath
	    );

	    processBuilder.inheritIO(); // To see output/errors in the console
	    Process process = processBuilder.start();
	    int exitCode = process.waitFor();

	    if (exitCode != 0) {
	        throw new IOException("WebP conversion failed. Exit code: " + exitCode);
	    }
	}

	public static void convertToWebM(String inputPath, String outputPath) throws IOException, InterruptedException {
    
    String ffmpegPath = "C:\\Program Files\\webmUtil\\ffmpeg-7.1.1-essentials_build\\bin\\ffmpeg.exe";
    
    //  String ffmpegPath= "/usr/bin/ffmpeg"

    ProcessBuilder builder = new ProcessBuilder(
    		 ffmpegPath,
    		    "-i", inputPath,

    		    // ⚡ Speed-optimized libvpx settings
    		    "-c:v", "libvpx",
    		    "-b:v", "1M",           
    		    "-crf", "23",           
    		    "-cpu-used", "5",        
    		    "-threads", "4",        
    		    "-deadline", "realtime",     

    		    "-s", "854x480",       
    		    "-r", "24",             

    		    "-c:a", "libopus",
    		    "-b:a", "96k",
    		    "-ac", "2",
    		    "-ar", "48000",

    		    "-y",
    		    outputPath
    );

    builder.redirectErrorStream(true);

    long startTime = System.currentTimeMillis();
    Process process = builder.start();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        System.out.println("=== FFmpeg Output (WebM) ===");
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    int exitCode = process.waitFor();
    long duration = System.currentTimeMillis() - startTime;
    System.out.println("WebM encoding completed in " + duration + " ms");

    if (exitCode != 0) {
        throw new IOException("FFmpeg WebM conversion failed. Exit code: " + exitCode);
    }
}

}
