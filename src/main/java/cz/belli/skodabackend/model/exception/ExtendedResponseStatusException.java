package cz.belli.skodabackend.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExtendedResponseStatusException extends ResponseStatusException {

    // Pro zobrazeni uzivateli v aplikaci, pokud apliakce nezobrazi svoji hlasku.
    private String userMessage;

    // Chyba urcena pro vyvojare. Je detailnejsi.
    private String devMessage;

    public ExtendedResponseStatusException(HttpStatus status, String message) {
        super(status);
        this.userMessage = message;
        this.devMessage = message;
    }

    public ExtendedResponseStatusException(HttpStatus status, String message, String reason) {
        super(status);
        this.userMessage = message;
        this.devMessage = reason;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getDevMessage() {
        return devMessage;
    }
}
