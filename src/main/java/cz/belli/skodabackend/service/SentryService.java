package cz.belli.skodabackend.service;

import org.springframework.stereotype.Service;

@Service
public class SentryService {

    static public void captureMessage(String message) {
        System.out.println("[Sentry captureMessage]: " + message);
        // todo: implementace Sentry.
    }

}
