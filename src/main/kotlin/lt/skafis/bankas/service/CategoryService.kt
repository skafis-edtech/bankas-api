package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category

interface CategoryService {
    fun createCategory(categoryPostDto: CategoryPostDto): Category

    fun updateCategory(
        id: String,
        categoryPostDto: CategoryPostDto,
    ): Category

    fun deleteCategory(id: String)
}
