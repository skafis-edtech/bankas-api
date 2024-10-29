package lt.skafis.bankas.dto

import lt.skafis.bankas.model.Visibility

data class CategoryDisplayDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val problemCount: Int = 0,
    val visibility: Visibility = Visibility.PUBLIC,
)
