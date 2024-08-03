package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryListDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Problem

interface SortService {
    fun getMySortedProblems(): List<ProblemDisplayViewDto>
    fun getMyUnsortedProblems(): List<ProblemDisplayViewDto>
    fun sortProblem(problemId: String, categories: List<String>): Problem
    fun getNotMySortedProblems(): List<ProblemDisplayViewDto>
    fun getNotMyUnsortedProblems(): List<ProblemDisplayViewDto>
}