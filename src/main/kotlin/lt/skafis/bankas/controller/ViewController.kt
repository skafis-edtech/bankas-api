package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.dto.CategoryDisplayDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.dto.StatsDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.service.ViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/view")
@Tag(name = "View Controller", description = "PUBLIC")
@SecurityRequirement(name = "bearerAuth")
@Logged
class ViewController {
    @Autowired
    private lateinit var viewService: ViewService

    @GetMapping("/stats")
    fun getStats(): ResponseEntity<StatsDto> = ResponseEntity.ok(StatsDto(viewService.getProblemsCount()))

    @GetMapping("/problemsByCategory/{categoryId}")
    fun getProblemsByCategory(
        @PathVariable categoryId: String,
    ): ResponseEntity<List<ProblemDisplayViewDto>> = ResponseEntity.ok(viewService.getProblemsByCategoryShuffle(categoryId))

    @GetMapping("/problemsUnsorted")
    fun getProblemsUnsorted(): ResponseEntity<List<ProblemDisplayViewDto>> = ResponseEntity.ok(viewService.getUnsortedProblems())

    @GetMapping("/category/{categoryId}")
    fun getCategoryById(
        @PathVariable categoryId: String,
    ): ResponseEntity<Category> = ResponseEntity.ok(viewService.getCategoryById(categoryId))

    @GetMapping("/categories")
    fun getCategories(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
    ): ResponseEntity<List<CategoryDisplayDto>> = ResponseEntity.ok(viewService.getCategories(page, size, search))

    @GetMapping("/problem/{skfCode}")
    fun getProblemBySkfCode(
        @PathVariable skfCode: String,
    ): ResponseEntity<ProblemDisplayViewDto> = ResponseEntity.ok(viewService.getProblemBySkfCode(skfCode))

    // Source stuff -----------------------------------------------------------------------------------------------
    @GetMapping("/source/{sourceId}")
    @Operation(
        summary = "PUBLIC if approved, USER if owned, ADMIN else.",
        description = "Get source by ID. Returns source entity.",
    )
    fun getSourceById(
        @PathVariable sourceId: String,
    ): ResponseEntity<SourceDisplayDto> = ResponseEntity.ok(viewService.getSourceById(sourceId))

    @GetMapping("/sourcesByAuthor/{authorUsername}")
    fun getSourcesByAuthor(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
        @PathVariable authorUsername: String,
    ): ResponseEntity<List<SourceDisplayDto>> = ResponseEntity.ok(viewService.getSourcesByAuthor(authorUsername, page, size, search))

    @GetMapping("/approvedSources")
    fun getApprovedSources(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
    ): ResponseEntity<List<SourceDisplayDto>> = ResponseEntity.ok(viewService.getApprovedSources(page, size, search))

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
        val problems = viewService.getProblemsBySource(sourceId, page, size)
        return ResponseEntity(problems, HttpStatus.OK)
    }
}
