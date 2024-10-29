package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.CategoryDisplayDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.service.CategoryViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categoryView")
@Tag(name = "Category View Controller", description = "USER")
@SecurityRequirement(name = "bearerAuth")
@RequiresRoleAtLeast(Role.USER)
@Logged
class CategoryViewController {
    @Autowired
    private lateinit var categoryViewService: CategoryViewService

    @GetMapping("/problemsByCategory/{categoryId}/{seed}")
    fun getProblemsByCategory(
        @PathVariable categoryId: String,
        @PathVariable seed: Long,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") allSourcesExcept: List<String>,
        @RequestParam(required = false, defaultValue = "") onlySources: List<String>,
    ): ResponseEntity<List<ProblemDisplayViewDto>> {
        if (allSourcesExcept.isNotEmpty() && onlySources.isNotEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok(
            categoryViewService.getProblemsByCategoryShuffle(
                categoryId,
                page,
                size,
                seed,
                allSourcesExcept,
                onlySources,
            ),
        )
    }

    @GetMapping("/category/{categoryId}")
    fun getCategoryById(
        @PathVariable categoryId: String,
    ): ResponseEntity<Category> = ResponseEntity.ok(categoryViewService.getCategoryById(categoryId))

    @GetMapping("/categories")
    fun getCategories(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") search: String,
        @RequestParam(required = false, defaultValue = "") allSourcesExcept: List<String>,
        @RequestParam(required = false, defaultValue = "") onlySources: List<String>,
    ): ResponseEntity<List<CategoryDisplayDto>> {
        if (allSourcesExcept.isNotEmpty() && onlySources.isNotEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok(categoryViewService.getCategories(page, size, search, allSourcesExcept, onlySources))
    }
}
