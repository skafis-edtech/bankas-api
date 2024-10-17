package lt.skafis.bankas.dto

data class CategoryDisplayDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val problemCount: Int = 0,
    val isPrivate: Boolean = false,
)
