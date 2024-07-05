package lt.skafis.bankas.modelOld

import java.time.Instant

data class UnderReviewCategory(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val author: String = "",
    val createdOn: String = Instant.now().toString(),
    val lastModifiedOn: String = Instant.now().toString(),
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val rejectedBy: String = "",
    val rejectedOn: String = "",
    val rejectionMessage: String = "",
)
