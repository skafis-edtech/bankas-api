package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/category")
@Tag(name = "Category Controller", description = "Public - SUPER_ADMIN, private - USER")
@SecurityRequirement(name = "bearerAuth")
@RequiresRoleAtLeast(Role.SUPER_ADMIN)
@Logged
class CategoryController {
    @Autowired
    private lateinit var categoryService: CategoryService

    @PostMapping
    @RequiresRoleAtLeast(Role.SUPER_ADMIN)
    fun createPublicCategory(
        @RequestBody categoryPostDto: CategoryPostDto,
    ): ResponseEntity<Category> {
        val category = categoryService.createCategory(categoryPostDto)
        return ResponseEntity.ok(category)
    }

    @PutMapping("/{id}")
    @RequiresRoleAtLeast(Role.SUPER_ADMIN)
    fun updatePublicCategory(
        @PathVariable id: String,
        @RequestBody categoryPostDto: CategoryPostDto,
    ): ResponseEntity<Category> {
        val updatedCategory = categoryService.updateCategory(id, categoryPostDto)
        return ResponseEntity.ok(updatedCategory)
    }

    @DeleteMapping("/{id}")
    @RequiresRoleAtLeast(Role.SUPER_ADMIN)
    fun deletePublicCategory(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        categoryService.deleteCategory(id)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/private")
    @RequiresRoleAtLeast(Role.USER)
    fun createPrivateCategory(
        @RequestBody categoryPostDto: CategoryPostDto,
    ): ResponseEntity<Category> {
        val category = categoryService.createPrivateCategory(categoryPostDto)
        return ResponseEntity.ok(category)
    }

    @PutMapping("/private/{id}")
    @RequiresRoleAtLeast(Role.USER)
    fun updatePrivateCategory(
        @PathVariable id: String,
        @RequestBody categoryPostDto: CategoryPostDto,
    ): ResponseEntity<Category> {
        val updatedCategory = categoryService.updatePrivateCategory(id, categoryPostDto)
        return ResponseEntity.ok(updatedCategory)
    }

    @DeleteMapping("/private/{id}")
    @RequiresRoleAtLeast(Role.USER)
    fun deletePrivateCategory(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        categoryService.deletePrivateCategory(id)
        return ResponseEntity.ok().build()
    }
}
