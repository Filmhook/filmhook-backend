package com.annular.filmhook;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@SpringBootApplication
@EnableScheduling
public class FilmHookApplication {

	@Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;
	
	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		Resource resource = new ClassPathResource(firebaseConfigPath);
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(resource.getInputStream());
	    FirebaseOptions firebaseOptions = FirebaseOptions
	            .builder()
	            .setCredentials(googleCredentials)
	            .build();
	    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "Film-Hook");
	    return FirebaseMessaging.getInstance(app);
	}
	public static void main(String[] args) {
		SpringApplication.run(FilmHookApplication.class, args);
	}

}
