package com.annular.filmhook.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.VideoSize;


public class MediaConversionUtil {

	private static final String CWEBP_PATH = "/usr/bin/cwebp";
	//	  private static final String CWEBP_PATH = "C:\\Program Files\\webpUtil\\libwebp-1.5.0-windows-x64\\bin\\cwebp.exe";
	private static final String MAGICK_PATH = "/usr/bin/convert"; 
	private static final List<String> HEIC_FORMATS =
			Arrays.asList("heic", "heif", "avif");



	public static void convertToWebP(String inputPath, String outputPath)
			throws IOException, InterruptedException {

		File inputFile = new File(inputPath);
		if (!inputFile.exists()) {
			throw new IOException("Input file not found: " + inputPath);
		}

		String extension = getFileExtension(inputFile.getName()).toLowerCase();

		// If HEIC/HEIF/AVIF → convert to temp PNG first
		if (HEIC_FORMATS.contains(extension)) {
			String tempPng = inputPath + "_temp.png";
			convertHeicToPng(inputPath, tempPng);
			convertImageToWebP(tempPng, outputPath);
			new File(tempPng).delete();
		} else {
			convertImageToWebP(inputPath, outputPath);
		}
	}

	// Converts HEIC / HEIF / AVIF → PNG
	private static void convertHeicToPng(String inputPath, String outputPath)
			throws IOException, InterruptedException {

		ProcessBuilder pb = new ProcessBuilder(
				MAGICK_PATH,
				inputPath,
				outputPath
				);

		pb.redirectErrorStream(true);
		Process process = pb.start();
		int exitCode = process.waitFor();

		if (exitCode != 0) {
			throw new IOException("HEIC conversion failed for: " + inputPath);
		}
	}

	private static String getFileExtension(String fileName) {
		int dot = fileName.lastIndexOf('.');
		return (dot == -1) ? "" : fileName.substring(dot + 1);
	}

	public static void convertImageToWebP(String inputPath, String outputPath) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(
				CWEBP_PATH,
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
	
	
	//vedio Conversion

	public static void convertToWebM(String inputPath, String outputPath) throws IOException, InterruptedException {
		// String ffmpegPath = "C:\\Program Files\\webmUtil\\ffmpeg-7.1.1-essentials_build\\bin\\ffmpeg.exe";

		String ffmpegPath= "/usr/bin/ffmpeg";


		ProcessBuilder builder = new ProcessBuilder(
				ffmpegPath,
				"-i", inputPath,

				// Video: High quality with scaling and padding to 1080x1920
				"-vf", "scale=1080:1920:force_original_aspect_ratio=decrease,pad=1080:1920:(ow-iw)/2:(oh-ih)/2",

				"-c:v", "libvpx",
				"-b:v", "3M",           // Higher bitrate for quality
				"-crf", "10",           // Lower CRF means higher quality
				"-cpu-used", "4",       // Balanced speed vs quality
				"-threads", "4",
				"-deadline", "realtime", // For quick processing

				"-r", "30",             // 30 fps

				// Audio
				"-c:a", "libopus",
				"-b:a", "128k",
				"-ac", "2",
				"-ar", "48000",

				"-y", // Overwrite without asking
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