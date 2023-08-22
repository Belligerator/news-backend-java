package cz.belli.skodabackend.api.article;

import cz.belli.skodabackend.model.dto.ArticleShortDTO;
import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleContentRepository extends JpaRepository<ArticleContentEntity, Integer> {

    // Use fetch join to get all related entities in one query.
    // Use left join to get also articles without tags.
    @Query("SELECT content FROM ArticleContentEntity content " +
            "JOIN FETCH content.article article " +
            "LEFT JOIN FETCH article.tags tags " +
            "WHERE content.id = :id")
    ArticleContentEntity getArticleDetailDto(@Param("id") int articleContentId);

    @Query("SELECT content FROM ArticleContentEntity content " +
            "JOIN FETCH content.article article " +
            "JOIN FETCH article.tags tags " +
            "WHERE content.language = :language " +
            "AND article.articleType = :articleType " +
            "AND article.active = true " +
            "AND (content.title LIKE %:pattern% OR content.body LIKE %:pattern%) ")
    List<ArticleContentEntity> searchArticle(
            @Param("articleType") ArticleTypeEnum articleType,
            @Param("pattern") String pattern,
            @Param("language") LanguageEnum language,
            Pageable pageable);

    @Query("SELECT new cz.belli.skodabackend.model.dto.ArticleShortDTO(content.id, content.title, content.dateOfPublication) " +
            "FROM ArticleContentEntity content " +
            "JOIN content.article article " +
            "WHERE content.language = :language " +
            "AND article.articleType = :articleType " +
            "AND article.active = true " +
            "AND (content.title LIKE %:pattern% OR content.body LIKE %:pattern%) ")
    List<ArticleShortDTO> searchArticleAutocomplete(
            @Param("articleType") ArticleTypeEnum articleType,
            @Param("pattern") String pattern,
            @Param("language") LanguageEnum language,
            Pageable pageable);

}
