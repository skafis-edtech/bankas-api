package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dto.CategoryViewDto

interface CategoryService {
    fun getCategoryById(id: String): CategoryViewDto
    fun createCategory(category: CategoryPostDto): CategoryViewDto
    fun updateCategory(id: String, category: CategoryPostDto): CategoryViewDto
    fun deleteCategoryWithProblems(id: String): String
    fun getAllCategories(): List<CategoryViewDto>
}