package cz.belli.skodabackend.exceptionhandler;

import cz.belli.skodabackend.model.dto.ErrorResponseDTO;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import cz.belli.skodabackend.model.exception.UnauthorizedException;
import cz.belli.skodabackend.model.exception.WrongEnumTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Set;

/**
 * This class handles exceptions thrown from controllers.
 */
@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({ AuthenticationException.class, UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(Exception ex) {
        return createAndReturnErrorResponseDto(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> res = ex.getConstraintViolations();
        StringBuilder error = new StringBuilder();

        if (res != null) {
            for (ConstraintViolation<?> violation : res) {
                error.append(violation.getPropertyPath()).append(": ");
                error.append(violation.getMessage()).append(". ");
            }
        }

        return createAndReturnErrorResponseDto(HttpStatus.BAD_REQUEST, error.toString(), error.toString());
    }
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return createAndReturnErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getMessage());
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<ErrorResponseDTO> handleBindException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        ArrayList<String> errors = new ArrayList<>();

        for (int i = 0; i < bindingResult.getErrorCount(); i++) {
            errors.add(bindingResult.getAllErrors().get(i).getDefaultMessage());
        }

        String error = String.join(" ", errors);

        return createAndReturnErrorResponseDto(HttpStatus.BAD_REQUEST, error.toString(), error.toString());
    }

    @ExceptionHandler({FileSizeLimitExceededException.class, SizeLimitExceededException.class})
    public ResponseEntity<ErrorResponseDTO> handleFileSizeLimitExceededException(SizeException ex) {
        return createAndReturnErrorResponseDto(HttpStatus.PAYLOAD_TOO_LARGE, "File too large", "File too large");
    }

    @ExceptionHandler(WrongEnumTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleWrongEnumTypeException(WrongEnumTypeException ex) {
        return createAndReturnErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, ex.getUserMessage(), ex.getDevMessage());
    }

    @ExceptionHandler(ExtendedResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(RuntimeException ex) {
        ExtendedResponseStatusException exception = (ExtendedResponseStatusException) getOriginalCause(ex);
        return createAndReturnErrorResponseDto(exception.getStatus(), exception.getUserMessage(), exception.getDevMessage());
    }

    /**
     * Create and return error response dto.
     * @param status    Http status to return.
     * @param message   Message to return.
     * @param error     Error to return.
     * @return          Error response dto.
     */
    private ResponseEntity<ErrorResponseDTO> createAndReturnErrorResponseDto(HttpStatus status, String message, String error) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setMessage(message);
        errorResponse.setError(error);
        errorResponse.setStatusCode(status.value());

        log.error(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Get original cause of exception.
     * E.g. ResponseStatusException can be thrown from another exception (eg. in StringToEnumConverter) and it will be nested in ex variable.
     * Therefore we take nested exception from ex variable.
     *
     * @param ex    Exception to get original cause from.
     * @return      Original cause of exception.
     */
    private Throwable getOriginalCause(RuntimeException ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
