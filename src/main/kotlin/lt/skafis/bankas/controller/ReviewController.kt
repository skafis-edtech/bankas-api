package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ReviewMsgDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.service.ReviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/review")
@Tag(
    name = "Review Controller",
    description = "ADMIN. Everything starting with given source made public ending with SUPER_ADMIN giving credits (not yet).",
)
@SecurityRequirement(name = "bearerAuth")
@RequiresRoleAtLeast(Role.ADMIN)
@Logged
class ReviewController {
    @Autowired
    private lateinit var reviewService: ReviewService

    @GetMapping("/pendingSources")
    @Operation(
        summary = "Get pending sources",
        description = "Requests from REVIEW page.",
    )
    fun getPendingSources(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
    ): ResponseEntity<List<SourceDisplayDto>> = ResponseEntity.ok(reviewService.getPendingSources(page, size, search))

    @GetMapping("/problemsBySource/{sourceId}")
    @Operation(
        summary = "Get pending source problems.",
        description = "Requests from REVIEW page.",
    )
    fun getProblemsBySource(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @PathVariable sourceId: String,
    ): ResponseEntity<List<ProblemDisplayViewDto>> {
        val problems = reviewService.getProblemsBySource(sourceId, page, size)
        return ResponseEntity(problems, HttpStatus.OK)
    }

    @PatchMapping("/approve/{sourceId}")
    @Operation(
        summary = "Approve source with problems",
        description = "Requests from REVIEW page.",
    )
    fun approve(
        @PathVariable sourceId: String,
        @RequestBody reviewMsgDto: ReviewMsgDto,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(reviewService.approve(sourceId, reviewMsgDto.reviewMessage))

    @PatchMapping("/reject/{sourceId}")
    @Operation(
        summary = "ADMIN. Reject source with problems",
        description = "Reject source with problems by source ID.",
    )
    fun reject(
        @PathVariable sourceId: String,
        @RequestBody reviewMsgDto: ReviewMsgDto,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(reviewService.reject(sourceId, reviewMsgDto.reviewMessage))
}
