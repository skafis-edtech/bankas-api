package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryDisplayDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.Category

interface ViewService {
    fun getProblemsCount(): Long

    fun getCategoryProblemCount(categoryId: String): Long

    fun getProblemsByCategoryShuffle(categoryId: String): List<ProblemDisplayViewDto>

    fun getCategoryById(categoryId: String): Category

    fun getCategories(
        page: Int,
        size: Int,
        search: String,
    ): List<CategoryDisplayDto>

    fun getProblemBySkfCode(skfCode: String): ProblemDisplayViewDto

    fun getSourceById(sourceId: String): SourceDisplayDto

    fun getSourcesByAuthor(
        authorUsername: String,
        page: Int,
        size: Int,
        search: String,
    ): List<SourceDisplayDto>

    fun getUnsortedProblems(): List<ProblemDisplayViewDto>

    fun getUnsortedProblemsCount(): Long

    fun getApprovedSources(
        page: Int,
        size: Int,
        search: String,
    ): List<SourceDisplayDto>

    fun getProblemsBySource(
        sourceId: String,
        page: Int,
        size: Int,
    ): List<ProblemDisplayViewDto>
}
