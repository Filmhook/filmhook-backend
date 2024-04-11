package com.annular.filmhook.util;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.nio.file.Paths;
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

    public static final String S3_PATH_DELIMITER = "/";

    public AwsCredentialsProvider getAwsCredentialsProvider() {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(DefaultCredentialsProvider.create())
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    public S3AsyncClient buildS3ClientAsync() {
        return S3AsyncClient.builder()
                .credentialsProvider(getAwsCredentialsProvider())
                .region(Region.of(s3RegionName))
                .build();
    }

    public S3Client buildS3ClientSync() {
        return S3Client.builder()
                .region(Region.of(s3RegionName))
                .credentialsProvider(getAwsCredentialsProvider())
                .build();
    }

    public List<S3Object> getAllObjectsFromS3Bucket(String bucketName) {
        logger.info("In getAllObjectsFromS3Bucket() Bucket Name :- [{}] ", bucketName);
        List<S3Object> objects = null;
        try(S3Client client = buildS3ClientSync()) {
            ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).build();
            ListObjectsResponse response = client.listObjects(request);
            objects = response.contents();
            logger.info("S3 Objects count :- [{}] ", objects.size());
        } catch (S3Exception e) {
            logger.error("Error in getAllObjectsFromS3Bucket" + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        }
        return objects;
    }

    public List<S3Object> getAllObjectsFromS3Bucket(String bucketName, String destinationPath) {
        logger.info("In getAllObjectsFromS3Bucket() Bucket Name :- [{}], Destination path :- [{}] ", bucketName, destinationPath);
        List<S3Object> objects = null;
        try (S3Client client = buildS3ClientSync()) {
            ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).prefix(destinationPath).build();
            ListObjectsResponse response = client.listObjects(request);
            objects = response.contents();
            logger.info("S3 Objects count :- [{}] ", objects.size());
        } catch (S3Exception e) {
            logger.error("Error in getListOfFileFromS3Bucket{}", e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        }
        return objects;
    }

    public byte[] getObjectAsBytes(String bucketName, String objectKey) {
        logger.info("In getObjectAsBytes() Bucket Name :- [{}], objectKey :- [{}] ", bucketName, objectKey);
        try (S3Client s3Client = buildS3ClientSync()) {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(objectKey)
                    .bucket(bucketName)
                    .build();
            return s3Client.getObjectAsBytes(objectRequest).asByteArray();
        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    /**
     * Put object into the s3 bucket Synchronously
     */
    public String putObjectIntoS3(String bucketName, String destinationPath, File file) {
        logger.info("In putObjectIntoS3() Details to upload into S3 :: Bucket Name :- [{}], destinationPath :- [{}] ", bucketName, destinationPath);
        try (S3Client s3Client = buildS3ClientSync()) {
            logger.info("File Path :- " + file.getAbsoluteFile().getAbsolutePath() + ", File Name :- " + file.getName());
            PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(destinationPath).build();
            PutObjectResponse response = s3Client.putObject(objectRequest, Paths.get(file.getAbsoluteFile().getAbsolutePath()));
            return response != null ? "File uploaded" : "Error";
        } catch (Exception e) {
            logger.error("Error at object upload...", e);
            return null;
        }
    }

    /**
     * Put object into the s3 bucket Asynchronously
     */
    public void putObjectIntoS3Async(String bucketName, String destinationPath, File file) {
        logger.info("In putObjectIntoS3Async() Details to upload into S3 :: Bucket Name :- [{}], destinationPath :- [{}] ", bucketName, destinationPath);
        String responseFromS3 = null;
        try (S3AsyncClient s3AsyncClient = buildS3ClientAsync()) {
            logger.info("File Path :- " + file.getAbsoluteFile().getAbsolutePath() + ", File Name :- " + file.getName());
            CompletableFuture<PutObjectResponse> futureCompletion = this.putObjectInS3bucketAsync(bucketName, destinationPath, file.getAbsoluteFile().getAbsolutePath(), file.getName(), s3AsyncClient);
            responseFromS3 = this.executeFutureCompletion(futureCompletion, "Uploaded successfully");
        } catch (Exception e) {
            responseFromS3 = "Error";
        }
        if (!responseFromS3.equalsIgnoreCase("Uploaded successfully")) {
            logger.info("Error while uploading file into S3...");
        }
    }

    /**
     * Put object into the s3 bucket Asynchronously
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
                    logger.info("{}: {}", msgToPrint, resp);
                } else {
                    returnMessage.set("Error");
                    logger.error("Uploaded failed..{}", throwable.getMessage());
                    throwable.printStackTrace(); // Handle error
                }
            } catch (Exception e) {
                returnMessage.set("Error");
                logger.error("Uploaded failed: {}", throwable.getMessage());
                throwable.printStackTrace();
            }
        });
        futureCompletion.join();
        return returnMessage.get();
    }

    public void deleteAllObjectFromS3Async(String bucketName, String destPath) {
        logger.info("Inn deleteAllObjectFromS3Async() Destination to delete all objects from S3..." + destPath);
        try (S3AsyncClient s3AsyncClient = buildS3ClientAsync()) {
            List<S3Object> s3ObjectList = this.getAllObjectsFromS3Bucket(bucketName, destPath);
            if (s3ObjectList != null && !s3ObjectList.isEmpty()) {

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
            logger.error("Error in deleting object from s3 : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteObjectFromS3(String bucketName, String destinationPath, String objectKey) {
        logger.info("In deleteObjectFromS3() Key to delete from S3...{}", objectKey);
        try (S3AsyncClient s3Client = buildS3ClientAsync()) {
            List<S3Object> s3Objects = this.getAllObjectsFromS3Bucket(bucketName, destinationPath);
            if (s3Objects != null && !s3Objects.isEmpty()) {
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
        }
    }
}
