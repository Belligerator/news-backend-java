package cz.belli.skodabackend.endpoint.pushnotification;

import cz.belli.skodabackend.model.dto.PushTokenDTO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/push-notifications")
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    /**
     * Save or update token in database.
     *
     * @param tokenDTO  Device push token to save.
     */
    @PostMapping("/token")
    public void saveToken(@RequestBody @Valid PushTokenDTO tokenDTO) {
        this.pushNotificationService.saveToken(tokenDTO);
    }

    /**
     * Delete token from database.
     *
     * @param tokenDTO  Device push token to delete.
     */
    @DeleteMapping("/token")
    public void deleteToken(@RequestBody @Valid PushTokenDTO tokenDTO) {
        this.pushNotificationService.deleteToken(tokenDTO);
    }

}
