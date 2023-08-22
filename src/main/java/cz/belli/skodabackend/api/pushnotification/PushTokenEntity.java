package cz.belli.skodabackend.api.pushnotification;

import cz.belli.skodabackend.model.dto.PushTokenDTO;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Entity for storing push tokens.
 * Mobile application does not have user accounts, so sending push notifications to a specific user does not make sense.
 * It is only for purpose of sample application. We will send push notification (daily cookie :D) to a random device (token).
 */
@Setter
@Getter
@DynamicInsert
@Entity
@Table(name = "push_token")
public class PushTokenEntity {

    @Id
    @Column()
    private String token;

    @Column(nullable = false)
    private LanguageEnum language;

    @Column(columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP",
            nullable = false,
            name = "created_at")
    private Date createdAt;

    /**
     * Last time when token saved to database.
     * onUpdate will not update time, because there are no columns to update. So we need to update it manually.
     * This column is used for finding when application was last time opened. It is not guaranteed because token can be changed on device.
     */
    @Column(columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
            nullable = false,
            name = "updated_at")
    private Date updatedAt;

    public PushTokenEntity() {
    }

    public PushTokenEntity(PushTokenDTO tokenDTO) {
        this.token = tokenDTO.getToken();
        this.language = LanguageEnum.get(tokenDTO.getLanguage());
        this.updatedAt = new Date();
    }
}


