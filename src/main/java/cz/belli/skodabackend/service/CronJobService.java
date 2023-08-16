package cz.belli.skodabackend.service;

import cz.belli.skodabackend.endpoint.pushnotification.PushNotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CronJobService {

    private final PushNotificationService pushNotificationService;

    public CronJobService(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    /**
     * Cron job for sending free daily cookie via push notifications.
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void checkAndSendPushNotificationsCronJob() {
        this.pushNotificationService.sendCookieToRandomDevice();
    }
}
