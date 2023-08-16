package cz.belli.skodabackend.model.dto;

import cz.belli.skodabackend.endpoint.tag.TagEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDTO {

    /**
     * Unique string shortcut for tag.
     *
     * @example world for World news.
     */
    private String id;

    /**
     * What language is the tag available in?
     *
     * @example en
     */
    private String language;

    /**
     * User-friendly name of the tag.
     *
     * @example World
     */
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

}
