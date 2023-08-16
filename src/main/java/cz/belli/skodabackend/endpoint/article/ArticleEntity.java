package cz.belli.skodabackend.endpoint.article;


import com.fasterxml.jackson.annotation.JsonBackReference;
import cz.belli.skodabackend.endpoint.tag.TagEntity;
import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicInsert
@Table(name = "article")
public class ArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Integer id;

    @Column(name = "article_type", nullable = false)
    private ArticleTypeEnum articleType;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean active;

    @Column(insertable = false, updatable = false)
    private Integer parent;

    @Column(columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Date timestamp;

    @OneToMany(mappedBy = "article")
    @JsonBackReference
    private List<ArticleContentEntity> articleContents;

    @OneToMany(mappedBy = "parentArticle")
    @JsonBackReference
    private List<ArticleEntity> childrenArticles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", referencedColumnName = "id")
    @JsonBackReference
    private ArticleEntity parentArticle;

    @ManyToMany
    @JsonBackReference
    @JoinTable(
            name = "article__tag",
            joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "tag_id", referencedColumnName = "id"),
                    @JoinColumn(name = "tag_language", referencedColumnName = "language")
            }
    )
    List<TagEntity> tags;

}
