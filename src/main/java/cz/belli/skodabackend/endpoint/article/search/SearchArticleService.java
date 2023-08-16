package cz.belli.skodabackend.endpoint.article.search;

import cz.belli.skodabackend.endpoint.article.ArticleContentEntity;
import cz.belli.skodabackend.model.dto.ArticleDTO;
import cz.belli.skodabackend.model.dto.ArticleShortDTO;
import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.endpoint.article.ArticleContentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchArticleService {

    ArticleContentRepository articleContentRepository;

    public SearchArticleService(ArticleContentRepository articleContentRepository) {
        this.articleContentRepository = articleContentRepository;
    }

    /**
     * This method is used for searching articles by pattern in title or body.
     * @param pattern   Pattern to search.
     * @param language  Language of the article.
     * @param page      Pagination page.
     * @param count     Number of articles per page.
     * @return          List of articles as list of ArticleDto.
     */
    public List<ArticleDTO> searchArticle(ArticleTypeEnum articleType, String pattern, int page, int count, LanguageEnum language) {
        // Page is indexed from 0. But request is indexed from 1.
        Pageable pageable = PageRequest.of(page - 1, count, Sort.Direction.DESC, "dateOfPublication");

        List<ArticleContentEntity> articleContentEntities =
                this.articleContentRepository.searchArticle(articleType, pattern, language, pageable);
        return ArticleDTO.createDtosFromEntities(articleContentEntities);
    }

    /**
     * This method is used for searching articles by pattern in title or body for autocomplete.
     *
     * @param articleType   Type of the article.
     * @param pattern       Pattern to search.
     * @param language      Language of the article.
     * @return              List of articles as list of ArticleDto. Max 10 articles.
     */
    public List<ArticleShortDTO> searchArticleAutocomplete(ArticleTypeEnum articleType, String pattern, LanguageEnum language) {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "dateOfPublication");
        return this.articleContentRepository.searchArticleAutocomplete(articleType, pattern, language, pageable);
    }
}
