package cz.belli.skodabackend.model.dto;

import cz.belli.skodabackend.endpoint.article.ArticleContentEntity;
import cz.belli.skodabackend.endpoint.tag.TagEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TagDTO {

    /**
     * Unique string shortcut for tag.
     *
     * @example world for World news.
     */
    @NotNull(message = "Id is required.")
    private String id;

    /**
     * What language is the tag available in?
     *
     * @example en
     */
    @NotNull(message = "Language is required.")
    private String language;

    /**
     * User-friendly name of the tag.
     *
     * @example World
     */
    @NotNull(message = "Title is required.")
    private String title;

    /**
     * Order of the tag.
     */
    private Integer order;

    public TagDTO() {
    }

    public TagDTO(TagEntity tagEntity) {
        this.id = tagEntity.getId();
        this.language = tagEntity.getLanguage().getLanguage();
        this.title = tagEntity.getTitle();
        this.order = tagEntity.getOrder();
    }

    public static List<TagDTO> createDtosFromEntities(List<TagEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(TagDTO::createDtoFromEntity)
                .collect(Collectors.toList());
    }

    public static TagDTO createDtoFromEntity(TagEntity entity) {
        return new TagDTO(entity);
    }
}
