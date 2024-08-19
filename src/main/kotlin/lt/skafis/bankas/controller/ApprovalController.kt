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
import lt.skafis.bankas.service.SourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/approval")
@Tag(name = "Approval Controller", description = "USER and ADMIN")
@SecurityRequirement(name = "bearerAuth")
@Logged
class ApprovalController {
    @Autowired
    private lateinit var approvalService: ApprovalService

    @Autowired
    private lateinit var sourceService: SourceService

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
        description = "Doesn't work from swagger... responds with 415. Submit problem data with images for approval. Returns the ID of the created problem.",
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
    fun getMySources(): ResponseEntity<List<SourceDisplayDto>> {
        val sources = approvalService.getMySources()
        return ResponseEntity(sources, HttpStatus.OK)
    }

    @GetMapping("/problemsBySource/{sourceId}")
    @Operation(
        summary = "Either USER with it's problems, or ADMIN, or PUBLIC && source.reviewStatus === ReviewStatus.APPROVED.",
        description = "Get all problems submitted for the source.",
    )
    fun getProblemsBySource(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @PathVariable sourceId: String,
    ): ResponseEntity<List<ProblemDisplayViewDto>> {
        val problems = approvalService.getProblemsBySource(sourceId, page, size)
        return ResponseEntity(problems, HttpStatus.OK)
    }

    @PatchMapping("/approve/{sourceId}")
    @Operation(
        summary = "ADMIN. Approve source with problems",
        description = "Approve source with problems by source ID.",
    )
    @RequiresRoleAtLeast(Role.ADMIN)
    fun approve(
        @PathVariable sourceId: String,
        @RequestBody reviewMsgDto: ReviewMsgDto,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(approvalService.approve(sourceId, reviewMsgDto.reviewMessage))

    @GetMapping("/sources")
    @Operation(
        summary = "ADMIN. Get all sources",
        description = "Get all sources submitted for approval (or already approved).",
    )
    @RequiresRoleAtLeast(Role.ADMIN)
    fun getSources(): ResponseEntity<List<SourceDisplayDto>> = ResponseEntity.ok(approvalService.getSources())

    @PatchMapping("/reject/{sourceId}")
    @Operation(
        summary = "ADMIN. Reject source with problems",
        description = "Reject source with problems by source ID.",
    )
    @RequiresRoleAtLeast(Role.ADMIN)
    fun reject(
        @PathVariable sourceId: String,
        @RequestBody reviewMsgDto: ReviewMsgDto,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(approvalService.reject(sourceId, reviewMsgDto.reviewMessage))

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
    fun update(
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
