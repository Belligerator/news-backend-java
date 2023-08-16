package cz.belli.skodabackend.endpoint.tag;

import cz.belli.skodabackend.endpoint.tag.TagEntity;
import cz.belli.skodabackend.endpoint.tag.TagEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, TagEntityId> {

    @Query("SELECT tag FROM TagEntity tag WHERE tag.id IN :ids")
    List<TagEntity> findAllByIdIn(@Param("ids") List<String> ids);

}
