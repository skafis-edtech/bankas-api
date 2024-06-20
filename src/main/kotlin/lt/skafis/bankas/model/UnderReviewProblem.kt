package lt.skafis.bankas.model

data class UnderReviewProblem(
    val id: String = "",
    val problemText: String = "",
    val answerText: String = "",
    val problemImagePath: String = "",
    val answerImagePath: String = "",
    val categoryId: String = "",
    val author: String = "",
    val createdOn: String = "",
    val lastModifiedOn: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val rejectedBy: String = "",
    val rejectedOn: String = "",
    val rejectionMessage: String = "",
)
