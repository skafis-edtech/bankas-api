package lt.skafis.bankas.serviceOld

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dtoOld.CountDto
import lt.skafis.bankas.modelOld.Category
import lt.skafis.bankas.modelOld.UnderReviewCategory

interface CategoryService {
    fun getPublicCategoryById(id: String): Category
    fun getAllPublicCategories(): List<Category>
    fun getPublicCategoriesCount(): CountDto
    fun submitCategory(category: CategoryPostDto, userId: String): UnderReviewCategory
    fun getAllUnderReviewCategories(userId: String): List<UnderReviewCategory>
    fun approveCategory(id: String, userId: String): Category
    fun getAllMySubmittedCategories(userId: String): List<UnderReviewCategory>
    fun getAllMyApprovedCategories(userId: String): List<Category>
    fun rejectCategory(id: String, rejectMsg: String, userId: String): UnderReviewCategory
    fun updateMyUnderReviewCategory(id: String, category: CategoryPostDto, userId: String): UnderReviewCategory
    fun deleteUnderReviewCategoryWithUnderReviewProblems(id: String, userId: String): Boolean
}