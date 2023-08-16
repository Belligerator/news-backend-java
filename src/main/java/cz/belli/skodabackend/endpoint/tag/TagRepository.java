package cz.belli.skodabackend.endpoint.tag;

import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, TagEntityId> {

    @Query("SELECT tag FROM TagEntity tag WHERE tag.id IN :ids")
    List<TagEntity> findAllByIdIn(@Param("ids") List<String> ids);

    List<TagEntity> findAllByLanguageOrderByOrderAsc(LanguageEnum language);

    /**
     * Delete tag by id and language.
     * Use query to avoid exception when deleting non existing tag.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM TagEntity tag WHERE tag.id = :id AND tag.language = :language")
    void deleteById(String id, LanguageEnum language);

}
