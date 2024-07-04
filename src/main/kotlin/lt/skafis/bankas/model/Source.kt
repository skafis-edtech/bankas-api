package lt.skafis.bankas.model

import lt.skafis.bankas.modelOld.ReviewStatus
import org.threeten.bp.Instant

data class Source(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val reviewedBy: String = "",
    val reviewedOn: String = "",
    val reviewMessage: String = "",
    val createdOn: String = Instant.now().toString(),
    val lastModifiedOn: String = Instant.now().toString()
)