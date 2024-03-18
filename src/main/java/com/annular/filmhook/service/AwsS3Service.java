package com.annular.filmhook.service;

import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.List;

public interface AwsS3Service {

    List<S3Object> getAllObjectsByBucket(String bucketName);
    List<S3Object> getAllObjectsByBucketAndDestination(String bucketName, String destinationPath);
    byte[] getObjectFromS3(String bucketName, String key);
    String putObjectIntoS3(String bucketName, String destinationPath, File inputData);
    void putObjectIntoS3Async(String bucketName, String destinationPath, File inputData);
    void deleteAllObjectsFromDestination(String bucketName, String destinationPath);
    void deleteObjectByKeyFromS3(String bucketName, String destinationPath, String key);

}
