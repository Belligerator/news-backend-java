package cz.belli.skodabackend.converter;

import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;

/**
 * Converter for converting string to ArticleTypeEnum in request parameters.
 */
public class ArticleTypeStringToEnumConverter implements Converter<String, ArticleTypeEnum> {
    @Override
    public ArticleTypeEnum convert(String source) {
        try {
            return ArticleTypeEnum.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ExtendedResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong article type: " + source);
        }
    }
}
