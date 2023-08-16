package cz.belli.skodabackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponseDTO {

    /**
     * Error message for developers.
     */
    private String error;

    /**
     * User-friendly error message for users, if the application does not display its own message.
     */
    private String message;

    /**
     * HTTP status code.
     */
    private int statusCode;

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
