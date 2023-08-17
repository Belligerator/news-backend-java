package cz.belli.skodabackend.service;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SentryService {

    /**
     * Capture message to Sentry
     * @param message   Message to capture
     */
    public static void captureMessage(String message) {
        log.info("Capture message: " + message);
        Sentry.captureMessage(message);
    }

    /**
     * Capture exception to Sentry
     * @param exception Exception to capture
     */
    public static void captureException(Exception exception) {
        log.error("Capture exception: " + exception.getMessage());
        Sentry.captureException(exception);
    }

}
