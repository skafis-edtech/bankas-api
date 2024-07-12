package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.config.RequiresRoleAtLeast
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.service.SortService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sort")
@Tag(name = "Sort Controller", description = "USER and ADMIN")
@SecurityRequirement(name = "bearerAuth")
@Logged
class SortController {

    @Autowired
    private lateinit var sortService: SortService

    @GetMapping("/mySortedProblems")
    @Operation(
        summary = "USER. Get all problems sorted by author",
        description = "Get all problems sorted by author. Returns a list of problems.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun getSortedProblems(): ResponseEntity<List<ProblemDisplayViewDto>> {
        return ResponseEntity.ok(sortService.getMySortedProblems())
    }

    @GetMapping("/myUnsortedProblems")
    @Operation(
        summary = "USER. Get all problems unsorted",
        description = "Get all problems unsorted. Returns a list of problems.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun getUnsortedProblems(): ResponseEntity<List<ProblemDisplayViewDto>> {
        return ResponseEntity.ok(sortService.getMyUnsortedProblems())
    }

    @PatchMapping("/sortMy/{problemId}/{categoryId}")
    @Operation(
        summary = "USER. Sort a problem into a category",
        description = "Sort a problem into a category. Returns problem firestore entity.",
    )
    @RequiresRoleAtLeast(Role.USER)
    fun sortProblem(@PathVariable problemId: String, @PathVariable categoryId: String): ResponseEntity<Problem> {
        return ResponseEntity.ok(sortService.sortMyProblem(problemId, categoryId))
    }

    @GetMapping("/notMySortedProblems")
    @Operation(
        summary = "ADMIN. Get all problems sorted by !author",
        description = "Get all problems sorted by !author. Returns a list of problems.",
    )
    @RequiresRoleAtLeast(Role.ADMIN)
    fun getNotMySortedProblems(): ResponseEntity<List<ProblemDisplayViewDto>> {
        return ResponseEntity.ok(sortService.getNotMySortedProblems())
    }

    @GetMapping("/notMyUnsortedProblems")
    @Operation(
        summary = "ADMIN. Get all problems unsorted by !author",
        description = "Get all problems unsorted by !author. Returns a list of problems.",
    )
    @RequiresRoleAtLeast(Role.ADMIN)
    fun getNotMyUnsortedProblems(): ResponseEntity<List<ProblemDisplayViewDto>> {
        return ResponseEntity.ok(sortService.getNotMyUnsortedProblems())
    }

    @PatchMapping("/sortNotMy/{problemId}/{categoryId}")
    @Operation(
        summary = "ADMIN. Sort a not owned problem into a category",
        description = "Sort a problem into a category. Returns problem firestore entity.",
    )
    @RequiresRoleAtLeast(Role.ADMIN)
    fun sortNotMyProblem(@PathVariable problemId: String, @PathVariable categoryId: String): ResponseEntity<Problem> {
        return ResponseEntity.ok(sortService.sortNotMyProblem(problemId, categoryId))
    }
}