package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.dto.IdDto
import lt.skafis.bankas.dto.SourceSubmitDto
import lt.skafis.bankas.service.ApprovalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.ProblemSubmitDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.Source

@RestController
@RequestMapping("/approval")
@Tag(name = "Approval Controller", description = "USER and ADMIN")
@SecurityRequirement(name = "bearerAuth")
@Logged
class ApprovalController {

    @Autowired
    private lateinit var approvalService: ApprovalService

    @PostMapping("/submit/source")
    @Operation(
        summary = "USER. Submit source data",
        description = "Submit source data for approval. Returns the ID of the created source.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun submitSourceData(@RequestBody sourceData: SourceSubmitDto): ResponseEntity<IdDto> {
        val sourceId = approvalService.submitSourceData(sourceData)
        return ResponseEntity(IdDto(sourceId), HttpStatus.CREATED)
    }

    @PostMapping("/submit/problem/{sourceId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "USER. Submit problem data with images",
        description = "Doesn't work from swagger... responds with 415. Submit problem data with images for approval. Returns the ID of the created problem.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(mediaType = "multipart/form-data")]
        )
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
    fun getMySources(): ResponseEntity<List<Source>> {
        val sources = approvalService.getMySources()
        return ResponseEntity(sources, HttpStatus.OK)
    }

    @GetMapping("/problemsBySource/{sourceId}")
    @Operation(
        summary = "Either USER with it's problems, or ADMIN, or PUBLIC && source.reviewStatus === ReviewStatus.APPROVED. Get problems by source",
        description = "Get all problems submitted for the source.",
    )
    fun getProblemsBySource(@PathVariable sourceId: String): ResponseEntity<List<ProblemDisplayViewDto>> {
        val problems = approvalService.getProblemsBySource(sourceId)
        return ResponseEntity(problems, HttpStatus.OK)
    }
}