package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Problem

interface SortService {
    fun getSortedProblems(): List<ProblemDisplayViewDto>
    fun getUnsortedProblems(): List<ProblemDisplayViewDto>
    fun sortProblem(problemId: String, categoryId: String): Problem
}