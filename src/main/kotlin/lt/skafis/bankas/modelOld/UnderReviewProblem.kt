package lt.skafis.bankas.modelOld

import lt.skafis.bankas.model.ReviewStatus
import java.time.Instant

data class UnderReviewProblem(
    val id: String = "",
    val problemText: String = "",
    val answerText: String = "",
    val problemImagePath: String = "",
    val answerImagePath: String = "",
    val categoryId: String = "",
    val author: String = "",
    val createdOn: String = Instant.now().toString(),
    val lastModifiedOn: String = Instant.now().toString(),
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val rejectedBy: String = "",
    val rejectedOn: String = "",
    val rejectionMessage: String = "",
)
