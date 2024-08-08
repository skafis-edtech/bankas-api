package lt.skafis.bankas.model

import lt.skafis.bankas.dto.SourceDisplayDto
import org.threeten.bp.Instant

data class Source(
    override var id: String = "",
    val name: String = "",
    val description: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val reviewHistory: String = "",
    val authorId: String = "",
    val createdOn: String = Instant.now().toString(),
    val lastModifiedOn: String = Instant.now().toString()
): Identifiable {
    fun toDisplayDto(
        authorUsername: String = "",
    ): SourceDisplayDto {
        return SourceDisplayDto(
            id = this.id,
            name = this.name,
            description = this.description,
            reviewStatus = this.reviewStatus,
            reviewHistory = this.reviewHistory,
            authorUsername = authorUsername,
            createdOn = this.createdOn,
            lastModifiedOn = this.lastModifiedOn
        )
    }
}