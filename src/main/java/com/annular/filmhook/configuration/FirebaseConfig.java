
package com.annular.filmhook.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

    private FirebaseOptions getFirebaseOptions() throws IOException {
        logger.info("FireBase config path -> {}", firebaseConfigPath);
        Resource resource = new ClassPathResource(firebaseConfigPath);
        return FirebaseOptions
                .builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();
    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        FirebaseApp app = FirebaseApp.initializeApp(this.getFirebaseOptions(), "Film-Hook");
        return FirebaseMessaging.getInstance(app);
    }

    @PostConstruct
    public void initialize() {
    	
        try {
            // Check if FirebaseApp is already initialized
            if (!FirebaseApp.getApps().isEmpty()) {
                logger.info("Firebase application is already initialized");
                return;
            }
            FirebaseApp.initializeApp(this.getFirebaseOptions()); // Initialize FirebaseApp
            logger.info("Firebase application has been initialized");
        } catch (IOException e) {
            logger.error("Error initializing Firebase: {}", e.getMessage());
        }
    }

}
