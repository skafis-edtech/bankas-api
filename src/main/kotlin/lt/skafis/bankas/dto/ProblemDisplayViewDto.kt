package lt.skafis.bankas.dto

import lt.skafis.bankas.model.ProblemVisibility

data class ProblemDisplayViewDto(
    val id: String = "",
    val sourceListNr: Int = 0,
    val skfCode: String = "",
    val problemText: String = "",
    val problemImageSrc: String = "",
    val answerText: String = "",
    val answerImageSrc: String = "",
    val categories: List<String> = emptyList(),
    val sourceId: String = "",
    val problemVisibility: ProblemVisibility = ProblemVisibility.HIDDEN,
)
