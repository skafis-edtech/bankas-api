package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.service.ProblemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/problem")
@Tag(name = "Problem Controller", description = "SUPER_ADMIN")
@SecurityRequirement(name = "bearerAuth")
class ProblemController {

    @Autowired
    private lateinit var problemService: ProblemService

    @PostMapping
    fun createProblem(@RequestBody problemPostDto: ProblemPostDto): ResponseEntity<Problem> {
        val problem = problemService.createProblem(problemPostDto)
        return ResponseEntity.ok(problem)
    }

    @GetMapping
    fun getAllProblems(): ResponseEntity<List<Problem>> {
        val problems = problemService.getProblems()
        return ResponseEntity.ok(problems)
    }

    @GetMapping("/{id}")
    fun getProblemById(@PathVariable id: String): ResponseEntity<Problem> {
        val problem = problemService.getProblemById(id)
        return ResponseEntity.ok(problem)
    }

    @PutMapping("/{id}")
    fun updateProblem(@PathVariable id: String, @RequestBody problemPostDto: ProblemPostDto): ResponseEntity<Problem> {
        val updatedProblem = problemService.updateProblem(id, problemPostDto)
        return ResponseEntity.ok(updatedProblem)
    }

    @DeleteMapping("/{id}")
    fun deleteProblem(@PathVariable id: String): ResponseEntity<Void> {
        problemService.deleteProblem(id)
        return ResponseEntity.ok().build()
    }
}