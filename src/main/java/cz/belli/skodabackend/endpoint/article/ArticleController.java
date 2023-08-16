package cz.belli.skodabackend.endpoint.article;

import cz.belli.skodabackend.model.dto.ArticleDTO;
import cz.belli.skodabackend.model.dto.ArticleRequestDTO;
import cz.belli.skodabackend.model.enumeration.ArticleTypeEnum;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import cz.belli.skodabackend.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/api/articles")
public class ArticleController {

    private final ArticleService articlesService;
    private final FileService fileService;

    public ArticleController(
            ArticleService articlesService,
            FileService fileService
    ) {
        this.articlesService = articlesService;
        this.fileService = fileService;
    }

    /**
     * API accepts ArticleRequestDto from request. For each language creates new ArticleContent.
     *
     * @throws ExtendedResponseStatusException 400 BAD_REQUEST - Error while storing file, if file is not image.
     * @throws ExtendedResponseStatusException 400 BAD_REQUEST - Tag with given id does not exist.
     * @throws ExtendedResponseStatusException 400 BAD_REQUEST - Title or body is empty for some language.
     * @param articleType   Type of article to create.
     * @param articleDto    Article data.
     * @param uploadedFile  Uploaded cover image.
     */
    @PostMapping(path = "{articleType}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public void createArticle(
            @PathVariable(name = "articleType") ArticleTypeEnum articleType,
            @ModelAttribute @Valid ArticleRequestDTO articleDto,
            @RequestPart(name = "file", required = false) MultipartFile uploadedFile
    ) {

        String newFileName = this.fileService.storeFile(uploadedFile);

        if (newFileName != null) {
            // We want '/' as separator to be stored in database.
            articleDto.setCoverImage(FileService.UPLOADS_FOLDER + '/' + newFileName);
        }

        try {
            this.articlesService.createArticle(articleType, articleDto);
        } catch (Exception e) {
            // If article creation fails, delete uploaded file.
            if (newFileName != null) {
                this.fileService.deleteFile(newFileName);
            }
            throw e;
        }
    }

    /**
     * API updates article with given id.
     * If cover image is present, update it, if not, delete old one.
     * If tags are present, update them, if not, remove all tags from article.
     *
     * @throws ExtendedResponseStatusException 400 BAD_REQUEST - Error while storing file, if file is not image.
     * @throws ExtendedResponseStatusException 400 BAD_REQUEST - Tag with given id does not exist.
     * @throws ExtendedResponseStatusException 400 BAD_REQUEST - Title or body is empty.
     * @param articleContentId  Id of article to update.
     * @param articleDto        Article data.
     * @param uploadedFile      Uploaded cover image.
     * @return                  Updated article as ArticleDto.
     */
    @PutMapping(path = "{articleContentId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ArticleDTO updateArticle(
            @PathVariable(name = "articleContentId") int articleContentId,
            @ModelAttribute @Valid ArticleDTO articleDto,
            @RequestPart(name = "file", required = false) MultipartFile uploadedFile
    ) {

        String newFileName = this.fileService.storeFile(uploadedFile);

        if (newFileName != null) {
            // We want '/' as separator to be stored in database.
            articleDto.setCoverImage(FileService.UPLOADS_FOLDER + '/' + newFileName);
        }

        try {
            return this.articlesService.updateArticle(articleContentId, articleDto);
        } catch (Exception e) {
            // If article creation fails, delete uploaded file.
            if (newFileName != null) {
                this.fileService.deleteFile(newFileName);
            }
            throw e;
        }
    }

    /**
     * This API is used for updating article activity. Articles cannot be deleted, only deactivated.
     * Articles can be deactivated also via updateArticleById. But this API is for quick deactivation via administration.
     *
     * @param articleContentId  Id of article to update.
     * @param active            If true, article will be active, if false, article will be inactive.
     */
    @PutMapping(path = "{articleContentId}/activity")
    public void updateArticleActivity(
            @PathVariable(name = "articleContentId") int articleContentId,
            @RequestParam(name = "active") boolean active
    ) {
        this.articlesService.updateArticleActivity(articleContentId, active);
    }

    /**
     * API returns article content with given id.
     *
     * @throws ExtendedResponseStatusException 404 NOT_FOUND - Article with given id does not exist.
     * @param articleContentId  Id of article to get.
     */
    @GetMapping(path = "detail/{articleContentId}")
    public ArticleDTO getArticleDetail(@PathVariable(name = "articleContentId") int articleContentId) {
        return this.articlesService.getArticleDetail(articleContentId);
    }

    /**
     * Get articles by type and another filters.
     *
     * @param articleType   Type of article.
     * @param language      Language of article.
     * @param page          Page number.
     * @param count         Count of articles per page.
     * @param active        If true, return only active articles.
     * @param tagId         If not null, return only articles with this tag.
     * @return              List of articles as list of ArticleDto.
     */
    @GetMapping(path = "{articleType}")
    public List<ArticleDTO> getArticlesByType(
            @PathVariable(name = "articleType") ArticleTypeEnum articleType,
            @RequestParam(defaultValue = "1", name = "page") @Valid @Min(1) int page,
            @RequestParam(defaultValue = "20", name = "count") @Valid @Min(1) int count,
            @RequestParam(defaultValue = "true", name = "active") boolean active,
            @RequestParam(name = "tagId", required = false) String tagId,
            @RequestHeader(defaultValue = "EN", name = "x-language") LanguageEnum language
    ) {
        return this.articlesService.getArticlesByTypeAndFilter(articleType, language, page, count, active, tagId);
    }
}
