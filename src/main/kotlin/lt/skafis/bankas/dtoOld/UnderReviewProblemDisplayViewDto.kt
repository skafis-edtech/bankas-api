package lt.skafis.bankas.dtoOld

import lt.skafis.bankas.model.ReviewStatus

data class UnderReviewProblemDisplayViewDto(
    val id: String = "",
    val problemText: String = "",
    val answerText: String = "",
    val problemImageSrc: String = "",
    val answerImageSrc: String = "",
    val categoryId: String = "",
    val author: String = "",
    val createdOn: String = "",
    val lastModifiedOn: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val rejectedBy: String = "",
    val rejectedOn: String = "",
    val rejectionMessage: String = "",
)
