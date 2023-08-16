package cz.belli.skodabackend.endpoint.tag;

import cz.belli.skodabackend.model.dto.TagDTO;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final EntityManager entityManager;

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository, EntityManager entityManager) {
        this.tagRepository = tagRepository;
        this.entityManager = entityManager;
    }

    /**
     * Create new tag if not exists and return created tag. If tag already exists, throw exception.
     *
     * @param tag   New tag.
     * @return      Created tag.
     */
    @Transactional
    public TagDTO createTag(TagDTO tag) {
        TagEntityId tagEntityId = new TagEntityId(tag.getId(), LanguageEnum.get(tag.getLanguage()));
        Optional<TagEntity> tagEntity = this.tagRepository.findById(tagEntityId);

        if (tagEntity.isPresent()) {
            throw new ExtendedResponseStatusException(HttpStatus.CONFLICT, "Tag already exists.");
        }

        TagEntity newTagEntity = new TagEntity();
        newTagEntity.setId(tag.getId());
        newTagEntity.setLanguage(LanguageEnum.get(tag.getLanguage()));
        newTagEntity.setTitle(tag.getTitle());
        newTagEntity.setOrder(tag.getOrder());

        newTagEntity = this.tagRepository.saveAndFlush(newTagEntity);

        // Reload tag entity to get the correct order.
        this.entityManager.refresh(newTagEntity);

        return new TagDTO(newTagEntity);
    }

    /**
     * Get tag by id for specific language.
     *
     * @return      List of tags.
     */
    public List<TagDTO> getAllTags(LanguageEnum language) {
        List<TagEntity> tagEntities = this.tagRepository.findAllByLanguageOrderByOrderAsc(language);
        return TagDTO.createDtosFromEntities(tagEntities);
    }

    /**
     * Update tag and return updated tag. If tag does not exist, throw exception.
     *
     * @param tag   New tag.
     * @return      Updated tag.
     */
    public TagDTO updateTag(TagDTO tag) {
        TagEntityId tagEntityId = new TagEntityId(tag.getId(), LanguageEnum.get(tag.getLanguage()));
        Optional<TagEntity> tagEntity = this.tagRepository.findById(tagEntityId);

        if (tagEntity.isEmpty()) {
            throw new ExtendedResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found.");
        }

        TagEntity newTagEntity = tagEntity.get();
        newTagEntity.setTitle(tag.getTitle());
        if (tag.getOrder() != null) {
            newTagEntity.setOrder(tag.getOrder());
        }

        this.tagRepository.save(newTagEntity);

        return new TagDTO(newTagEntity);
    }

    /**
     * Delete tag.
     *
     * @param tag   Tag to delete.
     */
    public void deleteTag(TagDTO tag) {
        this.tagRepository.deleteById(tag.getId(), LanguageEnum.get(tag.getLanguage()));
    }
}
