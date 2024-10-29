package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryDisplayDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Category

interface CategoryViewService {
    fun getProblemsByCategoryShuffle(
        categoryId: String,
        page: Int,
        size: Int,
        seed: Long,
        allSourcesExcept: List<String>,
        onlySources: List<String>,
    ): List<ProblemDisplayViewDto>

    fun getCategoryById(categoryId: String): Category

    fun getCategories(
        page: Int,
        size: Int,
        search: String,
        allSourcesExcept: List<String>,
        onlySources: List<String>,
    ): List<CategoryDisplayDto>
}
