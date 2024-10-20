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
    ): List<ProblemDisplayViewDto>

    fun getCategoryById(categoryId: String): Category

    fun getCategories(
        page: Int,
        size: Int,
        search: String,
    ): List<CategoryDisplayDto>
}
