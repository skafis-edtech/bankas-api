package lt.skafis.bankas.dto

data class ProblemSubmitDto(
    val sourceListNr: Int,
    val problemImageUrl: String, //should be always empty
    val answerImageUrl: String, //should be always empty
    val problemText: String,
    val answerText: String,
)