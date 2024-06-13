package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.dto.CountDto

interface CategoryService {
    fun getCategoryById(id: String): CategoryViewDto
    fun createCategory(category: CategoryPostDto, userId: String): CategoryViewDto
    fun updateCategory(id: String, category: CategoryPostDto, userId: String): CategoryViewDto
    fun deleteCategoryWithProblems(id: String, userId: String): Boolean
    fun getAllCategories(): List<CategoryViewDto>
    fun getCategoriesCount(): CountDto
}