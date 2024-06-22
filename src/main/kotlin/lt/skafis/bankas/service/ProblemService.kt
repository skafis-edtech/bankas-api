package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.UnderReviewProblemDisplayViewDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.UnderReviewProblem
import org.springframework.web.multipart.MultipartFile

interface ProblemService {
    fun getPublicProblemById(id: String): Problem
    fun getPublicProblemBySkfCode(skfCode: String): ProblemDisplayViewDto
    fun getPublicProblemsByCategoryId(categoryId: String): List<ProblemDisplayViewDto>
    fun getPublicProblemCount(): CountDto
    fun submitProblem(problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): UnderReviewProblem
    fun getAllUnderReviewProblems(userId: String): List<UnderReviewProblemDisplayViewDto>
    fun approveProblem(id: String, userId: String): Problem
    fun getAllUnderReviewProblemsForAuthor(userId: String): List<UnderReviewProblemDisplayViewDto>
    fun getAllApprovedProblemsForAuthor(userId: String): List<ProblemDisplayViewDto>
    fun rejectProblem(id: String, rejectMsg: String, userId: String): UnderReviewProblem
    //OLD STUFF
    fun updateProblem(id: String, problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): Problem
    fun deleteProblem(id: String, userId: String): Boolean
}