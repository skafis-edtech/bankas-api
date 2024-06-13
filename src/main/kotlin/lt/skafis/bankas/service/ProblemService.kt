package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.ProblemViewDto
import org.springframework.web.multipart.MultipartFile

interface ProblemService {
    fun getProblemById(id: String): ProblemDisplayViewDto
    fun getProblemsByCategoryId(categoryId: String): List<ProblemDisplayViewDto>
    fun createProblem(problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): ProblemViewDto
    fun updateProblem(id: String, problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): ProblemViewDto
    fun deleteProblem(id: String, userId: String): Boolean
    fun getProblemCount(): CountDto
}