package com.annular.filmhook.service;

//import com.amazonaws.services.s3.model.ObjectListing;
//import com.amazonaws.services.s3.model.PutObjectResult;
//import com.amazonaws.services.s3.model.S3Object;

import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

public interface AwsS3Service {

//    ObjectListing getAllObjects(String bucketName);
//    S3Object getObject(String bucketName, String key);
//    PutObjectResult putObject();

    List<S3Object> getAllObjectsByBucket(String bucketName);
    List<S3Object> getAllObjectsByBucketAndDestination(String bucketName, String destinationPath);
    ByteArrayInputStream getObjectFromS3(String bucketName, String key);
    void putObjectIntoS3(String bucketName, String destinationPath, File inputData);
    void deleteAllObjectsFromDestination(String bucketName, String destinationPath);
    void deleteObjectByKeyFromS3(String bucketName, String destinationPath, String key);

}
