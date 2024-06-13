package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.dto.ProblemViewDto
import lt.skafis.bankas.service.ProblemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@RestController
@RequestMapping("/api/problems")
class ProblemController(
    private val problemService: ProblemService
) {

    @GetMapping("/{id}")
    fun getProblem(@PathVariable id: String): ResponseEntity<ProblemDisplayViewDto> =
        ResponseEntity.ok(problemService.getProblemById(id))

    @GetMapping("/byCategory/{categoryId}")
    fun getProblemsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProblemDisplayViewDto>> =
        ResponseEntity.ok(problemService.getProblemsByCategoryId(categoryId))

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    fun createProblem(@RequestBody problem: ProblemPostDto,
                      @RequestParam("problemImageFile") problemImageFile: MultipartFile?,
                      @RequestParam("answerImageFile") answerImageFile: MultipartFile?,
                      principal: Principal): ResponseEntity<ProblemViewDto> =
        ResponseEntity(problemService.createProblem(problem, principal.name, problemImageFile, answerImageFile), HttpStatus.CREATED)

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    fun updateProblem(@PathVariable id: String,
                      @RequestBody problem: ProblemPostDto,
                      @RequestParam("problemImageFile") problemImageFile: MultipartFile?,
                      @RequestParam("answerImageFile") answerImageFile: MultipartFile?,
                      principal: Principal): ResponseEntity<ProblemViewDto> =
        ResponseEntity.ok(problemService.updateProblem(id, problem, principal.name, problemImageFile, answerImageFile))

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    fun deleteProblem(@PathVariable id: String, principal: Principal): ResponseEntity<Void> =
        if (problemService.deleteProblem(id, principal.name)) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    @GetMapping("/count")
    fun getProblemsCount(): ResponseEntity<CountDto> =
        ResponseEntity.ok(problemService.getProblemCount())
}
