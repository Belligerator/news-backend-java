package cz.belli.skodabackend.model.enumeration;

import cz.belli.skodabackend.model.exception.WrongEnumTypeException;

import java.util.Arrays;

import static cz.belli.skodabackend.Constants.INTERNAL_SERVER_ERROR_MESSAGE;

public enum ArticleTypeEnum {
    NEWS("news"),
    STORY("story");

    private String articleType;

    ArticleTypeEnum(String value) {
        articleType = value;
    }

    public String getArticleType() {
        return articleType;
    }

    public static ArticleTypeEnum get(String value) {
        return Arrays.stream(ArticleTypeEnum.values())
                .filter(item -> item.articleType.equals(value))
                .findFirst()
                .orElseThrow(() -> new WrongEnumTypeException(
                        INTERNAL_SERVER_ERROR_MESSAGE,
                        "ArticleTypeEnum.get() - wrong article type: " + value
                ));
    }
}
