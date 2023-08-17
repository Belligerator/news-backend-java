package cz.belli.skodabackend.service;

import org.springframework.stereotype.Service;

@Service
public class SentryService {

    public static void captureMessage(String message) {
        System.out.println("[Sentry captureMessage]: " + message);
        // todo: implementace Sentry.
    }

    public static void captureException(Exception exception) {
        System.out.println("[Sentry captureException]: " + exception.getMessage());
        // todo: implementace Sentry.
    }

}
