package lt.skafis.bankas.category

import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.repository.FirestoreCategoryRepository

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryRepository: FirestoreCategoryRepository
) {

    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: String): CategoryViewDto? {
        return categoryRepository.getCategoryById(id)
    }

    @PostMapping
    fun createCategory(@RequestBody category: CategoryViewDto): String {
        return categoryRepository.createCategory(category)
    }

    @PutMapping
    fun updateCategory(@RequestBody category: CategoryViewDto): String {
        val updated = categoryRepository.updateCategory(category)
        return if (updated) "Update successful" else "Update failed"
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: String): String {
        val deleted = categoryRepository.deleteCategory(id)
        return if (deleted) "Delete successful" else "Delete failed"
    }

    @GetMapping
    fun getAllCategories(): List<CategoryViewDto> {
        return categoryRepository.getAllCategories()
    }
}
