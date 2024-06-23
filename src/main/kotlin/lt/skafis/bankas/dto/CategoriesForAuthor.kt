package lt.skafis.bankas.dto

import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.UnderReviewCategory

data class CategoriesForAuthor(
    val underReviewCategories: List<UnderReviewCategory>,
    val approvedCategories: List<Category>
)