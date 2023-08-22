package cz.belli.skodabackend.service;

import cz.belli.skodabackend.api.pushnotification.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CronJobService {

    private final PushNotificationService pushNotificationService;

    /**
     * Cron job for sending free daily cookie via push notifications.
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void checkAndSendPushNotificationsCronJob() {
        this.pushNotificationService.sendCookieToRandomDevice();
    }
}
