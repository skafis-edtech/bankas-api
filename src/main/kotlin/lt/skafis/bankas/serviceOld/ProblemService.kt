package lt.skafis.bankas.serviceOld

import lt.skafis.bankas.dtoOld.CountDto
import lt.skafis.bankas.dtoOld.ProblemDisplayViewDto
import lt.skafis.bankas.dtoOld.ProblemPostDtoOld
import lt.skafis.bankas.dtoOld.UnderReviewProblemDisplayViewDto
import lt.skafis.bankas.modelOld.Problem
import lt.skafis.bankas.modelOld.UnderReviewProblem
import org.springframework.web.multipart.MultipartFile

interface ProblemService {
    fun getPublicProblemById(id: String): Problem
    fun getPublicProblemBySkfCode(skfCode: String): ProblemDisplayViewDto
    fun getPublicProblemsByCategoryId(categoryId: String): List<ProblemDisplayViewDto>
    fun getPublicProblemCount(): CountDto
    fun submitProblem(problem: ProblemPostDtoOld, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): UnderReviewProblem
    fun approveProblem(id: String, userId: String): Problem
    fun getAllUnderReviewProblemsForAuthor(userId: String): List<UnderReviewProblemDisplayViewDto>
    fun getAllApprovedProblemsForAuthor(userId: String): List<ProblemDisplayViewDto>
    fun rejectProblem(id: String, rejectMsg: String, userId: String): UnderReviewProblem
    fun updateMyUnderReviewProblem(id: String, problem: ProblemPostDtoOld, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): UnderReviewProblem
    fun deleteMyUnderReviewProblem(id: String, userId: String): Boolean
    fun getUnderReviewProblemsByArbitraryCategory(categoryId: String, userId: String): List<UnderReviewProblemDisplayViewDto>
}