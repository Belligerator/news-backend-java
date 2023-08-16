package cz.belli.skodabackend.converter;

import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;

/**
 * Converter for converting string to LanguageEnum in request parameters.
 */
public class LanguageStringToEnumConverter implements Converter<String, LanguageEnum> {
    @Override
    public LanguageEnum convert(String source) {
        try {
            return LanguageEnum.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ExtendedResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong language type: " + source);
        }
    }
}
