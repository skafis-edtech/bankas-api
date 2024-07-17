package lt.skafis.bankas.dto

data class ProblemDisplayViewDto(
    val id: String = "",
    val sourceListNr: Int = 0,
    val skfCode: String = "",
    val problemText: String = "",
    val problemImageSrc: String = "",
    val answerText: String = "",
    val answerImageSrc: String = "",
    val categoryId: String = "",
    val sourceId: String = "",
)
