package com.annular.filmhook.service;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FCMInitializer {

	@Value("${app.firebase-configuration-file}")
	private String firebaseConfigPath;

	private static final Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

	@PostConstruct
	public void initialize() {

		try {
			// Check if FirebaseApp is already initialized
			if (!FirebaseApp.getApps().isEmpty()) {
				logger.info("Firebase application is already initialized");
				return;
			}
			System.out.println("Inside FCMINitiali=zser >>>>>>> " + firebaseConfigPath);
			// Load the Firebase configuration file from the classpath
			ClassPathResource resource = new ClassPathResource(firebaseConfigPath);
			InputStream inputStream = resource.getInputStream();

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(inputStream)).build();

			// Initialize FirebaseApp
			FirebaseApp.initializeApp(options);
			logger.info("Firebase application has been initialized");
		} catch (IOException e) {
			logger.error("Error initializing Firebase: " + e.getMessage());
		}
	}
}
