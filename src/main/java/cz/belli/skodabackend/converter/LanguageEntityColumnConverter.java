package cz.belli.skodabackend.converter;

import cz.belli.skodabackend.model.enumeration.LanguageEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter for {@link LanguageEnum} to String and vice versa in Entity.
 */
@Converter(autoApply = true)
public class LanguageEntityColumnConverter implements AttributeConverter<LanguageEnum, String> {
    @Override
    public String convertToDatabaseColumn(LanguageEnum entityValue) {
        if (entityValue == null) {
            return null;
        }
        return entityValue.getLanguage();
    }

    @Override
    public LanguageEnum convertToEntityAttribute(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }

        return LanguageEnum.get(databaseValue);
    }
}
