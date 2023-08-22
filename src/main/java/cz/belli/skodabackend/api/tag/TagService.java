package cz.belli.skodabackend.api.tag;

import cz.belli.skodabackend.model.dto.TagDTO;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {

    private final EntityManager entityManager;
    private final TagRepository tagRepository;


    /**
     * Create new tag if not exists and return created tag. If tag already exists, throw exception.
     *
     * @param tag   New tag.
     * @return      Created tag.
     */
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public TagDTO createTag(TagDTO tag) {
        TagEntityId tagEntityId = new TagEntityId(tag.getId(), LanguageEnum.get(tag.getLanguage()));
        TagEntity tagEntity = this.tagRepository.findById(tagEntityId).orElse(null);

        if (tagEntity != null) {
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
    @Cacheable("tags")
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
    @CacheEvict(value = "tags", allEntries = true)
    public TagDTO updateTag(TagDTO tag) {
        TagEntityId tagEntityId = new TagEntityId(tag.getId(), LanguageEnum.get(tag.getLanguage()));
        TagEntity tagEntity = this.tagRepository.findById(tagEntityId).orElse(null);

        if (tagEntity == null) {
            throw new ExtendedResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found.");
        }

        tagEntity.setTitle(tag.getTitle());
        if (tag.getOrder() != null) {
            tagEntity.setOrder(tag.getOrder());
        }

        this.tagRepository.save(tagEntity);

        return new TagDTO(tagEntity);
    }

    /**
     * Delete tag.
     *
     * @param tag   Tag to delete.
     */
    @CacheEvict(value = "articles", allEntries = true)
    public void deleteTag(TagDTO tag) {
        this.tagRepository.deleteById(tag.getId(), LanguageEnum.get(tag.getLanguage()));
    }
}
