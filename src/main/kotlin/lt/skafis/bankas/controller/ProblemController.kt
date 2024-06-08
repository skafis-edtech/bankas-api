package lt.skafis.bankas.controller

import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.dto.ProblemViewDto
import lt.skafis.bankas.repository.FirestoreProblemRepository

@RestController
@RequestMapping("/api/problems")
class ProblemController(
    private val problemRepository: FirestoreProblemRepository
) {

    @GetMapping("/{id}")
    fun getProblem(@PathVariable id: String): ProblemViewDto? {
        return problemRepository.getProblemById(id)
    }

    @PostMapping
    fun createProblem(@RequestBody problem: ProblemViewDto): String {
        return problemRepository.createProblem(problem)
    }

    @PutMapping
    fun updateProblem(@RequestBody problem: ProblemViewDto): String {
        val updated = problemRepository.updateProblem(problem)
        return if (updated) "Update successful" else "Update failed"
    }

    @DeleteMapping("/{id}")
    fun deleteProblem(@PathVariable id: String): String {
        val deleted = problemRepository.deleteProblem(id)
        return if (deleted) "Delete successful" else "Delete failed"
    }

    @GetMapping
    fun getAllProblems(): List<ProblemViewDto> {
        return problemRepository.getAllProblems()
    }
}
