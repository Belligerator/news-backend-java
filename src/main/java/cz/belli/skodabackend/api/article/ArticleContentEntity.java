package cz.belli.skodabackend.api.article;

import cz.belli.skodabackend.api.article.ArticleEntity;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@DynamicInsert  // only non-null columns get referenced in the prepared sql statement
@Table(name="article_content")
public class ArticleContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "article_id", nullable = false, insertable = false, updatable = false)
    private Integer articleId;

    @Column(nullable = false)
    private LanguageEnum language;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String body;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "date_of_publication", columnDefinition = "datetime NOT NULL", nullable = false)
    private Date dateOfPublication;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private ArticleEntity article;

}
