package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.dto.CategoryPostDto
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.security.Principal

@RestController
@RequestMapping("/api/category")
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
    @SecurityRequirement(name = "bearerAuth")
    fun createCategory(@RequestBody category: CategoryPostDto, principal: Principal): ResponseEntity<CategoryViewDto> =
        ResponseEntity(categoryService.createCategory(category, principal.name), HttpStatus.CREATED)

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    fun updateCategory(@PathVariable id: String, @RequestBody category: CategoryPostDto, principal: Principal): ResponseEntity<CategoryViewDto> =
        ResponseEntity.ok(categoryService.updateCategory(id, category, principal.name))

    @DeleteMapping("/{id}/cascade")
    @SecurityRequirement(name = "bearerAuth")
    fun deleteCategory(@PathVariable id: String, principal: Principal): ResponseEntity<Void> =
    if (categoryService.deleteCategoryWithProblems(id, principal.name)) {
        ResponseEntity(HttpStatus.NO_CONTENT)
    } else {
        ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/count")
    fun getCategoriesCount(): ResponseEntity<CountDto> =
        ResponseEntity.ok(categoryService.getCategoriesCount())

}
