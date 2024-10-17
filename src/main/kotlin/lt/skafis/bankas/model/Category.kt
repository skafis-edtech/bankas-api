package lt.skafis.bankas.model

import lt.skafis.bankas.dto.CategoryDisplayDto

data class Category(
    override var id: String = "",
    val name: String = "",
    val description: String = "",
    val isPrivate: Boolean = false,
    val ownerOfPrivateId: String = "",
) : Identifiable {
    fun toDisplayDto(problemCount: Int = 0): CategoryDisplayDto =
        CategoryDisplayDto(
            id = this.id,
            name = this.name,
            description = this.description,
            problemCount = problemCount,
            isPrivate = this.isPrivate,
        )
}
