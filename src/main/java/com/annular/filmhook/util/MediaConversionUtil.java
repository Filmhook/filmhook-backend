package com.annular.filmhook.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.FileImageOutputStream;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;


public class MediaConversionUtil {
	  public static void convertToWebP(String inputPath, String outputPath) throws IOException {
	        BufferedImage inputImage = ImageIO.read(new File(inputPath));

	        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
	        if (!writers.hasNext()) {
	            throw new IOException("WebP writer not found. Make sure TwelveMonkeys is in your classpath.");
	        }

	        ImageWriter writer = writers.next();
	        File outputFile = new File(outputPath);
	        FileImageOutputStream output = new FileImageOutputStream(outputFile);
	        writer.setOutput(output);
	        writer.write(inputImage);
	        output.close();
	        writer.dispose();
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
