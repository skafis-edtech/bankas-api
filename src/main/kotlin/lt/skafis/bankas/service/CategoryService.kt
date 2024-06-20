package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.UnderReviewCategory

interface CategoryService {
    fun getPublicCategoryById(id: String): Category
    fun getAllPublicCategories(): List<Category>
    fun getPublicCategoriesCount(): CountDto
    fun submitCategory(category: CategoryPostDto, userId: String): UnderReviewCategory
    fun getAllUnderReviewCategories(userId: String): List<UnderReviewCategory>
    fun approveCategory(id: String, userId: String): Category
    fun getAllMySubmittedCategories(userId: String): List<UnderReviewCategory>
    fun getAllMyApprovedCategories(userId: String): List<Category>
    //OLD STUFF ------------------------------------------------------------------------------------------------------
    fun createCategory(category: CategoryPostDto, userId: String): Category
    fun updateCategory(id: String, category: CategoryPostDto, userId: String): Category
    fun deleteCategoryWithProblems(id: String, userId: String): Boolean
}