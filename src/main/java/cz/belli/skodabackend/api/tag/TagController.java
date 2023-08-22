package cz.belli.skodabackend.api.tag;

import cz.belli.skodabackend.model.dto.TagDTO;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/tags")
public class TagController {

    private final TagService tagService;

    /**
     * Create new tag if not exists.
     *
     * @param tag   New tag.
     * @return      Created tag.
     */
    @PostMapping()
    public TagDTO createTag(@RequestBody @Valid TagDTO tag) {
        return this.tagService.createTag(tag);
    }

    /**
     * Get tag by id for specific language.
     *
     * @return      List of tags.
     */
    @GetMapping()
    public List<TagDTO> getAllTags(
            @RequestHeader(value = "x-language", defaultValue = "EN") LanguageEnum language
    ) {
        return this.tagService.getAllTags(language);
    }

    /**
     * Update tag.
     *
     * @param tag   New tag.
     * @return      Updated tag.
     */
    @PutMapping()
    public TagDTO updateTag(@RequestBody @Valid TagDTO tag) {
        return this.tagService.updateTag(tag);
    }

    /**
     * Delete tag.
     *
     * @param tag   Tag to delete.
     */
    @DeleteMapping()
    public void deleteTag(@RequestBody @Valid TagDTO tag) {
        this.tagService.deleteTag(tag);
    }

}
