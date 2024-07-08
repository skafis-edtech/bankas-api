package lt.skafis.bankas.dto

import lt.skafis.bankas.model.ReviewStatus
import org.threeten.bp.Instant

data class SourcePostDto(
    val name: String = "",
    val description: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val reviewedBy: String = "",
    val reviewedOn: String = "",
    val reviewMessage: String = "",
    val author: String = "",
    val createdOn: String = Instant.now().toString(),
    val lastModifiedOn: String = Instant.now().toString(),
)
