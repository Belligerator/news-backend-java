package cz.belli.skodabackend.endpoint.pushnotification;

import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import cz.belli.skodabackend.endpoint.article.ArticleContentEntity;
import cz.belli.skodabackend.model.dto.PushTokenDTO;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.service.SentryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
public class PushNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final PushNotificationRepository pushNotificationRepository;

    public PushNotificationService(FirebaseApp firebaseApp,
                                   PushNotificationRepository pushNotificationRepository) {
        this.firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
        this.pushNotificationRepository = pushNotificationRepository;
    }

    /**
     * Send push notification to topic when new article is created.
     *
     * @param articleContentEntity Article content entity.
     * @param language             Language of the article. To what topic should we send the notification.
     */
    @Async
    public void sendPushNotificationToTopic(ArticleContentEntity articleContentEntity, LanguageEnum language) {
        // Do not send whole body, because it could be too long.
        String body = articleContentEntity.getBody();
        body = body.length() > 252 ? body.substring(0, 252) + "..." : body;

        Message message = Message.builder()
                .setTopic("new-article-" + language.getLanguage())
                .setNotification(Notification.builder()
                        .setTitle(articleContentEntity.getTitle())
                        .setBody(body)
                        .build())
                .putData("articleId", articleContentEntity.getId().toString())
                .putData("articleType", articleContentEntity.getArticle().getArticleType().toString())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .setIcon("notification_icon")
                                .build())
                        .build())
                .build();

        // Send a message to the devices subscribed to the provided topic.
        // Send message asynchronously. We do not want to wait for response.
        ApiFuture<String> response = firebaseMessaging.sendAsync(message);
        response.addListener(() -> {
            try {
                System.out.println("[PUSH_NOTIFICATION_SERVICE] Push notification send: " + response.get());
            } catch (Exception e) {
                // todo better error handling
                System.err.println("Error sending message: " + e.getMessage());
                SentryService.captureException(e);
            }
        }, Runnable::run);
    }

    /**
     * Send push notification to random device. Used as example.
     * Mobile app does not have user authentication, so we cannot send push notification to specific user.
     */
    public void sendCookieToRandomDevice() {
        // Get count of all tokens from DB.
        long count = this.pushNotificationRepository.count();

        // Get random token from DB. We use offset to get random token.
        // It is better than getting all tokens and then getting random token from list.
        int offset = (int) (Math.random() * count);
        Pageable pageable = PageRequest.of(offset, 1);

        Page<PushTokenEntity> entities = this.pushNotificationRepository.findAll(pageable);

        if (!entities.isEmpty()) {
            PushTokenEntity pushTokenEntity = entities.get().collect(Collectors.toList()).get(0);
            Message message = Message.builder()
                    .setToken(pushTokenEntity.getToken())
                    .setNotification(Notification.builder()
                            .setTitle("Daily cookie")
                            .setBody("Congratulation, you won daily cookie! \uD83C\uDF6A")
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                    .setIcon("notification_icon")
                                    .build())
                            .build())
                    .build();

            try {
                String response = this.firebaseMessaging.send(message);
                System.out.println("[PUSH_NOTIFICATION_SERVICE] Push notification send cookie: " + response);
            } catch (FirebaseMessagingException e) {
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    // Token is no longer registered. Delete it from DB.
                    this.pushNotificationRepository.delete(pushTokenEntity);
                } else {
                    // todo better error handling
                    System.out.println(e.getMessagingErrorCode());
                    SentryService.captureException(e);
                }
            }
        }
    }

    /**
     * Save or update token in database.
     * If token already exists, update updatedAt field.
     * So we can see, when the token was used last time (until the token changes).
     *
     * @param tokenDTO Device push token to save.
     */
    protected void saveToken(PushTokenDTO tokenDTO) {

        PushTokenEntity tokenEntityFromDB = this.pushNotificationRepository.findById(tokenDTO.getToken()).orElse(null);

        if (tokenEntityFromDB == null) {
            tokenEntityFromDB = new PushTokenEntity(tokenDTO);
        }

        tokenEntityFromDB.setUpdatedAt(new Date());

        this.pushNotificationRepository.save(tokenEntityFromDB);

    }

    /**
     * Delete token from database.
     *
     * @param tokenDTO Device push token to delete.
     */
    protected void deleteToken(PushTokenDTO tokenDTO) {
        this.pushNotificationRepository.deleteById(tokenDTO.getToken());
    }

}
