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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        String firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH");

        if (firebaseConfigPath == null || firebaseConfigPath.isEmpty()) {
            throw new IllegalStateException("FIREBASE_CONFIG_PATH environment variable is not set");
        }

        try (FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized from " + firebaseConfigPath);
            }
        }
    }
}