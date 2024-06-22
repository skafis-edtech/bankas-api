package lt.skafis.bankas.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.model.UnderReviewProblem
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.service.ProblemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@RestController
@RequestMapping("/problem")
class ProblemController(
    private val problemService: ProblemService
) {

    @GetMapping("/{id}")
    @Operation(
        summary = "Should work. PUBLIC"
    )
    fun getPublicProblem(@PathVariable id: String): ResponseEntity<ProblemDisplayViewDto> =
        ResponseEntity.ok(problemService.getPublicProblemById(id))

    @GetMapping("/byCategory/{categoryId}")
    @Operation(
        summary = "Should work. PUBLIC"
    )
    fun getPublicProblemsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProblemDisplayViewDto>> =
        ResponseEntity.ok(problemService.getPublicProblemsByCategoryId(categoryId))

    @GetMapping("/count")
    @Operation(
        summary = "Should work. PUBLIC"
    )
    fun getPublicProblemsCount(): ResponseEntity<CountDto> =
        ResponseEntity.ok(problemService.getPublicProblemCount())

    @PostMapping("/submit")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Works. USER. Careful! Complex file and text upload logic AND not easily testable file upload!",
        description = """
            This endpoint allows uploading problem information as JSON along with optional image files.

            **Request Type**: `multipart/form-data`
            - Key `problem`: JSON string containing the problem information
            - Key `problemImageFile`: (optional) Image file associated with the problem
            - Key `answerImageFile`: (optional) Image file associated with the answer

            **Logic**:
            - If `problem.problemImageUrl` is a URL and `problemImageFile` is null, return `problemImagePath = problem.problemImage`.
            - If `problem.problemImageUrl` is "" and `problemImageFile` is provided, upload the file and return `problemImagePath = "problems/SKF-..."`.
            - If `problem.problemImageUrl` is "" and `problemImageFile` is null, return `problemImagePath = ""`.
        """
    )
    fun submitProblem(@RequestPart("problem", required = true) problemString: String,
                      @RequestPart("problemImageFile", required = false) problemImageFile: MultipartFile?,
                      @RequestPart("answerImageFile", required = false) answerImageFile: MultipartFile?,
                      principal: Principal
    ): ResponseEntity<UnderReviewProblem> {
        val problem = ObjectMapper().readValue(problemString, ProblemPostDto::class.java)
        return ResponseEntity(problemService.submitProblem(problem, principal.name, problemImageFile, answerImageFile), HttpStatus.CREATED)
    }

//    @GetMapping("/underReview")
//    @PostMapping("/{id}/approve")
//    @GetMapping("/myAllSubmitted")
//    @PatchMapping("/{id}/reject")
//    @PutMapping("/{id}/fixMyUnderReview")
//    @DeleteMapping("/underReview/{id}") - delete only your own


//
//    @PutMapping("/{id}")
//    @SecurityRequirement(name = "bearerAuth")
//    fun updateProblem(@PathVariable id: String,
//                      @RequestBody problem: ProblemPostDto,
//                      @RequestParam("problemImageFile") problemImageFile: MultipartFile?,
//                      @RequestParam("answerImageFile") answerImageFile: MultipartFile?,
//                      principal: Principal): ResponseEntity<ProblemViewDto> =
//        ResponseEntity.ok(problemService.updateProblem(id, problem, principal.name, problemImageFile, answerImageFile))
//
//    @DeleteMapping("/{id}")
//    @SecurityRequirement(name = "bearerAuth")
//    fun deleteProblem(@PathVariable id: String, principal: Principal): ResponseEntity<Void> =
//        if (problemService.deleteProblem(id, principal.name)) {
//            ResponseEntity(HttpStatus.NO_CONTENT)
//        } else {
//            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
//        }
//

}
