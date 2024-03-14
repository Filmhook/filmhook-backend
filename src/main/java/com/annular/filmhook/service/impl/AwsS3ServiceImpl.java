package com.annular.filmhook.service.impl;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectListing;
//import com.amazonaws.services.s3.model.PutObjectResult;
//import com.amazonaws.services.s3.model.S3Object;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.util.S3Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3ServiceImpl.class);

//    @Autowired
//    AmazonS3 amazonS3;

    @Autowired
    S3Util s3Util;

//    @Override
//    public ObjectListing getAllObjects(String bucketName) {
//        logger.info("Region from s3 :- " + amazonS3.getRegionName());
//        return amazonS3.listObjects(bucketName);
//    }
//
//    @Override
//    public S3Object getObject(String bucketName, String key) {
//        return null;
//    }
//
//    @Override
//    public PutObjectResult putObject() {
//        return null;
//    }


    @Override
    public List<S3Object> getAllObjectsByBucket(String bucketName) {
        return s3Util.getAllObjectsFromS3Bucket(bucketName);
    }

    @Override
    public List<S3Object> getAllObjectsByBucketAndDestination(String bucketName, String destinationPath) {
        return s3Util.getAllObjectsFromS3Bucket(bucketName, destinationPath);
    }

    @Override
    public ByteArrayInputStream getObjectFromS3(String bucketName, String key) {
        return s3Util.getObjectAsBytes(bucketName, key);
    }

    @Override
    public void putObjectIntoS3(String bucketName, String destinationPath, File inputData) {
        s3Util.putObjectIntoS3(bucketName, destinationPath, inputData);
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
