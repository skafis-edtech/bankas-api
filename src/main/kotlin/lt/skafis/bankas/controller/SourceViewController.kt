package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.SortBy
import lt.skafis.bankas.service.SourceViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sourceView")
@Tag(name = "Source View Controller", description = "USER.")
@SecurityRequirement(name = "bearerAuth")
@RequiresRoleAtLeast(Role.USER)
@Logged
class SourceViewController {
    @Autowired
    private lateinit var sourceViewService: SourceViewService

    @GetMapping("/availableSources")
    @Operation(
        summary = "Get public approved sources + own sources.",
        description = "Requests from MY_PROBLEMS page.",
    )
    fun getAvailableSources(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
        @RequestParam(required = false, defaultValue = "NEWEST") sortBy: SortBy,
    ): ResponseEntity<List<SourceDisplayDto>> {
        val sources = sourceViewService.getAvailableSources(page, size, search, sortBy)
        return ResponseEntity(sources, HttpStatus.OK)
    }

    @GetMapping("/availableSource/{sourceId}")
    @Operation(
        summary = "USER - public approved + owned.",
        description = "Requests from problemInfo button and EDITING page.",
    )
    fun getSourceById(
        @PathVariable sourceId: String,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(sourceViewService.getSourceById(sourceId))

    @GetMapping("/sourcesByAuthor/{authorUsername}")
    @Operation(
        summary = "USER. Get public approved sources of user",
        description = "Requests from PROFILE page.",
    )
    fun getSourcesByAuthor(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
        @RequestParam(required = false, defaultValue = "NEWEST") sortBy: SortBy,
        @PathVariable authorUsername: String,
    ): ResponseEntity<List<SourceDisplayDto>> =
        ResponseEntity.ok(sourceViewService.getSourcesByAuthor(authorUsername, page, size, search, sortBy))

    @GetMapping("/problemsBySource/{sourceId}")
    @Operation(
        summary = "USER - public approved + owned.",
        description = "Requests from MY_PROBLEMS and EDITING and PROFILE pages.",
    )
    fun getProblemsBySource(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @PathVariable sourceId: String,
    ): ResponseEntity<List<ProblemDisplayViewDto>> {
        val problems = sourceViewService.getProblemsBySource(sourceId, page, size)
        return ResponseEntity(problems, HttpStatus.OK)
    }
}
