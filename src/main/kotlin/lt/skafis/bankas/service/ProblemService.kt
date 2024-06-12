package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.ProblemViewDto

interface ProblemService {
    fun getProblemById(id: String): ProblemViewDto
    fun getProblemsByCategoryId(categoryId: String): List<ProblemViewDto>
    fun createProblem(problem: ProblemPostDto): ProblemViewDto
    fun updateProblem(id: String, problem: ProblemPostDto): ProblemViewDto
    fun deleteProblem(id: String): Boolean
    fun getProblemCount(): CountDto
}