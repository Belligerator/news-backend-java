package cz.belli.skodabackend.api.article;

import cz.belli.skodabackend.api.pushnotification.PushNotificationService;
import cz.belli.skodabackend.api.tag.TagEntity;
import cz.belli.skodabackend.api.tag.TagEntity_;
import cz.belli.skodabackend.model.dto.ArticleDTO;
import cz.belli.skodabackend.model.dto.ArticleRequestDTO;
import cz.belli.skodabackend.model.dto.TagDTO;
import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import cz.belli.skodabackend.api.tag.TagRepository;
import cz.belli.skodabackend.service.EmailService;
import cz.belli.skodabackend.service.FileService;
import cz.belli.skodabackend.service.SentryService;
import cz.belli.skodabackend.service.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleContentService {

    private final ArticleContentRepository articleContentRepository;
    private final FileService fileService;
    private final TagRepository tagRepository;
    private final EntityManager entityManager;
    private final PushNotificationService pushNotificationService;
    private final EmailService emailService;

    /**
     * Get article detail by articleContentId.
     *
     * @param articleContentId Id of article content.
     */
    @Cacheable("articles")
    public ArticleDTO getArticleDetail(int articleContentId) {
        ArticleContentEntity articleContentEntity = this.articleContentRepository.getArticleDetailDto(articleContentId);
        if (articleContentEntity == null) {
            SentryService.captureMessage("Article content (" + articleContentId + ") not found.");
            throw new ExtendedResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Article content not found.",
                    "Article content (" + articleContentId + ") not found."
            );
        }
        return ArticleDTO.createDtoFromEntity(articleContentEntity);
    }

    /**
     * Create new article.
     *
     * @param articleType   Type of article.
     * @param newArticleDto Article details.
     */
    @CacheEvict(value = { "articles", "search" }, allEntries = true)
    public void createArticle(ArticleTypeEnum articleType, ArticleRequestDTO newArticleDto) {
        log.info("Creating Article=" + newArticleDto.getTitle());

        // Title is mandatory, we will take from it what languages are in the request.
        Set<String> languages = newArticleDto.getTitle().keySet();

        // Create ArticleEntity.
        ArticleEntity newArticleEntity = new ArticleEntity();
        newArticleEntity.setArticleType(articleType);
        newArticleEntity.setParent(newArticleDto.getParent());

        // Parse tags from articleDto.
        List<String> tagsIds = new ArrayList<>();

        try {
            // Parse tag ids from request from JSON.
            Utils.stringToJsonObject(newArticleDto.getTags(), TagDTO.class)
                    .forEach(tagDto -> tagsIds.add(((TagDTO) tagDto).getId()));
        } catch (ExtendedResponseStatusException e) {
            log.error("Cannot parse tags from request: " + newArticleDto.getTags(), e);

            throw new ExtendedResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot parse tags from request. " + newArticleDto.getTags()
            );
        }

        // Get tags from DB by tagsIds.
        List<TagEntity> tags = this.tagRepository.findAllByIdIn(tagsIds);
        newArticleEntity.setTags(tags);

        // Create new article content for each language.
        List<ArticleContentEntity> articleContentEntities = new ArrayList<>();

        for (String language : languages) {

            String title = newArticleDto.getTitle().get(language);
            String body = newArticleDto.getBody().get(language);

            // Check if title and body are not null for this language.
            if (title == null) {
                throw new ExtendedResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Missing mandatory parameter(s): title for language " + language + ".");
            }

            if (body == null) {
                throw new ExtendedResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Missing mandatory parameter(s): body for language " + language + ".");
            }

            ArticleContentEntity articleContentEntity = new ArticleContentEntity();
            articleContentEntity.setArticle(newArticleEntity);
            articleContentEntity.setLanguage(LanguageEnum.get(language));
            articleContentEntity.setTitle(title);
            articleContentEntity.setBody(body);
            articleContentEntity.setCoverImage(newArticleDto.getCoverImage());

            // If date of publication is not set, set it to current date.
            if (newArticleDto.getDateOfPublication() != null) {
                articleContentEntity.setDateOfPublication(newArticleDto.getDateOfPublication());
            } else {
                articleContentEntity.setDateOfPublication(new Date());
            }

            articleContentEntities.add(articleContentEntity);
        }

        // Save article content.
        this.articleContentRepository.saveAll(articleContentEntities);
        log.info("Article saved: " + newArticleDto.getTitle());

        // Send push notification to topic for every language.
        articleContentEntities.forEach(articleContentEntity -> {
            this.pushNotificationService.sendPushNotificationToTopic(articleContentEntity, articleContentEntity.getLanguage());
        });

        // Send email about new article.
        // This feature is just for testing purposes, so pick first language.
        this.emailService.sendNewArticleEmail(articleContentEntities.get(0));
    }

    /**
     * Update article. If cover image is present, update it, if not, delete old one.
     * If tags are present, update them, if not, remove all tags from article.
     *
     * @param articleContentId Id of article content to update.
     * @param updatedArticle   Updated article data.
     * @return Updated article as ArticleDto.
     */
    @CacheEvict(value = { "articles", "search" }, allEntries = true)
    public ArticleDTO updateArticle(int articleContentId, ArticleDTO updatedArticle) {
        ArticleContentEntity articleContentEntity = this.articleContentRepository.findById(articleContentId).orElse(null);

        if (articleContentEntity == null) {
            throw new ExtendedResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Article content not found.",
                    "Article content with id " + articleContentId + " not found."
            );
        }

        // Update article content.
        articleContentEntity.setTitle(updatedArticle.getTitle());
        articleContentEntity.setBody(updatedArticle.getBody());
        articleContentEntity.getArticle().setParent(updatedArticle.getParent());
        boolean active = updatedArticle.getActive() != null ? updatedArticle.getActive() : articleContentEntity.getArticle().getActive();
        articleContentEntity.getArticle().setActive(active);

        // If date of publication is not set, set it to current date.
        if (updatedArticle.getDateOfPublication() != null) {
            articleContentEntity.setDateOfPublication(updatedArticle.getDateOfPublication());
        } else {
            articleContentEntity.setDateOfPublication(new Date());
        }

        if (updatedArticle.getUpdatedTags() != null && !updatedArticle.getUpdatedTags().isEmpty()) {
            // Update tags.
            List<String> tagsIds = new ArrayList<>();

            try {
                // Parse tag ids from request from JSON.
                Utils.stringToJsonObject(updatedArticle.getUpdatedTags(), TagDTO.class)
                        .forEach(tagDto -> tagsIds.add(((TagDTO) tagDto).getId()));
            } catch (ExtendedResponseStatusException e) {
                log.error("Cannot parse tags from request: " + updatedArticle.getUpdatedTags(), e);

                throw new ExtendedResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot parse tags from request. " + updatedArticle.getTags()
                );
            }

            // Get tags from DB by tagsIds.
            List<TagEntity> tags = this.tagRepository.findAllByIdIn(tagsIds);
            articleContentEntity.getArticle().setTags(tags);

        } else {
            // If tags are not present, remove all tags from article.
            articleContentEntity.getArticle().setTags(new ArrayList<>());
        }

        String coverImageToDelete = null;

        // If cover image is present, update it.
        if (updatedArticle.getCoverImage() != null) {
            // If cover image is updated, delete old one.
            coverImageToDelete = articleContentEntity.getCoverImage();

            // New cover image.
            articleContentEntity.setCoverImage(updatedArticle.getCoverImage());

        } else if (articleContentEntity.getCoverImage() != null) {
            // If cover image is not present, delete old one.
            coverImageToDelete = articleContentEntity.getCoverImage();

            // New cover image.
            articleContentEntity.setCoverImage(null);
        }

        // Save article content.
        ArticleContentEntity newArticleContentEntity = this.articleContentRepository.save(articleContentEntity);

        // New entity is saved, we can remove old cover image.
        if (coverImageToDelete != null) {
            this.fileService.deleteFile(coverImageToDelete);
        }

        return ArticleDTO.createDtoFromEntity(newArticleContentEntity);
    }

    /**
     * This API is used for updating article activity. Articles cannot be deleted, only deactivated.
     * Articles can be deactivated also via updateArticleById. But this API is for quick deactivation via administration.
     *
     * @param articleContentId Id of article to update.
     * @param active           If true, article will be activated, if false, article will be deactivated.
     */
    @CacheEvict(value = { "articles", "search" }, allEntries = true)
    public void updateArticleActivity(int articleContentId, boolean active) {
        ArticleContentEntity articleContentEntity = this.articleContentRepository.findById(articleContentId).orElse(null);

        if (articleContentEntity == null) {
            throw new ExtendedResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Article content not found.",
                    "Article content with id " + articleContentId + " not found."
            );
        }

        articleContentEntity.getArticle().setActive(active);
        this.articleContentRepository.save(articleContentEntity);

    }

    /**
     * Get articles by type and another filters.
     * Use CriteriaBuilder so we can add filters (conditions) dynamically.
     *
     * @param articleType Type of article.
     * @param language    Language of article.
     * @param page        Page number.
     * @param count       Count of articles per page.
     * @param active      If true, return only active articles.
     * @param tagId       If not null, return only articles with this tag.
     * @return List of articles as list of ArticleDto.
     */
    @Cacheable("articles")
    public List<ArticleDTO> getArticlesByTypeAndFilter(
            ArticleTypeEnum articleType,
            LanguageEnum language,
            int page,
            int count,
            boolean active,
            String tagId
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ArticleContentEntity> cq = cb.createQuery(ArticleContentEntity.class);

        Root<ArticleContentEntity> content = cq.from(ArticleContentEntity.class);

        // Join article. Use fetch to get all data in one query.
        Fetch<ArticleContentEntity, ArticleEntity> fetchArticle = content.fetch(ArticleContentEntity_.ARTICLE, JoinType.LEFT);
        Join<ArticleContentEntity, ArticleEntity> article = (Join<ArticleContentEntity, ArticleEntity>) fetchArticle;

        // Join tags. Use fetch to get all data in one query.
        Fetch<ArticleEntity, TagEntity> fetchTags = article.fetch(ArticleEntity_.TAGS, JoinType.LEFT);
        Join<ArticleEntity, TagEntity> tags = (Join<ArticleEntity, TagEntity>) fetchTags;

        // Create list of where predicates.
        List<Predicate> wherePredicates = new ArrayList<>();

        // Add conditions to wherePredicates.
        wherePredicates.add(cb.equal(content.get(ArticleContentEntity_.LANGUAGE), language));
        wherePredicates.add(cb.equal(article.get(ArticleEntity_.ARTICLE_TYPE), articleType));

        if (active) {
            wherePredicates.add(cb.equal(article.get(ArticleEntity_.ACTIVE), true));
        }

        if (tagId != null) {
            wherePredicates.add(cb.equal(tags.get(TagEntity_.LANGUAGE), language));
            wherePredicates.add(cb.equal(tags.get(TagEntity_.ID), tagId));
        }

        cq.where(
                wherePredicates.toArray(Predicate[]::new)
        ).orderBy(
                cb.desc(content.get(ArticleContentEntity_.DATE_OF_PUBLICATION))
        );

        List<ArticleContentEntity> articleContentEntities = entityManager
                .createQuery(cq)
                .setFirstResult(page < 1 ? 0 : (page - 1) * count)
                .setMaxResults(count)
                .getResultList();

        return ArticleDTO.createDtosFromEntities(articleContentEntities);
    }

}
