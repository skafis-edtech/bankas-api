package lt.skafis.bankas.controller

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemPostDto
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.dto.ProblemViewDto
import lt.skafis.bankas.service.ProblemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/problems")
class ProblemController(
    private val problemService: ProblemService
) {

    @GetMapping("/{id}")
    fun getProblem(@PathVariable id: String): ResponseEntity<ProblemViewDto?> =
        ResponseEntity.ok(problemService.getProblemById(id))

    @GetMapping("/{categoryId}")
    fun getProblemsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProblemViewDto>> =
        ResponseEntity.ok(problemService.getProblemsByCategoryId(categoryId))

    @PostMapping
    fun createProblem(@RequestBody problem: ProblemPostDto): ResponseEntity<ProblemViewDto> =
        ResponseEntity(problemService.createProblem(problem), HttpStatus.CREATED)

    @PutMapping("/{id}")
    fun updateProblem(@PathVariable id: String, @RequestBody problem: ProblemPostDto): ResponseEntity<ProblemViewDto> =
        ResponseEntity.ok(problemService.updateProblem(id, problem))

    @DeleteMapping("/{id}")
    fun deleteProblem(@PathVariable id: String): ResponseEntity<Void> =
        if (problemService.deleteProblem(id)) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    @GetMapping("/count")
    fun getProblemsCount(): ResponseEntity<CountDto> =
        ResponseEntity.ok(problemService.getProblemCount())
}
