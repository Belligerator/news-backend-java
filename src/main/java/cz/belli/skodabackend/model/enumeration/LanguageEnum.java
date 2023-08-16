package cz.belli.skodabackend.model.enumeration;

import cz.belli.skodabackend.model.exception.WrongEnumTypeException;

import java.util.Arrays;

import static cz.belli.skodabackend.Constants.INTERNAL_SERVER_ERROR_MESSAGE;

public enum LanguageEnum {
    CS("cs"),
    EN("en");

    private String language;

    LanguageEnum(String value) {
        language = value;
    }

    public String getLanguage() {
        return language;
    }

    public static LanguageEnum get(String value) {
        return Arrays.stream(LanguageEnum.values())
                .filter(item -> item.language.equals(value))
                .findFirst()
                .orElseThrow(() -> new WrongEnumTypeException(
                        INTERNAL_SERVER_ERROR_MESSAGE,
                        "LanguageEnum.get() - wrong language type: " + value
                ));
    }


}
