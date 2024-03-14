package com.annular.filmhook.util;

//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.ListObjectsRequest;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@Getter
@Setter
public class S3Util {

    public static final Logger logger = LoggerFactory.getLogger(S3Util.class);

    @Value("${s3.region.name}")
    private String s3RegionName;

    @Value("${s3.bucket.name}")
    private String s3BucketName;

    private static final String S3_PATH_DELIMITER = "/";

    /*@Bean
    public AmazonS3 s3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(s3RegionName)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        //S3Client
        //ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(s3BucketName).prefix(destinationPath).build();
        //ListObjectsResponse response = client.listObjects(request);
        //objects = response.contents();
    }*/

    public AwsCredentialsProvider getAwsCredentialsProvider() {
        AwsCredentialsProvider awsCredentialsProvider = AwsCredentialsProviderChain.builder().
                addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(DefaultCredentialsProvider.create())
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
        logger.info("AwsCredentialsProvider created successfully...");
        return awsCredentialsProvider;
    }

    public S3AsyncClient buildS3ClientAsync() {
        return S3AsyncClient.builder()
                .credentialsProvider(getAwsCredentialsProvider())
                .region(Region.of(s3RegionName))
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .connectionTimeout(Duration.ofMinutes(5))
                        .connectionMaxIdleTime(Duration.ofSeconds(5)))
                .build();
    }

    public S3Client buildS3ClientSync() {
        return S3Client.builder()
                .region(Region.of(s3RegionName))
                .credentialsProvider(getAwsCredentialsProvider())
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    public List<S3Object> getAllObjectsFromS3Bucket(String bucketName) {
        logger.info("Bucket Name to search :- [{}] ", bucketName);
        List<S3Object> objects = null;
        try {
            S3Client client = buildS3ClientSync();
            String destinationPath = "Sample" + S3_PATH_DELIMITER;
            logger.info("Destination to search :- [{}] ", destinationPath);
            ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).prefix(destinationPath).build();
            ListObjectsResponse response = client.listObjects(request);
            objects = response.contents();
            logger.info("S3 Objects count :- [{}] ", objects.size());
        } catch (S3Exception e) {
            logger.error("Error in getListOfFileFromS3Bucket" + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        }
        return objects;
    }

    public List<S3Object> getAllObjectsFromS3Bucket(String bucketName, String destinationPath) {
        logger.info("Bucket Name :- [{}], Destination path :- [{}] ", bucketName, destinationPath);
        List<S3Object> objects = null;
        try {
            S3Client client = buildS3ClientSync();
            ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).prefix(destinationPath).build();
            ListObjectsResponse response = client.listObjects(request);
            objects = response.contents();
            logger.info("S3 Objects count :- [{}] ", objects.size());
        } catch (S3Exception e) {
            logger.error("Error in getListOfFileFromS3Bucket" + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        }
        return objects;
    }

    public ByteArrayInputStream getObjectAsBytes(String bucketName, String objectKey) {
        logger.info("Bucket Name :- [{}], objectKey :- [{}] ", bucketName, objectKey);
        S3Client s3 = buildS3ClientSync();
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(objectKey)
                    .bucket(bucketName)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();
            return new ByteArrayInputStream(data);
        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            throw e;
        } finally {
            s3.close();
        }
    }

    /**
     * Put object into the s3 bucket
     */
    public void putObjectIntoS3(String bucketName, String destinationPath, File file) {
        logger.info("Details to put :: Bucket Name :- [{}], destinationPath :- [{}] ", bucketName, destinationPath);
        String responseFromS3 = null;
        try (S3AsyncClient s3AsyncClient = buildS3ClientAsync()) {
            CompletableFuture<PutObjectResponse> futureCompletion;
            futureCompletion = this.putObjectInS3bucketAsync(bucketName, destinationPath, file.getAbsoluteFile().getAbsolutePath(), file.getName(), s3AsyncClient);
            responseFromS3 = this.executeFutureCompletion(futureCompletion, "Uploaded successfully");
        } catch (Exception e) {
            responseFromS3 = "Error";
        }
        if (!responseFromS3.equalsIgnoreCase("Uploaded successfully")) {
            logger.info("Error while uploading file into S3...");
        }
    }

    /**
     * Put object into the s3 bucket Async
     */
    private CompletableFuture<PutObjectResponse> putObjectInS3bucketAsync(String s3Bucket, String destinationPath, String absolutePath, String name, S3AsyncClient s3AsyncClient) {
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(s3Bucket).key(destinationPath + name).build();
        return s3AsyncClient.putObject(objectRequest, Paths.get(absolutePath));
    }

    private String executeFutureCompletion(CompletableFuture<?> futureCompletion, String msgToPrint) {
        AtomicReference<String> returnMessage = new AtomicReference<>("");
        futureCompletion.whenComplete((resp, throwable) -> {
            try {
                if (resp != null) {
                    returnMessage.set(msgToPrint);
                    logger.info(msgToPrint + ": " + resp);
                } else {
                    returnMessage.set("Error");
                    logger.info("Uploaded failed" + ": " + throwable.getMessage());
                    throwable.printStackTrace(); // Handle error
                }
            } catch (Exception e) {
                returnMessage.set("Error");
                logger.info("Uploaded failed" + ": " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
        futureCompletion.join();
        return returnMessage.get();
    }

    public void deleteAllObjectFromS3Async(String bucketName, String destPath) {
        logger.info("Destination to delete all objects from S3..." + destPath);
        S3AsyncClient s3AsyncClient = buildS3ClientAsync();
        try {
            List<S3Object> s3ObjectList = this.getAllObjectsFromS3Bucket(bucketName, destPath);
            if (!s3ObjectList.isEmpty() && s3ObjectList.size() > 0) {

                List<ObjectIdentifier> objectIdentifiers = new ArrayList<>();
                s3ObjectList.forEach(x -> objectIdentifiers.add(ObjectIdentifier.builder().key(x.key()).build()));

                DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder().objects(objectIdentifiers).build())
                        .build();

                CompletableFuture<DeleteObjectsResponse> deleteObjectsFuture = s3AsyncClient.deleteObjects(dor);
                executeFutureCompletion(deleteObjectsFuture, "Objects Deleted Successfully");
            }

        } catch (S3Exception e) {
            logger.error("Error in deleting object from s3 : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteObjectFromS3(String bucketName, String destinationPath, String objectKey) {
        logger.info("Key to delete from S3..." + objectKey);
        S3AsyncClient s3Client = buildS3ClientAsync();
        try {
            List<S3Object> s3Objects = this.getAllObjectsFromS3Bucket(bucketName, destinationPath);
            if (!s3Objects.isEmpty() && s3Objects.size() > 0) {
                List<ObjectIdentifier> objectIdentifiers = new ArrayList<>();
                s3Objects.stream()
                        .filter(item -> item.key().contains(objectKey))
                        .forEach(item -> objectIdentifiers.add(ObjectIdentifier.builder().key(item.key()).build()));

                DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder().objects(objectIdentifiers).build())
                        .build();

                CompletableFuture<DeleteObjectsResponse> deleteObjectsFuture = s3Client.deleteObjects(dor);
                executeFutureCompletion(deleteObjectsFuture, "Objects Deleted Successfully");
            }
        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            throw e;
        } finally {
            s3Client.close();
        }
    }
}
