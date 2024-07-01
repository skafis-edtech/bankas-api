package lt.skafis.bankas.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.UnderReviewProblem
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.service.ProblemService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@RestController
@RequestMapping("/problem")
class ProblemController(
    private val problemService: ProblemService
) {

    @GetMapping("/{skfCode}")
    @Operation(summary = "PUBLIC")
    fun getPublicProblem(@PathVariable skfCode: String): ResponseEntity<ProblemDisplayViewDto> =
        ResponseEntity.ok(problemService.getPublicProblemBySkfCode(skfCode))

    @GetMapping("/byCategory/{categoryId}")
    @Operation(summary = "PUBLIC")
    fun getPublicProblemsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProblemDisplayViewDto>> =
        ResponseEntity.ok(problemService.getPublicProblemsByCategoryId(categoryId))

    @GetMapping("/count")
    @Operation(summary = "PUBLIC")
    fun getPublicProblemsCount(): ResponseEntity<CountDto> =
        ResponseEntity.ok(problemService.getPublicProblemCount())

    @PostMapping("/submit", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "USER. Careful! Complex file and text upload logic AND not easily testable file upload!",
        description = """
            **Logic**:
            - If `problem.problemImageUrl` is a URL and `problemImageFile` is null, return `problemImagePath = problem.problemImage`.
            - If `problem.problemImageUrl` is "" and `problemImageFile` is provided, upload the file and return `problemImagePath = "problems/SKF-..."`.
            - If `problem.problemImageUrl` is "" and `problemImageFile` is null, return `problemImagePath = ""`.
        """,
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [
                Content(
                    mediaType = "multipart/form-data"
                )
            ]
        )
    )
    fun submitProblem(@RequestPart("problem") problem: ProblemPostDto,
                      @RequestPart(value = "problemImageFile", required = false) problemImageFile: MultipartFile?,
                      @RequestPart(value = "answerImageFile", required = false) answerImageFile: MultipartFile?,
                      principal: Principal
    ): ResponseEntity<UnderReviewProblem> {
        return ResponseEntity(problemService.submitProblem(problem, principal.name, problemImageFile, answerImageFile), HttpStatus.CREATED)
    }

    @GetMapping("/underReview")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "ADMIN")
    fun getAllUnderReviewProblems(principal: Principal): ResponseEntity<List<UnderReviewProblemDisplayViewDto>> =
        ResponseEntity.ok(problemService.getAllUnderReviewProblems(principal.name))

    @PostMapping("/{id}/approve")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "ADMIN")
    fun approveProblem(@PathVariable id: String, principal: Principal): ResponseEntity<Problem> =
        ResponseEntity.ok(problemService.approveProblem(id, principal.name))

    @GetMapping("/myAllSubmitted")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "USER")
    fun getMyAllSubmittedProblems(principal: Principal): ResponseEntity<ProblemsForAuthor> =
        ResponseEntity.ok(
            ProblemsForAuthor(
            problemService.getAllUnderReviewProblemsForAuthor(principal.name),
            problemService.getAllApprovedProblemsForAuthor(principal.name)
        ))

    @PatchMapping("/{id}/reject")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "ADMIN")
    fun rejectProblem(
        @PathVariable id: String,
        @RequestBody rejectMsgDto: RejectMsgDto,
        principal: Principal
    ): ResponseEntity<UnderReviewProblem> =
        ResponseEntity.ok(problemService.rejectProblem(
            id,
            rejectMsg = rejectMsgDto.rejectionMessage,
            userId = principal.name
        ))

    @PutMapping("/{id}/fixMyUnderReview")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "USER")
    fun fixMyUnderReviewProblem(
        @PathVariable id: String,
        @RequestPart("problem", required = true) problem: ProblemPostDto,
        @RequestPart("problemImageFile", required = false) problemImageFile: MultipartFile?,
        @RequestPart("answerImageFile", required = false) answerImageFile: MultipartFile?,
        principal: Principal
    ): ResponseEntity<UnderReviewProblem> =
        ResponseEntity.ok(problemService.updateMyUnderReviewProblem(
            id,
            problem,//ObjectMapper().readValue(problemString, ProblemPostDto::class.java),
            principal.name,
            problemImageFile,
            answerImageFile,
        ))

    @DeleteMapping("/underReview/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "USER")
    fun deleteUnderReviewProblem(@PathVariable id: String, principal: Principal): ResponseEntity<Void> =
        if (problemService.deleteMyUnderReviewProblem(id, principal.name)) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }

}
