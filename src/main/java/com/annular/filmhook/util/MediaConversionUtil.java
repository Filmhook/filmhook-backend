package com.annular.filmhook.util;



import java.io.File;
import java.io.IOException;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;


public class MediaConversionUtil {
	public static void convertToWebP(String inputPath, String outputPath) throws IOException, InterruptedException {
	    // Use the full path to cwebp.exe
	    ProcessBuilder processBuilder = new ProcessBuilder(
	        "C:\\Program Files\\webpUtil\\libwebp-1.5.0-windows-x64\\bin\\cwebp.exe",  "-q", "90",
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


	   public static void convertToWebM(String inputPath, String outputPath) throws Exception {
	        File source = new File(inputPath);
	        File target = new File(outputPath);

	        AudioAttributes audio = new AudioAttributes();
	        audio.setCodec("libopus");

	        VideoAttributes video = new VideoAttributes();
	        video.setCodec("libvpx");

	        EncodingAttributes attrs = new EncodingAttributes();
	        attrs.setOutputFormat("webm");
	        attrs.setAudioAttributes(audio);
	        attrs.setVideoAttributes(video);

	        Encoder encoder = new Encoder();
	        encoder.encode(new MultimediaObject(source), target, attrs);
	    }
	    
	   
}
