package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.dto.CountDto

interface CategoryService {
    fun getCategoryById(id: String): CategoryViewDto
    fun createCategory(category: CategoryPostDto): CategoryViewDto
    fun updateCategory(id: String, category: CategoryPostDto): CategoryViewDto
    fun deleteCategoryWithProblems(id: String): Boolean
    fun getAllCategories(): List<CategoryViewDto>
    fun getCategoriesCount(): CountDto
}