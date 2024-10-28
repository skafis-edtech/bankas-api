package lt.skafis.bankas.model

import lt.skafis.bankas.dto.CategoryDisplayDto

data class Category(
    override var id: String = "",
    val name: String = "",
    val description: String = "",
    val visibility: Visibility = Visibility.PUBLIC,
    val ownerOfPrivateId: String = "",
) : Identifiable {
    fun toDisplayDto(problemCount: Int = 0): CategoryDisplayDto =
        CategoryDisplayDto(
            id = this.id,
            name = this.name,
            description = this.description,
            visibility = this.visibility,
            problemCount = problemCount,
        )
}
