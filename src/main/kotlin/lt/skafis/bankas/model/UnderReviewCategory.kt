package lt.skafis.bankas.model

data class UnderReviewCategory(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val author: String = "",
    val createdOn: String = "",
    val lastModifiedOn: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val rejectedBy: String = "",
    val rejectedOn: String = "",
    val rejectionMessage: String = "",
)
