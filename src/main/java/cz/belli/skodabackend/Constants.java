package cz.belli.skodabackend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "server") // prefix matches the name of the property in application.properties
public class Constants {

    /**
     * Version of the server.
     */
    @Getter
    @Setter
    private String version;


    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error, contact the administrator.";

    public static final int REFRESH_TOKEN_SIZE = 32;

}
