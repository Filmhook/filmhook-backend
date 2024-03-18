package com.annular.filmhook.service.impl;

import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.util.S3Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.List;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3ServiceImpl.class);

    @Autowired
    S3Util s3Util;

    @Override
    public List<S3Object> getAllObjectsByBucket(String bucketName) {
        return s3Util.getAllObjectsFromS3Bucket(bucketName);
    }

    @Override
    public List<S3Object> getAllObjectsByBucketAndDestination(String bucketName, String destinationPath) {
        return s3Util.getAllObjectsFromS3Bucket(bucketName, destinationPath);
    }

    @Override
    public byte[] getObjectFromS3(String bucketName, String key) {
        return s3Util.getObjectAsBytes(bucketName, key);
    }

    @Override
    public String putObjectIntoS3(String bucketName, String destinationPath, File inputFile) {
        return s3Util.putObjectIntoS3(bucketName, destinationPath, inputFile);
    }

    @Override
    public void putObjectIntoS3Async(String bucketName, String destinationPath, File inputFile) {
        s3Util.putObjectIntoS3Async(bucketName, destinationPath, inputFile);
    }

    @Override
    public void deleteAllObjectsFromDestination(String bucketName, String destinationPath) {
        s3Util.deleteAllObjectFromS3Async(bucketName, destinationPath);
    }

    @Override
    public void deleteObjectByKeyFromS3(String bucketName, String destinationPath, String key) {
        s3Util.deleteObjectFromS3(bucketName, destinationPath, key);
    }
}
