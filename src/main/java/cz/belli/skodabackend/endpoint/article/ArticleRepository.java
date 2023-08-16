package cz.belli.skodabackend.endpoint.article;

import cz.belli.skodabackend.endpoint.article.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {

}
