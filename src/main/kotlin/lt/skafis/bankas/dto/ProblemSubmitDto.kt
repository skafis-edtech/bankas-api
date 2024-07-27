package lt.skafis.bankas.dto

data class ProblemSubmitDto(
    val sourceListNr: Int,
    val problemText: String,
    val answerText: String,
)