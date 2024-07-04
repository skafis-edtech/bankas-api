package lt.skafis.bankas.dtoOld

data class ProblemDisplayViewDto(
    val id: String = "",
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
