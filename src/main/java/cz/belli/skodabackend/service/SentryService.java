package cz.belli.skodabackend.service;

import io.sentry.Sentry;
import org.springframework.stereotype.Service;

@Service
public class SentryService {

    public static void captureMessage(String message) {
        System.out.println("[Sentry captureMessage]: " + message);
        Sentry.captureMessage(message);
    }

    public static void captureException(Exception exception) {
        System.out.println("[Sentry captureException]: " + exception.getMessage());
        Sentry.captureException(exception);
    }

}
