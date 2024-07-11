package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.service.PublicService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public")
@Tag(name = "Public Controller", description = "PUBLIC")
@SecurityRequirement(name = "bearerAuth")
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

    @GetMapping("/problemsByCategory/{categoryId}")
    fun getProblemsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProblemDisplayViewDto>> {
        return ResponseEntity.ok(publicService.getProblemsByCategory(categoryId))
    }

    @GetMapping("/category/{categoryId}")
    fun getCategoryById(@PathVariable categoryId: String): ResponseEntity<Category> {
        return ResponseEntity.ok(publicService.getCategoryById(categoryId))
    }

    @GetMapping("/categories")
    fun getCategories(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(publicService.getCategories())
    }

    @GetMapping("/problem/{problemId}")
    fun getProblemById(@PathVariable problemId: String): ResponseEntity<ProblemDisplayViewDto> {
        return ResponseEntity.ok(publicService.getProblemById(problemId))
    }

    @GetMapping("/source/{sourceId}")
    fun getSourceById(@PathVariable sourceId: String): ResponseEntity<Source> {
        return ResponseEntity.ok(publicService.getSourceById(sourceId))
    }

    @GetMapping("/sourcesByAuthor/{authorUsername}")
    fun getSourcesByAuthor(@PathVariable authorUsername: String): ResponseEntity<List<Source>> {
        return ResponseEntity.ok(publicService.getSourcesByAuthor(authorUsername))
    }

}