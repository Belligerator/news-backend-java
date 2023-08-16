package cz.belli.skodabackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.IOException;

/**
 *  Firebase configuration.
 */
@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() {

        try (FileInputStream serviceAccount = new FileInputStream("certs/serviceAccountKey.json")) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            // todo better logging
            System.out.println("Error during firebase initialization: " + e.getMessage());
        }

        return null;
    }
}