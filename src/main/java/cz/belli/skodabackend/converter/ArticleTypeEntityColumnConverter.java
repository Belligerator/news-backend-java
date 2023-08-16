package cz.belli.skodabackend.converter;

import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter for {@link ArticleTypeEnum} to String and vice versa in Entity.
 */
@Converter(autoApply = true)
public class ArticleTypeEntityColumnConverter implements AttributeConverter<ArticleTypeEnum, String> {
    @Override
    public String convertToDatabaseColumn(ArticleTypeEnum entityValue) {
        if (entityValue == null) {
            return null;
        }
        return entityValue.getArticleType();
    }

    @Override
    public ArticleTypeEnum convertToEntityAttribute(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }

        return ArticleTypeEnum.get(databaseValue);
    }
}
