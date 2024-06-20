package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.dto.CategoriesForAuthor
import lt.skafis.bankas.dto.CategoryPostDto
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.RejectMsgDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.UnderReviewCategory
import lt.skafis.bankas.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.security.Principal

@RestController
@RequestMapping("/category")
class CategoryController(
    private val categoryService: CategoryService,
) {

    @GetMapping("/{id}")
    fun getPublicCategory(@PathVariable id: String): ResponseEntity<Category?> =
        ResponseEntity.ok(categoryService.getPublicCategoryById(id))

    @GetMapping
    fun getAllPublicCategories(): ResponseEntity<List<Category>> =
        ResponseEntity.ok(categoryService.getAllPublicCategories())

    @GetMapping("/count")
    fun getPublicCategoriesCount(): ResponseEntity<CountDto> =
        ResponseEntity.ok(categoryService.getPublicCategoriesCount())

    @PostMapping("/submit")
    @Operation(
        summary = "Works"
    )
    @SecurityRequirement(name = "bearerAuth")
    fun submitCategory(@RequestBody category: CategoryPostDto, principal: Principal): ResponseEntity<UnderReviewCategory> =
        ResponseEntity(categoryService.submitCategory(category, principal.name), HttpStatus.CREATED)

    @GetMapping("/underReview")
    @Operation(
        summary = "Works"
    )
    @SecurityRequirement(name = "bearerAuth")
    fun getSubmittedCategories(principal: Principal): ResponseEntity<List<UnderReviewCategory>> =
        ResponseEntity.ok(categoryService.getAllUnderReviewCategories(principal.name))

    @PostMapping("/{id}/approve")
    @Operation(
        summary = "Works"
    )
    @SecurityRequirement(name = "bearerAuth")
    fun approveCategory(@PathVariable id: String, principal: Principal): ResponseEntity<Category> =
        ResponseEntity.ok(categoryService.approveCategory(id, principal.name))

    @GetMapping("/myAllSubmitted")
    @Operation(
        summary = "Works"
    )
    @SecurityRequirement(name = "bearerAuth")
    fun getMyAllSubmittedCategories(principal: Principal): ResponseEntity<CategoriesForAuthor> =
        ResponseEntity.ok(CategoriesForAuthor(
            categoryService.getAllMySubmittedCategories(principal.name),
            categoryService.getAllMyApprovedCategories(principal.name)
        ))

    @PatchMapping("/{id}/reject")
    @Operation(
        summary = "In progress"
    )
    @SecurityRequirement(name = "bearerAuth")
    fun rejectCategory(@PathVariable id: String, @RequestBody rejectMsgDto: RejectMsgDto, principal: Principal): ResponseEntity<UnderReviewCategory> =
        ResponseEntity.ok(categoryService.rejectCategory(
            id,
            rejectMsg = rejectMsgDto.rejectionMessage,
            userId = principal.name
        ))

    //OLD STUFF ------------------------------------------------------------------------------------------------------
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    fun createCategory(@RequestBody category: CategoryPostDto, principal: Principal): ResponseEntity<Category> =
        ResponseEntity(categoryService.createCategory(category, principal.name), HttpStatus.CREATED)

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    fun updateCategory(@PathVariable id: String, @RequestBody category: CategoryPostDto, principal: Principal): ResponseEntity<Category> =
        ResponseEntity.ok(categoryService.updateCategory(id, category, principal.name))

    @DeleteMapping("/{id}/cascade")
    @SecurityRequirement(name = "bearerAuth")
    fun deleteCategory(@PathVariable id: String, principal: Principal): ResponseEntity<Void> =
    if (categoryService.deleteCategoryWithProblems(id, principal.name)) {
        ResponseEntity(HttpStatus.NO_CONTENT)
    } else {
        ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    }


}
