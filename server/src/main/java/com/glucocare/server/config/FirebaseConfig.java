package com.glucocare.server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            var serviceAccount = getClass().getClassLoader()
                                           .getResourceAsStream("serviceAccountKey.json");

            var options = FirebaseOptions.builder()
                                         .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                         .build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase 초기화 완료");
        } catch (Exception e) {
            log.error("Firebase 초기화 실패");
        }
    }
}
