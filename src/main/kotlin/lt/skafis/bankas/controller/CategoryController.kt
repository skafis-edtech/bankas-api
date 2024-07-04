package lt.skafis.bankas.controller

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/category")
class CategoryController {

    @Autowired
    private lateinit var categoryService: CategoryService

    @PostMapping
    fun createCategory(@RequestBody categoryPostDto: CategoryPostDto): ResponseEntity<Category> {
        val category = categoryService.createCategory(categoryPostDto)
        return ResponseEntity.ok(category)
    }

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> {
        val categories = categoryService.getCategories()
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: String): ResponseEntity<Category> {
        val category = categoryService.getCategoryById(id)
        return ResponseEntity.ok(category)
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: String, @RequestBody categoryPostDto: CategoryPostDto): ResponseEntity<Category> {
        val updatedCategory = categoryService.updateCategory(id, categoryPostDto)
        return ResponseEntity.ok(updatedCategory)
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<Void> {
        categoryService.deleteCategory(id)
        return ResponseEntity.ok().build()
    }
}