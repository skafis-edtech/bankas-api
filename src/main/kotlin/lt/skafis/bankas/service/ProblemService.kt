package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.model.Problem
import org.springframework.web.multipart.MultipartFile

interface ProblemService {
    fun getPublicProblemById(id: String): ProblemDisplayViewDto
    fun getPublicProblemsByCategoryId(categoryId: String): List<ProblemDisplayViewDto>
    //Unimplemented stuff

    //OLD STUFF
    fun createProblem(problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): Problem
    fun updateProblem(id: String, problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): Problem
    fun deleteProblem(id: String, userId: String): Boolean
    fun getProblemCount(): CountDto
}