package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.dto.IdDto
import lt.skafis.bankas.dto.SourceWithProblemsSubmitDto
import lt.skafis.bankas.service.ApprovalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/approval")
@Tag(name = "Approval Controller", description = "USER and ADMIN")
@SecurityRequirement(name = "bearerAuth")
class ApprovalController {

    @Autowired
    private lateinit var approvalService: ApprovalService

    @PostMapping("/submitSourceWithProblems", consumes = ["multipart/form-data"])
    @Operation(
        summary = "User submits source info with problem list",
        description = "Name image files with GUIDs. Include them in the imageFilename fields of the problems. Responds with created source ID."
    )
    fun submitSourceWithProblems(
        @RequestPart("sourceData") sourceData: SourceWithProblemsSubmitDto,
        @RequestPart("problemImageFiles") problemImageFiles: List<MultipartFile>,
        @RequestPart("answerImageFiles") answerImageFiles: List<MultipartFile>,
    ): ResponseEntity<IdDto> {
        val sourceId = approvalService.submitSourceWithProblems(sourceData, problemImageFiles, answerImageFiles)
        return ResponseEntity(IdDto(sourceId), HttpStatus.CREATED)
    }

}