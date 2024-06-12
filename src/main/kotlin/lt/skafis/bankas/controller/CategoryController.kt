package lt.skafis.bankas.controller

import lt.skafis.bankas.dto.CategoryPostDto
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService,
) {

    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: String): ResponseEntity<CategoryViewDto?> =
        ResponseEntity.ok(categoryService.getCategoryById(id))

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryViewDto>> =
        ResponseEntity.ok(categoryService.getAllCategories())

    @PostMapping
    fun createCategory(@RequestBody category: CategoryPostDto): ResponseEntity<CategoryViewDto> =
        ResponseEntity(categoryService.createCategory(category), HttpStatus.CREATED)

    @PutMapping("/{id}")
    fun updateCategory( @PathVariable id: String, @RequestBody category: CategoryPostDto): ResponseEntity<CategoryViewDto> =
        ResponseEntity.ok(categoryService.updateCategory(id, category))

    @DeleteMapping("/{id}/cascade")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<Void> =
    if (categoryService.deleteCategoryWithProblems(id)) {
        ResponseEntity(HttpStatus.NO_CONTENT)
    } else {
        ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/count")
    fun getCategoriesCount(): ResponseEntity<CountDto> =
        ResponseEntity.ok(categoryService.getCategoriesCount())

}
