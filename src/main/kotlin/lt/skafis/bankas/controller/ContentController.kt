package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.service.ContentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/content")
@Tag(name = "Content Controller", description = "USER and ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RequiresRoleAtLeast(Role.USER)
@Logged
class ContentController {
    @Autowired
    private lateinit var contentService: ContentService

    @PostMapping("/submit/source")
    @Operation(
        summary = "USER. Submit source data",
        description = "Submit source data for approval. Returns the ID of the created source.",
    )
    fun submitSourceData(
        @RequestBody sourceData: SourceSubmitDto,
    ): ResponseEntity<IdDto> {
        val sourceId = contentService.submitSourceData(sourceData)
        return ResponseEntity(IdDto(sourceId), HttpStatus.CREATED)
    }

    @PostMapping("/submit/problem/{sourceId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "USER. Submit problem data with images",
        description = "415 from swagger.... Submit problem with images for approval. returns ID of the created problem.",
        requestBody =
            io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = [Content(mediaType = "multipart/form-data")],
            ),
    )
    fun submitProblem(
        @PathVariable sourceId: String,
        @RequestPart("problem") problem: ProblemSubmitDto,
        @RequestPart(value = "problemImageFile", required = false) problemImageFile: MultipartFile?,
        @RequestPart(value = "answerImageFile", required = false) answerImageFile: MultipartFile?,
    ): ResponseEntity<IdSkfDto> {
        val problemIds = contentService.submitProblem(sourceId, problem, problemImageFile, answerImageFile)
        return ResponseEntity(problemIds, HttpStatus.CREATED)
    }

    @DeleteMapping("/source/{id}")
    @Operation(
        summary = "USER but owning. Delete source with all problems",
        description = "Delete source with all problems by ID.",
    )
    fun deleteSource(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        contentService.deleteSource(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/problem/{id}")
    @Operation(
        summary = "USER but owning. Delete problem",
        description = "Delete problem by ID.",
    )
    fun deleteProblem(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        contentService.deleteProblem(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PutMapping("/source/{id}")
    @Operation(
        summary = "USER but owning. Update source data",
        description = "Update source data by ID.",
    )
    fun updateSource(
        @PathVariable id: String,
        @RequestBody sourceData: SourceSubmitDto,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(contentService.updateSource(id, sourceData))

    @PutMapping("/problem/texts/{id}")
    @Operation(
        summary = "USER but owning. Update problem texts",
        description = "Update problem texts by ID.",
    )
    fun updateProblemTexts(
        @PathVariable id: String,
        @RequestBody problemTexts: ProblemTextsDto,
    ): ResponseEntity<Void> {
        contentService.updateProblemTexts(id, problemTexts)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/problem/problemImage/{id}")
    @Operation(
        summary = "USER but owning. Delete problem image",
        description = "Delete problem image by ID.",
    )
    fun deleteProblemImage(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        contentService.deleteProblemImage(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/problem/answerImage/{id}")
    @Operation(
        summary = "USER but owning. Delete answer image",
        description = "Delete answer image by ID.",
    )
    fun deleteAnswerImage(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        contentService.deleteAnswerImage(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/problem/problemImage/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "USER but owning. Upload problem image",
        description = "Upload problem image by ID.",
        requestBody =
            io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = [Content(mediaType = "multipart/form-data")],
            ),
    )
    fun uploadProblemImage(
        @PathVariable id: String,
        @RequestPart("problemImageFile") problemImageFile: MultipartFile,
    ): ResponseEntity<ImageSrcDto> = ResponseEntity.ok(ImageSrcDto(contentService.uploadProblemImage(id, problemImageFile)))

    @PostMapping("/problem/answerImage/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "USER but owning. Upload answer image",
        description = "Upload answer image by ID.",
        requestBody =
            io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = [Content(mediaType = "multipart/form-data")],
            ),
    )
    fun uploadAnswerImage(
        @PathVariable id: String,
        @RequestPart("answerImageFile") answerImageFile: MultipartFile,
    ): ResponseEntity<ImageSrcDto> = ResponseEntity.ok(ImageSrcDto(contentService.uploadAnswerImage(id, answerImageFile)))
}
