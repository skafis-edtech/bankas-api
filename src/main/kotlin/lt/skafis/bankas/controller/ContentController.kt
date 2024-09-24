package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.service.ApprovalService
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
@Logged
class ContentController {
    @Autowired
    private lateinit var approvalService: ApprovalService

    @PostMapping("/submit/source")
    @Operation(
        summary = "USER. Submit source data",
        description = "Submit source data for approval. Returns the ID of the created source.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun submitSourceData(
        @RequestBody sourceData: SourceSubmitDto,
    ): ResponseEntity<IdDto> {
        val sourceId = approvalService.submitSourceData(sourceData)
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
    @RequiresRoleAtLeast(Role.USER)
    fun submitProblem(
        @PathVariable sourceId: String,
        @RequestPart("problem") problem: ProblemSubmitDto,
        @RequestPart(value = "problemImageFile", required = false) problemImageFile: MultipartFile?,
        @RequestPart(value = "answerImageFile", required = false) answerImageFile: MultipartFile?,
    ): ResponseEntity<IdDto> {
        val problemId = approvalService.submitProblem(sourceId, problem, problemImageFile, answerImageFile)
        return ResponseEntity(IdDto(problemId), HttpStatus.CREATED)
    }

    @GetMapping("/mySources")
    @Operation(
        summary = "USER. Get my sources",
        description = "Get all sources submitted by the current user.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun getMySources(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
    ): ResponseEntity<List<SourceDisplayDto>> {
        val sources = approvalService.getMySources(page, size, search)
        return ResponseEntity(sources, HttpStatus.OK)
    }

    @DeleteMapping("/source/{id}")
    @Operation(
        summary = "USER but owning. Delete source with all problems",
        description = "Delete source with all problems by ID.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun deleteSource(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        approvalService.deleteSource(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/problem/{id}")
    @Operation(
        summary = "USER but owning. Delete problem",
        description = "Delete problem by ID.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun deleteProblem(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        approvalService.deleteProblem(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PutMapping("/source/{id}")
    @Operation(
        summary = "USER but owning. Update source data",
        description = "Update source data by ID.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun updateSource(
        @PathVariable id: String,
        @RequestBody sourceData: SourceSubmitDto,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(approvalService.updateSource(id, sourceData))

    @PutMapping("/problem/texts/{id}")
    @Operation(
        summary = "USER but owning. Update problem texts",
        description = "Update problem texts by ID.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun updateProblemTexts(
        @PathVariable id: String,
        @RequestBody problemTexts: ProblemTextsDto,
    ): ResponseEntity<Void> {
        approvalService.updateProblemTexts(id, problemTexts)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/problem/problemImage/{id}")
    @Operation(
        summary = "USER but owning. Delete problem image",
        description = "Delete problem image by ID.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun deleteProblemImage(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        approvalService.deleteProblemImage(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/problem/answerImage/{id}")
    @Operation(
        summary = "USER but owning. Delete answer image",
        description = "Delete answer image by ID.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun deleteAnswerImage(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        approvalService.deleteAnswerImage(id)
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
    @RequiresRoleAtLeast(Role.USER)
    fun uploadProblemImage(
        @PathVariable id: String,
        @RequestPart("problemImageFile") problemImageFile: MultipartFile,
    ): ResponseEntity<ImageSrcDto> = ResponseEntity.ok(ImageSrcDto(approvalService.uploadProblemImage(id, problemImageFile)))

    @PostMapping("/problem/answerImage/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "USER but owning. Upload answer image",
        description = "Upload answer image by ID.",
        requestBody =
            io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = [Content(mediaType = "multipart/form-data")],
            ),
    )
    @RequiresRoleAtLeast(Role.USER)
    fun uploadAnswerImage(
        @PathVariable id: String,
        @RequestPart("answerImageFile") answerImageFile: MultipartFile,
    ): ResponseEntity<ImageSrcDto> = ResponseEntity.ok(ImageSrcDto(approvalService.uploadAnswerImage(id, answerImageFile)))
}
