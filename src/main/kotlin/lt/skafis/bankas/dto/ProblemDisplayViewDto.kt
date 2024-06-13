package lt.skafis.bankas.dto

data class ProblemDisplayViewDto(
    val id: String = "",
    val problemImageSrc: String? = null,
    val answerImageSrc: String? = null,
    val problemText: String? = null,
    val answerText: String? = null,
    val categoryId: String = "",
    val createdOn: String = "",
    )
