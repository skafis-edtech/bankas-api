package lt.skafis.bankas.dto

import lt.skafis.bankas.model.ReviewStatus
import org.threeten.bp.Instant

data class SourceDisplayDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val reviewHistory: String = "",
    val authorUsername: String = "",
    val problemCount: Int = 0,
    val createdOn: String = Instant.now().toString(),
    val lastModifiedOn: String = Instant.now().toString(),
)
