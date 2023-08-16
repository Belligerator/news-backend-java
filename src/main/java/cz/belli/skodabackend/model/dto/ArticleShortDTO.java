package cz.belli.skodabackend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ArticleShortDTO {

    private Integer articleContentId;

    private String title;

    /**
     * Date when the article was published or updated.
     */
    private Date dateOfPublication;

    public ArticleShortDTO() {
    }

    public ArticleShortDTO(Integer id, String title, Date dateOfPublication) {
        this.articleContentId = id;
        this.title = title;
        this.dateOfPublication = dateOfPublication;
    }

}
