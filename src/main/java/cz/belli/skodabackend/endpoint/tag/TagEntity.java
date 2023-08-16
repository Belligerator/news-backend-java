package cz.belli.skodabackend.endpoint.tag;

import com.fasterxml.jackson.annotation.JsonBackReference;
import cz.belli.skodabackend.endpoint.article.ArticleEntity;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(TagEntityId.class)
@Getter
@Setter
@DynamicInsert  // only non-null columns get referenced in the prepared sql statement
@Table(name="tag")
public class TagEntity {

    @Id
    @Column()
    private String id;

    @Id
    @Column()
    private LanguageEnum language;

    @Column(nullable = false)
    private String title;

    /**
     * Order of the tag in the list of tags.
     * Name "order" is reserved in SQL, so we have to use backticks.
     */
    @ColumnDefault("10")
    @Column(nullable = false, columnDefinition = "INT NOT NULL DEFAULT 10", name = "`order`")
    private Integer order;

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    List<ArticleEntity> articles;

}
