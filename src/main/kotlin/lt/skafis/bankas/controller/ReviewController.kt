package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.service.ApprovalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/review")
@Tag(
    name = "Review Controller",
    description = "ADMIN. Everything starting with given source made public ending with superadmin giving credits (not yet)",
)
@SecurityRequirement(name = "bearerAuth")
@Logged
class ReviewController {
    @Autowired
    private lateinit var approvalService: ApprovalService

    @GetMapping("/pendingSources")
    @Operation(
        summary = "ADMIN. Get pending sources",
        description = "Get pending sources submitted for approval.",
    )
    @RequiresRoleAtLeast(Role.ADMIN)
    fun getPendingSources(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
    ): ResponseEntity<List<SourceDisplayDto>> = ResponseEntity.ok(approvalService.getPendingSources(page, size, search))

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
}
