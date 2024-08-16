package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.websocket.server.PathParam
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.service.PublicService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public")
@Tag(name = "Public Controller", description = "PUBLIC")
@Logged
class PublicController {

    @Autowired
    private lateinit var publicService: PublicService

    @GetMapping("/problems/count")
    fun getProblemsCount(): ResponseEntity<CountDto> {
        return ResponseEntity.ok(CountDto(publicService.getProblemsCount()))
    }

    @GetMapping("/categories/count")
    fun getCategoriesCount(): ResponseEntity<CountDto> {
        return ResponseEntity.ok(CountDto(publicService.getCategoriesCount()))
    }

    @GetMapping("/categoryProblemCount/{categoryId}")
    fun getCategoryProblemCount(@PathVariable categoryId: String): ResponseEntity<CountDto> {
        return ResponseEntity.ok(CountDto(publicService.getCategoryProblemCount(categoryId)))
    }

    @GetMapping("/unsortedProblemsCount")
    fun getUnsortedProblemsCount(): ResponseEntity<CountDto> {
        return ResponseEntity.ok(CountDto(publicService.getUnsortedProblemsCount()))
    }

    @GetMapping("/problemsByCategory/{categoryId}")
    fun getProblemsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProblemDisplayViewDto>> {
        return ResponseEntity.ok(publicService.getProblemsByCategoryShuffle(categoryId))
    }

    @GetMapping("/problemsUnsorted")
    fun getProblemsUnsorted(): ResponseEntity<List<ProblemDisplayViewDto>> {
        return ResponseEntity.ok(publicService.getUnsortedProblems())
    }

    @GetMapping("/category/{categoryId}")
    fun getCategoryById(@PathVariable categoryId: String): ResponseEntity<Category> {
        return ResponseEntity.ok(publicService.getCategoryById(categoryId))
    }

    @GetMapping("/categories")
    fun getCategories(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String
    ): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(publicService.getCategories(page, size, search))
    }

    @GetMapping("/problem/{skfCode}")
    fun getProblemBySkfCode(@PathVariable skfCode: String): ResponseEntity<ProblemDisplayViewDto> {
        return ResponseEntity.ok(publicService.getProblemBySkfCode(skfCode))
    }

    @GetMapping("/source/{sourceId}")
    @Operation(
        summary = "PUBLIC if approved, USER if owned, ADMIN else",
        description = "Get source by ID. Returns source entity.",
    )
    @SecurityRequirement(name = "bearerAuth")
    fun getSourceById(@PathVariable sourceId: String): ResponseEntity<SourceDisplayDto> {
        return ResponseEntity.ok(publicService.getSourceById(sourceId))
    }

    @GetMapping("/sourcesByAuthor/{authorUsername}")
    fun getSourcesByAuthor(@PathVariable authorUsername: String): ResponseEntity<List<SourceDisplayDto>> {
        return ResponseEntity.ok(publicService.getSourcesByAuthor(authorUsername))
    }

    @GetMapping("/approvedSources")
    fun getApprovedSources(): ResponseEntity<List<SourceDisplayDto>> {
        return ResponseEntity.ok(publicService.getApprovedSources())
    }

}