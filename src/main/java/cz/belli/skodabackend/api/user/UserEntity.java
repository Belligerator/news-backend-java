package cz.belli.skodabackend.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Table(name = "user")
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "boolean NOT NULL DEFAULT true")
    private Boolean active;

    /**
     * Refresh token. User can be logged in from one device in one time.
     * For be able to log in from another device, table with refresh tokens must be created.
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * Refresh token expiration date.
     */
    @Column(name = "expiration_date", columnDefinition = "timestamp NULL DEFAULT NULL")
    private Date expirationDate;

}
