package cz.belli.skodabackend.config;

import cz.belli.skodabackend.converter.ArticleTypeStringToEnumConverter;
import cz.belli.skodabackend.converter.LanguageStringToEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * This bean is used for validation of method parameters.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ArticleTypeStringToEnumConverter());
        registry.addConverter(new LanguageStringToEnumConverter());
    }

}