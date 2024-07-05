package lt.skafis.bankas.dtoOld

import lt.skafis.bankas.modelOld.Category
import lt.skafis.bankas.modelOld.UnderReviewCategory

data class CategoriesForAuthor(
    val underReviewCategories: List<UnderReviewCategory>,
    val approvedCategories: List<Category>
)