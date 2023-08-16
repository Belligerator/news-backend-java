package cz.belli.skodabackend.model.exception;

public class WrongEnumTypeException extends RuntimeException {

    // Pro zobrazeni uzivateli v aplikaci, pokud apliakce nezobrazi svoji hlasku.
    private String userMessage;

    // Chyba urcena pro vyvojare. Je detailnejsi.
    private String devMessage;

    public WrongEnumTypeException(String message, String reason) {
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
