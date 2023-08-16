package cz.belli.skodabackend.endpoint.tag;

import com.fasterxml.jackson.annotation.JsonBackReference;
import cz.belli.skodabackend.endpoint.article.ArticleEntity;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(TagEntityId.class)
@Getter
@Setter
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

    @Column(nullable = false, columnDefinition = "INT NOT NULL DEFAULT 10")
    private Integer order;

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    List<ArticleEntity> articles;

}
