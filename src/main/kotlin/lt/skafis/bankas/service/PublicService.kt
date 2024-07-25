package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Source

interface PublicService {
    fun getProblemsCount(): Long
    fun getCategoriesCount(): Long
    fun getCategoryProblemCount(categoryId: String): Long
    fun getProblemsByCategoryShuffle(categoryId: String): List<ProblemDisplayViewDto>
    fun getCategoryById(categoryId: String): Category
    fun getCategories(): List<Category>
    fun getProblemBySkfCode(skfCode: String): ProblemDisplayViewDto
    fun getSourceById(sourceId: String): SourceDisplayDto
    fun getSourcesByAuthor(authorUsername: String): List<Source>
}