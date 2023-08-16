package cz.belli.skodabackend.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

@Setter
@Getter
public class ArticleRequestDTO {

    @NotNull(message = "Title is required.")
    private Map<String, String> title;

    @NotNull(message = "Body is required.")
    private Map<String, String> body;

    /**
     * Id of the parent article.
     */
    private Integer parent;

    /**
     * Stringified array of tag ids.
     */
    @NotNull(message = "Tags are required.")
    private String tags;

    /**
     * Date when the article is publicated. If not present, current date is used.
     */
    private Date dateOfPublication;

    /**
     * Cover image of the article.
     */
    private String coverImage;

}
