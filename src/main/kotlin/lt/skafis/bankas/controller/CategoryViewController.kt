package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.dto.CategoryDisplayDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.service.ViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categoryView")
@Tag(name = "Category View Controller", description = "PUBLIC")
@SecurityRequirement(name = "bearerAuth")
@Logged
class CategoryViewController {
    @Autowired
    private lateinit var viewService: ViewService

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
}
