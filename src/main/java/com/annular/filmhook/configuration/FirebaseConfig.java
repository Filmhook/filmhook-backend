//package com.annular.filmhook.configuration;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Configuration
//public class FirebaseConfig {
//
//    @PostConstruct
//    public void initialize() {
//        try {
//            String firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH");
//            if (firebaseConfigPath == null) {
//                throw new RuntimeException("FIREBASE_CONFIG_PATH environment variable not set.");
//            }
//
//            FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//
//            System.out.println("Firebase initialized successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

package com.annular.filmhook.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            String firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH");

            GoogleCredentials credentials;

            if (firebaseConfigPath != null && !firebaseConfigPath.isEmpty()) {
                // Local mode
                try (FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath)) {
                    credentials = GoogleCredentials.fromStream(serviceAccount);
                }
            } else {
                // Production mode - fetch from AWS Secrets Manager
                String secretName = "prod/AppBeta/FireBase"; // your secret name
                String region = "ap-southeast-2"; // your AWS region

                SecretsManagerClient client = SecretsManagerClient.builder()
                        .region(software.amazon.awssdk.regions.Region.of(region))
                        .build();

                GetSecretValueRequest request = GetSecretValueRequest.builder()
                        .secretId(secretName)
                        .build();

                GetSecretValueResponse response = client.getSecretValue(request);
                String secretString = response.secretString();

                credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(secretString.getBytes(StandardCharsets.UTF_8))
                );
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("✅ Firebase initialized successfully.");

        } catch (IOException e) {
            throw new RuntimeException("Error initializing Firebase", e);
        }
    }
}
