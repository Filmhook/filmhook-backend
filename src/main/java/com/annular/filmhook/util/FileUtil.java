package com.annular.filmhook.util;

import com.annular.filmhook.model.User;
import com.annular.filmhook.service.AwsS3Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Configuration
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    @Autowired
    AwsS3Service awsS3Service;

    @Autowired
    S3Util s3Util;

    public String uploadFile(File file, String destinationPath) {
        return awsS3Service.putObjectIntoS3(s3Util.getS3BucketName(), destinationPath, file);
    }

    public void uploadFileAsync(File file, String destinationPath) {
        awsS3Service.putObjectIntoS3Async(s3Util.getS3BucketName(), destinationPath, file);
    }

    public byte[] downloadFile(String objectKey) {
        return awsS3Service.getObjectFromS3(s3Util.getS3BucketName(), objectKey);
    }

    public void deleteFiles(List<S3Object> s3ObjectList) {
        awsS3Service.deleteObjectsFromS3(s3Util.getS3BucketName(), s3ObjectList);
    }

    public void deleteFile(String objectKey) {
        awsS3Service.deleteObjectFromS3(s3Util.getS3BucketName(), objectKey);
    }

    public List<S3Object> getS3Objects(String destinationPath) {
        return awsS3Service.getAllObjectsByBucketAndDestination(s3Util.getS3BucketName(), destinationPath);
    }

    public static void convertMultiPartFileToFile(MultipartFile multipartFile, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
    }

    public static String generateDestinationPath(User user, String category) {
        return new StringBuilder()
                .append(category)
                .append("/")
                .append(user.getUserId()).append("_").append(user.getName().toLowerCase().replace(" ", ""))
                .append("/")
                .toString();
    }

    public static String generateFilePath(User user, String category, String fileName) {
        return new StringBuilder()
                .append(category)
                .append("/")
                .append(user.getUserId()).append("_").append(user.getName().toLowerCase().replace(" ", ""))
                .append("/")
                .append(fileName)
                .toString();
    }

    public static void convertMultiPartFileToFile(MultipartFile[] images, File file) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            for (MultipartFile image : images) {
                byte[] bytes = image.getBytes();
                outputStream.write(bytes);
            }
        }
    }
}
