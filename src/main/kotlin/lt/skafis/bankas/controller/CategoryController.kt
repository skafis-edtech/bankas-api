package lt.skafis.bankas.controller

import lt.skafis.bankas.dto.CategoryPostDto
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.repository.FirestoreCategoryRepository
import lt.skafis.bankas.service.CategoryService
import lt.skafis.bankas.service.ProblemMetaService

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val problemMetaService: ProblemMetaService
) {

    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: String): CategoryViewDto? =
        categoryService.getCategoryById(id)

    @PostMapping
    fun createCategory(@RequestBody category: CategoryPostDto): CategoryViewDto =
        categoryService.createCategory(category)

    @PutMapping("/{id}")
    fun updateCategory( @PathVariable id: String, @RequestBody category: CategoryPostDto): CategoryViewDto =
        categoryService.updateCategory(id, category)

    @DeleteMapping("/{id}/cascade")
    fun deleteCategory(@PathVariable id: String): String =
        categoryService.deleteCategoryWithProblems(id)

    @GetMapping
    fun getAllCategories(): List<CategoryViewDto> =
        categoryService.getAllCategories()

}
