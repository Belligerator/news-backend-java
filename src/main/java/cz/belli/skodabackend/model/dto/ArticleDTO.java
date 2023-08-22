package cz.belli.skodabackend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.belli.skodabackend.api.article.ArticleContentEntity;
import cz.belli.skodabackend.api.tag.TagEntity;
import cz.belli.skodabackend.service.Utils;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ArticleDTO {

    private Integer articleContentId;

    @NotNull(message = "Title is required.")
    private String title;

    @NotNull(message = "Body is required.")
    private String body;

    /**
     * Content (title, body) language.
     *
     * @example cs
     */
    private String language;

    private String articleType;

    /**
     * Id of the parent article.
     */
    private Integer parent;

    private Boolean active;

    /**
     * Cover image of the article.
     */
    private String coverImage;

    /**
     * Date when the article was published or updated.
     */
    private Date dateOfPublication;

    /**
     * What tags are associated with this article. Useful for filtering.
     */
    private List<TagDTO> tags;

    /**
     * Tags in string format. When coming from form.
     */
    @JsonIgnore
    private String updatedTags;

    public ArticleDTO() {
    }
    public ArticleDTO(ArticleContentEntity articleContent) {
        this.articleContentId = articleContent.getId();
        this.title = articleContent.getTitle();
        this.dateOfPublication = articleContent.getDateOfPublication();

        this.articleType = articleContent.getArticle().getArticleType().getArticleType();
        this.language = articleContent.getLanguage().getLanguage();
        this.body = articleContent.getBody();
        this.active = articleContent.getArticle().getActive();
        this.coverImage = articleContent.getCoverImage();
        this.tags = new ArrayList<>();

        if (articleContent.getArticle() != null) {
            List<TagEntity> tagEntities = articleContent.getArticle().getTags()
                    .stream()
                    .filter(tag -> tag.getLanguage().getLanguage().equals(this.language))
                    .collect(Collectors.toList());
            this.tags = Utils.convertEntitiesToDTOs(tagEntities, TagDTO.class);
        }

    }

    public static List<ArticleDTO> createDtosFromEntities(List<ArticleContentEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(ArticleDTO::createDtoFromEntity)
                .collect(Collectors.toList());
    }

    public static ArticleDTO createDtoFromEntity(ArticleContentEntity entity) {
        return new ArticleDTO(entity);
    }
}
