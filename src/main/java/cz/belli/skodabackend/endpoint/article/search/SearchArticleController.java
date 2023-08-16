package cz.belli.skodabackend.endpoint.article.search;

import cz.belli.skodabackend.model.dto.ArticleDTO;
import cz.belli.skodabackend.model.dto.ArticleShortDTO;
import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/api/articles/search")
public class SearchArticleController {

    private final SearchArticleService searchArticleService;

    public SearchArticleController(SearchArticleService searchArticleService) {
        this.searchArticleService = searchArticleService;
    }

    /**
     * This API is used for searching articles by pattern in title or body.
     * @param pattern   Pattern to search.
     * @param language  Language of the article.
     * @param page      Pagination page.
     * @param count     Number of articles per page.
     * @return          List of articles as list of ArticleDto.
     */
    @GetMapping(path = "{articleType}")
    public List<ArticleDTO> searchArticle(
            @PathVariable(name = "articleType") ArticleTypeEnum articleType,
            @RequestParam(name = "pattern") String pattern,
            @RequestParam(name = "page", defaultValue = "1") @Valid @Min(1)  int page,
            @RequestParam(name = "count", defaultValue = "10") @Valid @Min(1) int count,
            @RequestHeader(name = "x-language", defaultValue = "EN") LanguageEnum language
    ) {
        return this.searchArticleService.searchArticle(articleType, pattern, page, count, language);
    }

    /**
     * This method is used for searching articles by pattern in title or body for autocomplete.
     *
     * @param articleType   Type of the article.
     * @param pattern       Pattern to search.
     * @param language      Language of the article.
     * @return              List of articles as list of ArticleDto. Max 10 articles.
     */
    @GetMapping(path = "autocomplete/{articleType}")
    public List<ArticleShortDTO> searchArticleAutocomplete(
            @PathVariable(name = "articleType") ArticleTypeEnum articleType,
            @RequestParam(name = "pattern") String pattern,
            @RequestHeader(name = "x-language", defaultValue = "EN") LanguageEnum language
    ) {
        return this.searchArticleService.searchArticleAutocomplete(articleType, pattern, language);
    }
}
