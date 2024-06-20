package lt.skafis.bankas.dto

data class ProblemDisplayViewDto(
    val skfCode: String = "",
    val problemText: String = "",
    val problemImageSrc: String = "",
    val answerText: String = "",
    val answerImageSrc: String = "",
    val categoryId: String = "",
    val author: String = "",
    val approvedBy: String = "",
    val approvedOn: String = "",
    val createdOn: String = "",
    val lastModifiedOn: String = "",
)
