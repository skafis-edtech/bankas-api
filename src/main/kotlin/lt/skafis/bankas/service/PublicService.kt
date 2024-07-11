package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Source

interface PublicService {
    fun getProblemsCount(): Long
    fun getCategoriesCount(): Long
    fun getProblemsByCategory(categoryId: String): List<ProblemDisplayViewDto>
    fun getCategoryById(categoryId: String): Category
    fun getCategories(): List<Category>
    fun getProblemById(problemId: String): ProblemDisplayViewDto
    fun getSourceById(sourceId: String): Source
    fun getSourcesByAuthor(authorUsername: String): List<Source>
}