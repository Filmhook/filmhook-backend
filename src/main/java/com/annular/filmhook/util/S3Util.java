package com.annular.filmhook.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class S3Util {

    public static final Logger logger = LoggerFactory.getLogger(S3Util.class);

    @Value("${s3.region.name}")
    private static String S3_REGION_NAME;
    @Value("${s3.bucket.name}")
    private static String S3_BUCKET_NAME;

    public static AwsCredentialsProvider getAwsCredentialsProvider() {
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

    public static S3AsyncClient buildAsyncS3Client() {
        return S3AsyncClient.builder()
                .credentialsProvider(getAwsCredentialsProvider())
                .region(Region.of(S3_REGION_NAME))
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .connectionTimeout(Duration.ofMinutes(5))
                        .connectionMaxIdleTime(Duration.ofSeconds(5)))
                .build();
    }

    public static S3Client buildSyncS3Client() {
        return S3Client.builder()
                .region(Region.of(S3_REGION_NAME))
                .credentialsProvider(getAwsCredentialsProvider())
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    public List<S3Object> getListOfFilesFromS3Bucket() {
        List<S3Object> objects = null;
        try {
            S3Client client = buildSyncS3Client();
            String destinationPath = S3_BUCKET_NAME + "/" + "Sample";
            ListObjectsRequest request = ListObjectsRequest.builder().bucket(S3_BUCKET_NAME).prefix(destinationPath).build();
            ListObjectsResponse response = client.listObjects(request);
            objects = response.contents();
            logger.info("S3 Objects count :- [{}] ", objects.size());
        } catch (S3Exception e) {
            logger.error("Error in getListOfFileFromS3Bucket" + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        }
        return objects;
    }


    public void putCat1ZipFileIntoS3(File zipFile) {
        String responseFromS3 = null;
        try (S3AsyncClient s3AsyncClient = buildAsyncS3Client()) {
            CompletableFuture<PutObjectResponse> futureCompletion;
            String destinationPath = S3_BUCKET_NAME + "/" + "Sample";
            futureCompletion = this.putObjectInS3bucketAsync(S3_BUCKET_NAME, destinationPath, zipFile.getAbsoluteFile().getAbsolutePath(), zipFile.getName(), s3AsyncClient);
            responseFromS3 = this.executeFutureCompletion(futureCompletion);
        } catch (Exception e) {
            responseFromS3 = "Error";
        }
        if (!responseFromS3.equalsIgnoreCase("Uploaded successfully")) {
            logger.info("Error while uploading file into S3...");
        }
    }

    private CompletableFuture<PutObjectResponse> putObjectInS3bucketAsync(String s3Bucket, String destinationPath, String absolutePath, String name, S3AsyncClient s3AsyncClient) {
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(s3Bucket).key(destinationPath + name).build();
        // Put object into the s3 bucket
        return s3AsyncClient.putObject(objectRequest, Paths.get(absolutePath));
    }

    private String executeFutureCompletion(CompletableFuture<PutObjectResponse> futureCompletion) {
        AtomicReference<String> returnMessage = new AtomicReference<>("");
        futureCompletion.whenComplete((resp, throwable) -> {
            try {
                if (resp != null) {
                    returnMessage.set("Uploaded successfully");
                    logger.info("Uploaded successfully" + ": " + resp);
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
}
