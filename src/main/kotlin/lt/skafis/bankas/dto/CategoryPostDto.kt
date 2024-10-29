package lt.skafis.bankas.dto

import lt.skafis.bankas.model.Visibility

data class CategoryPostDto(
    val name: String,
    val description: String,
    val visibility: Visibility,
)
